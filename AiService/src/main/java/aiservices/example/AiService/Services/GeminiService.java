package aiservices.example.AiService.Services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService{

    @Value("${gemini.api.url}")  /// It will directly inject the fields value for the url and the key from .yml
    private String geminiapiurl;

    @Value("${gemini.api.key}")
    private String geminiapikey;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }


    public String getAnswer(String question) {   /// In this format you will provide the Input to Gemini

    Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                    Map.of(
                            "parts", List.of(
                                    Map.of(
                                            "text", question
                                    )
                            )
                    )
            )
    );
    ///@Calling_The_API_here_using_WebClient_Builder_Pattern_(Method_Chaining)
          String response = webClient.post()
                .uri(geminiapiurl + geminiapikey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

      return response;

    }

}
