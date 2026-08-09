package com.example.AIpowered_Application.Services;


import com.example.AIpowered_Application.Dto.UserRequests;
import com.example.AIpowered_Application.Dto.UserResponse;
import com.example.AIpowered_Application.Exceptions.UserAlreadyExistsException;
import com.example.AIpowered_Application.Exceptions.UserNotFoundException;
import com.example.AIpowered_Application.Mapper.UserMapperDtos;
import com.example.AIpowered_Application.Model.User;
import com.example.AIpowered_Application.Model.User_Logs;
import com.example.AIpowered_Application.Repository.UserLogsRepository;
import com.example.AIpowered_Application.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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
        User user = userRepository.findById(userid)
                .orElseThrow(()->
                        new UserNotFoundException(String.format("User with id %s not found",userid)));
        return UserMapperDtos.responseDto(user);

    }
    public UserResponse findbyemail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found",email)));
        return UserMapperDtos.responseDto(user);

    }
    public Page<UserResponse> findall(Pageable pageable){
        return userRepository.findAll(pageable)
                .map(UserMapperDtos::responseDto);

    }

    public UserResponse adduser(UserRequests userRequests)  {

        if(userRepository.findByEmail(userRequests.getEmail()).isPresent()){
             throw new UserAlreadyExistsException("User is Already exists with this email !!!"+userRequests.getEmail());
        }
        User user = UserMapperDtos.requestDto(userRequests);
        User saaved = userRepository.save(user);
        return UserMapperDtos.responseDto(saaved);

    }
    public void deleteuser(String userid){
        if(userLogsRepository.existsByoriginalid(userid)){
            throw new RuntimeException("User is Already Deleted !!!");
        }
        User user = userRepository.findById(userid)
                .orElseThrow(() ->
                        new UserNotFoundException("User is NOT Exists !!!"));

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
