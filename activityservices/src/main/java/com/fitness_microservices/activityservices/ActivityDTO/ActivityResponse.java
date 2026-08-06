package com.fitness_microservices.activityservices.ActivityDTO;

import com.fitness_microservices.activityservices.Enums.ActivityType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponse {


    private String activityId;
    private String userId;
    private ActivityType type;
    private LocalDateTime startTime;
    private Integer duration;
    private Integer calories;

    private Map<String,Object> additionalMatrics;
}
