package com.example.AIpowered_Application.Services;


import com.example.AIpowered_Application.Dto.UserRequests;
import com.example.AIpowered_Application.Dto.UserResponse;
import com.example.AIpowered_Application.Mapper.UserMapperDtos;
import com.example.AIpowered_Application.Model.User;
import com.example.AIpowered_Application.Model.User_Logs;
import com.example.AIpowered_Application.Repository.UserLogsRepository;
import com.example.AIpowered_Application.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServices {

    private final UserRepository userRepository;
    private final UserLogsRepository userLogsRepository;

    public UserServices(UserRepository userRepository,UserLogsRepository userLogsRepository) {
        this.userRepository = userRepository;
        this.userLogsRepository = userLogsRepository;
    }

    public UserResponse findbyuserid(String userid){
        User user  = userRepository.findById(userid)
                .orElseThrow(()-> new RuntimeException("User not found!"));

        return UserMapperDtos.responseDto(user);
    }
    public UserResponse findbyemail(String email){
        User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not Exists !!! "));
        return UserMapperDtos.responseDto(user);
    }

    public List<UserResponse> findall(){
          List<User> users = userRepository.findAll();
          if(users.isEmpty())throw new RuntimeException("There is no users Exists !!! ");
          return users
                  .stream()
                  .map(UserMapperDtos::responseDto)
                  .collect(Collectors.toList());
    }

    public UserResponse adduser(UserRequests userRequests){

        if(userRepository.existsByEmail(userRequests.getEmail())){
            throw new RuntimeException("Email Already Exists");
        }
         User user  = UserMapperDtos.requestDto(userRequests);
         return UserMapperDtos.responseDto(userRepository.save(user));

    }
    public void deleteuser(String userid){
        if(userLogsRepository.existsByoriginalid(userid)){
            throw new RuntimeException("User Already deleted Exists !!!");
        }
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("User is NOT Exists !!!"));

        User_Logs userLogs = new  User_Logs();
        userLogs.setOriginalid(user.getId());
        userLogs.setFirstname(user.getFirstname());
        userLogs.setLastname(user.getLastname());
        userLogs.setEmail(user.getEmail());
        userLogs.setPassword(user.getPassword());
        userLogs.setDateofbirth(user.getDateOfBirth());
        userLogs.setPhone(user.getPhoneNumber());

        userLogsRepository.save(userLogs);
        userRepository.deleteById(userid);

    }

    /// @Helps in the Asynchrounous communication while acrtivity service will sends a requests to user services
    /// for the users validations by the user ID
    public Boolean existsbyuserid(String userid) {
        log.info("Checking if exists by userid  for User Validation : {}", userid);
          return userRepository.existsById(userid);
    }
}
