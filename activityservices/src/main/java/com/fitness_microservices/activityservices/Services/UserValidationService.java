package com.fitness_microservices.activityservices.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final WebClient userserviceWebClient;

    public boolean validateUser(String userid) {
        try {/// make API Call
            /// make API Call
            log.info("Calling the User Validation API  {}", userid);
            return  Boolean.TRUE.equals(userserviceWebClient.get()
                    .uri("/api/users/{userid}/validate",userid)
                    .retrieve()         /// make API Call
                    .bodyToMono(Boolean.class)
                    .block());

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("User Not Found : " + userid);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Invalid user id : " + userid);
            }

        }
        return false;
    }

}
