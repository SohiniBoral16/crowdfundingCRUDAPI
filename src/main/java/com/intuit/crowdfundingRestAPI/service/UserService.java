package com.intuit.crowdfundingRestAPI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.intuit.crowdfundingRestAPI.entity.User;
import com.intuit.crowdfundingRestAPI.repo.UsersRepo;

@Service
public class UserService {

    @Autowired
    private UsersRepo usersRepo;

    public User createUser(User user) {
        return usersRepo.save(user);
    }
    
    public boolean userExistsOrNot(Integer id) {
    	return usersRepo.existsById(id);
    }
    
    public List<User> getAllUsers() {
        return usersRepo.findAll();
    }

    public void deleteUser(Integer id) {
    	usersRepo.deleteById(id);
    }

    public User updateUser(Integer id, User userDetails) {
        Optional<User> optionalUser = usersRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setPasswordHash(userDetails.getPasswordHash());
            user.setRole(userDetails.getRole());
            return usersRepo.save(user);
        }
        return null;
    }
    
    public User findUserByEmail(String email) {
        return usersRepo.findByEmail(email);
    }
    
    public User getUser(Integer userId) {
        Optional<User> user = usersRepo.findById(userId);
        return user.orElse(null);
    }

}

