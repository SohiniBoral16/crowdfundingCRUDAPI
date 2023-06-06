package com.intuit.crowdfundingRestAPI.rest;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.intuit.crowdfundingRestAPI.entity.User;
import com.intuit.crowdfundingRestAPI.exception.UserNotFoundException;
import com.intuit.crowdfundingRestAPI.repo.UsersRepo;
import com.intuit.crowdfundingRestAPI.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    

     //1. Create User
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
 
    // 2. Get All Users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 3. Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Integer id) {

    	if(userService.userExistsOrNot(id)) {
    		userService.deleteUser(id);
            return new ResponseEntity<String>("User Data Deleted", HttpStatus.OK);
    	} else {
    		throw new UserNotFoundException("No user found");
    	}
    }

	// 5. Edit User
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Integer id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // 6. Login - working
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User loginUser) {
        User user = userService.findUserByEmail(loginUser.getEmail());

        if (user != null && user.getPasswordHash().equals(loginUser.getPasswordHash())) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    

}

