package aiservices.example.AiService.Services;


import aiservices.example.AiService.Model.Activity;
import aiservices.example.AiService.Model.Recommendation;
import aiservices.example.AiService.RecommandRepo.RecommRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAiService {

    private final GeminiService geminiService;
    private final RecommRepository recommRepository;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptactivity(activity);
        String airesponse = geminiService.getAnswer(prompt);
        return processingAIresponse(airesponse, activity);
    }

    /// Here we are parsing the AI response we get from getAnswer() of geminiservices in JSON format to the String
    private Recommendation processingAIresponse(String airesponse, Activity activity) {
        try{
            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonNode = mapper.readTree(airesponse);


            JsonNode content = jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String text = content.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            log.info("Parsed AI Response  : " + text);

            ///  Now we are first assigning the recommendations field of main Recommendation Entity
            ///  For this purpose we need to extract the analysis String from the Main Parsed AI response
            ///  It is the Node pointing to the main RootNode of the text String
            JsonNode analysisjson  = mapper.readTree(text);
            /// It is the Entire analysis section
            /// Here we are keep the JsonNode pointer on ananlysis node
            JsonNode analysisNode = analysisjson.path("analysis");
            String recommendation = analysisExtracted(analysisNode);
            log.info("Analysis Extracted / Recommendation Field  : " + recommendation);

            ///  It is the List of the Improvements
            /// Here we are keep the JsonNode pointer on improvements node
            JsonNode improvement = analysisjson.path("improvements");
            List<String> improvementsList = extractimprovements(improvement);

            log.info("Here we are printing the extracted improvements :  \n ");
            for(String str : improvementsList){
                System.out.println(str);
            }
            /// It is the List of the Suggestions
            /// Here we are keep the JsonNode pointer at suggestion node
            JsonNode suggestionNode = analysisjson.path("suggestions");
            List<String> suggestionList = extractsuggestions(suggestionNode);
            log.info("Extracted suggestions List: \n ");
            for(String str : suggestionList){
                System.out.println(str);
            }
            /// It is the List of the Suggestions
            /// Here we are keep the JsonNode pointer at suggestion node
           JsonNode safetyNode = analysisjson.path("safety");
           List<String>safetyList = extractsafetymeasures(safetyNode);
           log.info("Extracted safety List : \n ");
            for (String str : safetyList){
                System.out.println(str);
            }

            return Recommendation.builder()
                    .activityId(activity.getActivityId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendations(recommendation)
                    .improvements(improvementsList)
                    .suggestions(suggestionList)
                    .safety(safetyList)
                    .createdAt(LocalDateTime.now())
                    .build();

        }catch (Exception ex){
            ex.printStackTrace();
            return DefaultRecommendation(activity);
        }
    }

    private Recommendation DefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getActivityId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendations("Unable to generate the Detailed analysis ")
                .improvements(Collections.singletonList("Continue with your Current Routine "))
                .suggestions(Collections.singletonList("Consulting with an Professional Trainer "))
                .safety(Arrays.asList(
                        "Always Warmup Before Exercise",
                        "Stay Harder",
                        "Listen to your Body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }


    private void  fullanalysisSection(StringBuilder analysis, JsonNode analysisNode, String key, String prefix) {

        if(!analysisNode.path(key).isMissingNode()){
            analysis.append(prefix);
            analysis.append(analysisNode.path(key).asText());
            analysis.append("\n");
        }

    }
    private String analysisExtracted(JsonNode analysisNode) {
        StringBuilder analysis =  new StringBuilder();
        fullanalysisSection(analysis,analysisNode,"overall","Overall :");
        fullanalysisSection(analysis,analysisNode,"pace","Pace :");
        fullanalysisSection(analysis,analysisNode,"heartRate","Heart Rate :");
        fullanalysisSection(analysis,analysisNode,"caloriesBurned","Calories Burned :");

        return analysis.toString().trim();
    }
    private List<String> extractimprovements(JsonNode improvementNode) {
        List<String> improvementsList = new ArrayList<>();

        if(improvementNode.isArray()){
            improvementNode.forEach(improvement -> {
                String area =   improvement.path("area").asText();
                String desc =   improvement.path("recommendation").asText();
                improvementsList.add(String.format("Area : %s\nRecommendation : %s\n", area, desc));
            });
        }
        return improvementsList.isEmpty() ?
                Collections.singletonList("No improvements is there !!") :
                improvementsList;

    }
    private List<String> extractsuggestions(JsonNode suggestionNode) {
        List<String> suggestions = new ArrayList<>();
        if(suggestionNode.isArray()){
            suggestionNode.forEach(suggestion ->{
                String WorkOut = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("Work Out: %s\n Description: %s\n", WorkOut,description));
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No Suggestion is there !!")
                : suggestions;
    }
    private List<String> extractsafetymeasures(JsonNode safetyNode) {

        List<String> safetyList = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach( safety ->{
                if(!safety.isNull()){
                    safetyList.add(safety.asText());
                }
            });
        }
        return safetyList.isEmpty() ?
                Collections.singletonList("No Safety measures is Required !!!") :
                safetyList;
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
                activity.getType(),           /// This is where we are using the activity to create the Activity Customized Prompt
                activity.getDuration(),
                activity.getCalories(),
                activity.getAdditionalMatrics()
        );

    }
}
