package aiservices.example.AiService.Model;


import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {

     private String activityId;
     private String userId;
     private Integer duration;
     private Integer calories;
     private LocalDateTime startTime;
     private Map<String,Object> additionalMatrics;
     private Instant creationTime;
     private Instant updateTime;
}
