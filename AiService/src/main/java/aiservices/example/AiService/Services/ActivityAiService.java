package aiservices.example.AiService.Services;


import aiservices.example.AiService.Model.Activity;
import aiservices.example.AiService.RecommandRepo.RecommRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAiService {

    private final GeminiService geminiService;
    private final RecommRepository recommRepository;

    public String generateRecommendation(Activity activity) {
        String prompt = createPromptactivity(activity);
        String airesponse = geminiService.getAnswer(prompt);
        processAiresponse(activity, airesponse);
        return airesponse;

    }
    private void processAiresponse(Activity activity, String airesponse) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(airesponse);

            JsonNode textnode = jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsontext = textnode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();
            log.info("AI response : " + jsontext);

            JsonNode jsonNode1 = mapper.readTree(jsontext);

            JsonNode analysis = jsonNode1.path("analysis");

            String overall = analysis.path("overall").asText();
            String pace = analysis.path("pace").asText();
            String heartRate = analysis.path("heartRate").asText();
            String calories = analysis.path("caloriesBurned").asText();

            log.info("Overall : {}", overall);
            log.info("Pace : {}", pace);
            log.info("Heart Rate : {}", heartRate);
            log.info("Calories : {}", calories);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private String createPromptactivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCalories(),
                activity.getAdditionalMatrics()
        );

    }
}
