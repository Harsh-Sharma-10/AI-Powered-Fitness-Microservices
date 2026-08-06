package com.fitness_microservices.activityservices.ActivityDTO;


import com.fitness_microservices.activityservices.Enums.ActivityType;
import com.mongodb.lang.NonNull;
import lombok.Data;
import java.util.Map;

@Data
public class ActivityRequest {

    @NonNull
    private String userId;
    @NonNull
    private ActivityType activityType;

    @NonNull
    private Integer duration;
    @NonNull
    private Integer calories;

    private Map<String,Object> additionalProperties;

}
