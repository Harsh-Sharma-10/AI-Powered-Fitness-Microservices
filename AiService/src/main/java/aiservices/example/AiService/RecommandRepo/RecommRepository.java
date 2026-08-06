package aiservices.example.AiService.RecommandRepo;


import aiservices.example.AiService.Model.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommRepository extends MongoRepository<Recommendation, String> {
          List<Recommendation> findByUserId(String userId);

          Optional<Recommendation> findByActivityId(String acticityid);
}
