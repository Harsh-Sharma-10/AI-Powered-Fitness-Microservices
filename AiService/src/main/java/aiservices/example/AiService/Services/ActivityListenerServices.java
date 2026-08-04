package aiservices.example.AiService.Services;


import aiservices.example.AiService.Model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityListenerServices {

    private final ActivityAiService activityAiService;


    @RabbitListener(queues = "activity.queue")
    public void Activitylistener(Activity activity) {
        try {
            log.info("Received Activity with activity id : " + activity.getActivityId());
            String  recommendations = activityAiService.generateRecommendation(activity);
            log.info("Recommendations : " + recommendations);
        }catch (Exception ex) {
            log.error("Failed to generate recommendation", ex);
        }
    }

}
