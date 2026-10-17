package com.example.singidunum.Controller;

import com.example.singidunum.Model.Workout;
import com.example.singidunum.Repository.WorkoutRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class WorkoutController {
    private final WorkoutRepository workoutRepository;

    public WorkoutController(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @GetMapping("/getAllWorkouts")
    //@RolesAllowed("admin")
    public ResponseEntity<List<Workout>> findAll() {
        return ResponseEntity.ok(workoutRepository.findAll());
    }
    @GetMapping("/getWorkout/{name}")
    //@RolesAllowed("admin")
    public ResponseEntity<Workout> findById(@PathVariable(value = "name") String id){
        Optional<Workout> userData = Optional.ofNullable(workoutRepository.findByName(id));
        return userData.map(workout -> new ResponseEntity<>(workout, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/addWorkout")
    //@RolesAllowed({"user", "admin"})
    public ResponseEntity<Workout> save(@Valid @RequestBody Workout workout){
        Workout saved = workoutRepository.save(workout);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @DeleteMapping("/deleteWorkout/{name}")
    public ResponseEntity<Void> deleteUser(@PathVariable("name") String name) {
        Workout workout = workoutRepository.findByName(name);
        if (workout == null) {
            return ResponseEntity.notFound().build();
        }
        workoutRepository.delete(workout);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllNames")
    public ResponseEntity<List<String>> findAllNames(){
        return ResponseEntity.ok(workoutRepository.findDistinctNames());
    }    

    @PutMapping("/updateWorkout/{name}")
    public ResponseEntity<Workout> updateUsers(@PathVariable("name") String name, @Valid @RequestBody Workout workout) {
        Optional<Workout> userData = Optional.ofNullable(workoutRepository.findByName(name));
        if (userData.isPresent()) {
            Workout user = userData.get();
            user.setName(workout.getName());
            user.setMoves(workout.getMoves());
            user.setRestMin(workout.getRestMin());
            user.setRestSec(workout.getRestSec());
            user.setPrepareSec(workout.getPrepareSec());
            user.setPreparetMin(workout.getPreparetMin());
            user.setRoundNumber(workout.getRoundNumber());
            user.setRoundSec(workout.getRoundSec());
            user.setRoundMin(workout.getRoundMin());
            user.setMovesTime(workout.getMovesTime());
            return new ResponseEntity<>(workoutRepository.save(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
