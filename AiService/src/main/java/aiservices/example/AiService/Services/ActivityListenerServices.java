package aiservices.example.AiService.Services;


import aiservices.example.AiService.Model.Activity;
import aiservices.example.AiService.Model.Recommendation;
import aiservices.example.AiService.RecommandRepo.RecommRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityListenerServices {

    private final ActivityAiService activityAiService;
    private final RecommRepository recommRepository;

    @RabbitListener(queues = "activity.queue")
    public void Activitylistener(Activity activity) {
        try {
            log.info("Received Activity with activity id : " + activity.getActivityId());
            Recommendation recommendation =  activityAiService.generateRecommendation(activity);
            recommRepository.save(recommendation);
        }catch (Exception ex) {
            log.error("Failed to generate recommendation", ex);
        }
    }

}
