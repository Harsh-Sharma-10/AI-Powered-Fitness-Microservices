package com.example.AIpowered_Application.Controller;


import com.example.AIpowered_Application.Dto.UserRequests;
import com.example.AIpowered_Application.Dto.UserResponse;
import com.example.AIpowered_Application.Model.User;
import com.example.AIpowered_Application.Repository.UserRepository;
import com.example.AIpowered_Application.Services.UserServices;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/{userid}")
    public ResponseEntity<UserResponse> getUserprofile(@PathVariable String userid) {
        return ResponseEntity.ok(userServices.findbyuserid(userid));
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserbyemail(@PathVariable String email) {
        return ResponseEntity.ok(userServices.findbyemail(email));
    }
    @GetMapping("/allusers")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20)Pageable pageable) {
        return ResponseEntity.ok(userServices.findall(pageable));
    }

    @GetMapping("/{userid}/validate")        /// Endpoint Point for the user validation
    public ResponseEntity<Boolean> validateUser(@PathVariable String userid) { ///  This method is created for the user validation
        return ResponseEntity.ok(userServices.existsbyuserid(userid));         /// When Activity service is called it first validating the user
    }                                                                          /// from this endpoint it will validate that user is  Actually in the User DB or not ?

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequests registerRequest) {
        return ResponseEntity.ok(userServices.adduser(registerRequest));
    }
    @DeleteMapping("/delete/{userid}")
    public ResponseEntity<String> deleteUser(@PathVariable String userid) {
        userServices.deleteuser(userid);
        return new ResponseEntity<>("User has been deleted", HttpStatus.NO_CONTENT);
    }


}
