package aiservices.example.AiService.Controller;


import aiservices.example.AiService.DTO.RecommendationResponseDTO;
import aiservices.example.AiService.Model.Recommendation;

import aiservices.example.AiService.Services.recommServices;
import jakarta.ws.rs.GET;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airecommendations")
public class RecommendationController {

    private final recommServices recommServices;


    public RecommendationController(recommServices recommServices) {
        this.recommServices = recommServices;
    }

    @GetMapping("/allrecommands/{userid}")
    public ResponseEntity<List<RecommendationResponseDTO>>getallrecommandsbyuser(@PathVariable String userid){
         return ResponseEntity.ok(recommServices.findbyuserid(userid));
    }
    @GetMapping("/recommands/{acticityid}")
    public ResponseEntity<RecommendationResponseDTO> getallrecommandsbyactivity(@PathVariable String acticityid){
        /// Endpoint for the Get all Activities via Activity

        return ResponseEntity.ok(recommServices.findByActivityId(acticityid));
    }
}
