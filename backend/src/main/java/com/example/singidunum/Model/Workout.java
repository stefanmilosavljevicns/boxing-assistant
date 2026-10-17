package com.example.singidunum.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Workout")
public class Workout {
    @Id
    private String id;
    @NotBlank
    private String name;
    @NotEmpty
    private ArrayList<String> moves;
    @NotNull
    private ArrayList<Long> movesTime;
    @Positive
    private int roundNumber;
    @Min(0)
    private int roundMin;
    @Min(0) @Max(59)
    private int roundSec;
    @Min(0)
    private int restMin;
    @Min(0) @Max(59)
    private int restSec;
    @JsonProperty("prepareMin")
    @Field("preparetMin")
    @Min(0)
    private int preparetMin;
    @Min(0) @Max(59)
    private int prepareSec;

}
