package com.example.singidunum.Repository;


import com.example.singidunum.Model.Workout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface WorkoutRepository extends MongoRepository<Workout, String> {
    Workout findByName(String name);

    @Aggregation(pipeline = { "{ '$group': { '_id' : '$name' } }" })
    List<String> findDistinctNames();
}
