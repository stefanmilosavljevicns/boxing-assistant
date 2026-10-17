package com.example.singidunum;

import com.example.singidunum.Controller.WorkoutController;
import com.example.singidunum.Model.Workout;
import com.example.singidunum.Repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutController.class)
class WorkoutControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private WorkoutRepository repository;

    @Test
    void listsNamesAndReturnsWorkoutByNameIncludingPreparationMinutes() throws Exception {
        when(repository.findDistinctNames()).thenReturn(List.of("Intervals"));
        when(repository.findByName("Intervals")).thenReturn(workout("mongo-id", "Intervals"));

        mvc.perform(get("/api/v1/getAllNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Intervals"));
        mvc.perform(get("/api/v1/getWorkout/Intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("mongo-id"))
                .andExpect(jsonPath("$.prepareMin").value(1))
                .andExpect(jsonPath("$.preparetMin").doesNotExist());
    }

    @Test
    void returnsNotFoundForMissingWorkout() throws Exception {
        mvc.perform(get("/api/v1/getWorkout/missing")).andExpect(status().isNotFound());
    }

    @Test
    void createsValidatedWorkoutWithCreatedStatus() throws Exception {
        when(repository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));
        mvc.perform(post("/api/v1/addWorkout").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Intervals"))
                .andExpect(jsonPath("$.prepareMin").value(1));
    }

    @Test
    void rejectsInvalidTimingAndMalformedJson() throws Exception {
        mvc.perform(post("/api/v1/addWorkout").contentType(MediaType.APPLICATION_JSON)
                        .content(validJson().replace("\"roundSec\":30", "\"roundSec\":90")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        mvc.perform(post("/api/v1/addWorkout").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed request body"));
    }

    @Test
    void updatesExistingWorkoutWithoutReplacingItsDatabaseIdentifier() throws Exception {
        Workout existing = workout("stable-id", "Intervals");
        when(repository.findByName("Intervals")).thenReturn(existing);
        when(repository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));
        mvc.perform(put("/api/v1/updateWorkout/Intervals").contentType(MediaType.APPLICATION_JSON)
                        .content(validJson().replace("Intervals", "Updated")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("stable-id"))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deletesByWorkoutNameAndReportsMissingRecords() throws Exception {
        Workout existing = workout("stable-id", "Intervals");
        when(repository.findByName("Intervals")).thenReturn(existing);
        mvc.perform(delete("/api/v1/deleteWorkout/Intervals")).andExpect(status().isNoContent());
        verify(repository).delete(existing);
        mvc.perform(delete("/api/v1/deleteWorkout/missing")).andExpect(status().isNotFound());
    }

    private static Workout workout(String id, String name) {
        return new Workout(id, name, new ArrayList<>(List.of("Jab")), new ArrayList<>(List.of(1000L)),
                3, 2, 30, 1, 0, 1, 15);
    }

    private static String validJson() {
        return "{\"name\":\"Intervals\",\"moves\":[\"Jab\"],\"movesTime\":[1000],"
                + "\"roundNumber\":3,\"roundMin\":2,\"roundSec\":30,\"restMin\":1,\"restSec\":0,"
                + "\"prepareMin\":1,\"prepareSec\":15}";
    }
}
