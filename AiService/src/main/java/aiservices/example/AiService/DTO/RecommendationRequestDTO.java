package aiservices.example.AiService.DTO;


import com.mongodb.lang.NonNull;
import lombok.Data;

import java.util.List;


@Data
public class RecommendationRequestDTO {

      @NonNull
      private String ActivityId;
      @NonNull
      private String UserId;
      @NonNull
      private String activityType;
    @NonNull
    private String recommendations;
    @NonNull
    private List<String> improvements;
    @NonNull
    private  List<String> suggestions;
    @NonNull
    private  List<String> safety;


}
