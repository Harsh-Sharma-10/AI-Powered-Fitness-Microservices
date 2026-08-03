package aiservices.example.AiService.Services;


import aiservices.example.AiService.Model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivityListenerServices {

    @RabbitListener(queues = "activity.queue")
    public void Activitylistner(Activity activity) {
        log.info("Received the message from the queue with activity id {} ", activity.getActivityId());
    }

}
