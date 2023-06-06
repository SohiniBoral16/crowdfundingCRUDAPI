package com.intuit.crowdfundingRestAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.intuit.crowdfundingRestAPI.entity.*;
import com.intuit.crowdfundingRestAPI.repo.*;

@SpringBootApplication
@EnableEurekaClient
public class CrowdfundingCrudapiApplication {

	public static void main(String[] args) {
		//ConfigurableApplicationContext context =
		        SpringApplication.run(CrowdfundingCrudapiApplication.class, args);

//		        ProjectsRepo repo = context.getBean(ProjectsRepo.class);
//		        UsersRepo userRepo = context.getBean(UsersRepo.class);
//
//		        System.out.println(repo.getClass().getName());
//
//		        // Fetch an existing user or create a new user
//		        User user = userRepo.findById(4).orElse(new User());
//		        // Populate user's fields if creating a new user...
//
//		        Project p1 = new Project();
//		        p1.setName("Project E");
//		        p1.setDescription("Project E Description");
//		        p1.setGoalAmount(new BigDecimal(160000));
//		        p1.setRaisedAmount(new BigDecimal(0));
//		        p1.setUser(user);
//
//		        repo.save(p1);
	}

}
