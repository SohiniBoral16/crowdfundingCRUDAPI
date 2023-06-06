package com.intuit.crowdfundingRestAPI.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.intuit.crowdfundingRestAPI.entity.User;

public interface UsersRepo extends JpaRepository<User, Integer> {
	
	User findByEmail(String email);

}

