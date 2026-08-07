package com.fitness_microservices.activityservices.Controller;



import com.fitness_microservices.activityservices.ActivityDTO.ActivityRequest;
import com.fitness_microservices.activityservices.ActivityDTO.ActivityResponse;
import com.fitness_microservices.activityservices.Services.ActivityServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/activity/")
public class Activitycontroller {

    private final ActivityServices activityServices;

    public Activitycontroller(ActivityServices activityServices) {
        this.activityServices = activityServices;

    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String id ) {

        return ResponseEntity.ok(activityServices.getActivitybyid(id));

    }

    @GetMapping("/allactivities")
    public ResponseEntity<List<ActivityResponse>> getAllActivities() {
        return ResponseEntity.ok(activityServices.getActivities());
    }


    @GetMapping("/usersactivities")
    public ResponseEntity<List<ActivityResponse>> getAllUserActivities(
            @RequestHeader("Z-user-id") String userid) {  /// A header is passed and we have to enter
        return ResponseEntity.ok(activityServices.getActivitiesByUserid(userid)); /// the value for the Header Key --> Value(have to enter)
    }

    @PostMapping("/addActivity")
    public ResponseEntity<ActivityResponse> addActivity(@RequestBody ActivityRequest activity) {
        return ResponseEntity.ok(activityServices.addActivity(activity));
    }
}
