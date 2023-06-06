package com.intuit.crowdfundingRestAPI.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intuit.crowdfundingRestAPI.entity.Project;

public interface ProjectsRepo extends JpaRepository<Project, Integer>{
	
	 List<Project> findByUserId(Integer id);

}
