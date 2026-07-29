package com.fitness_microservices.activityservices.Services;

import com.fitness_microservices.activityservices.ActivityDTO.ActivityRequest;
import com.fitness_microservices.activityservices.ActivityDTO.ActivityResponse;
import com.fitness_microservices.activityservices.Mapper.MapperClass;
import com.fitness_microservices.activityservices.Repository.ActivityRepo;
import com.fitness_microservices.activityservices.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ActivityServices {
    
    private final ActivityRepo activityRepo;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;


    /// @Value annotation will injecting the properties from yml file to these attributes
    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingkey;



     public ActivityServices( ActivityRepo  activityRepo,
                              UserValidationService userValidationService,
                              RabbitTemplate rabbitTemplate) {  /// Replacable By
            this.activityRepo = activityRepo;                                                          /// @RequiredArgsConstructor
            this.userValidationService  =  userValidationService;
            this.rabbitTemplate = rabbitTemplate;
    }





    
    public ActivityResponse getActivitybyid(String activityId) {
        return activityRepo
                .findById(activityId)
                .map(MapperClass::activityresponse)
                .orElseThrow(()-> new RuntimeException("Activity with id " + activityId + " not found"));


    }

    public List<ActivityResponse> getActivities(){
           List<Activity> activityResponses = activityRepo.findAll();
           if(activityResponses.isEmpty()){
               throw new RuntimeException("No activities found");
           }

           return activityResponses
                   .stream()
                   .map(MapperClass::activityresponse)
                   .collect(Collectors.toList());
    }
    public ActivityResponse addActivity(ActivityRequest activityRequest){
        boolean isValiduser = userValidationService.validateUser(activityRequest.getUserId());
        if(!isValiduser){
            throw new RuntimeException("Invalid user : " + activityRequest.getUserId());
        }
        Activity activity = MapperClass.activityrequest(activityRequest);
        activityRepo.save(activity);

        try{
            log.info("Putting the message into the RabbitMQ");
            rabbitTemplate.convertAndSend(exchange,routingkey,activity);  ///@Sent the message to the Rabbit Mq using the filed exchange, routing key, activity
        }catch(Exception e){
            log.error("Message cannot be sent to RabbitMQ", e.getMessage());
        }
        return MapperClass.activityresponse(activity);

    }

    public List<ActivityResponse> getActivitiesByUserid(String userid){
         List<Activity> activities = activityRepo.findByUserId(userid);
         if(activities.isEmpty()) {
             throw new RuntimeException("No activities found for user with id : " + userid);
         }
          return  activities
                 .stream()
                 .map(MapperClass::activityresponse)
                 .collect(Collectors.toList());
    }


}
