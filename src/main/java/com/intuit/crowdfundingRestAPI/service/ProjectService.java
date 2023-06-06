package com.intuit.crowdfundingRestAPI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intuit.crowdfundingRestAPI.entity.Project;
import com.intuit.crowdfundingRestAPI.entity.User;
import com.intuit.crowdfundingRestAPI.repo.ProjectsRepo;
import com.intuit.crowdfundingRestAPI.repo.UsersRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectsRepo projectsRepo;

    @Autowired
    public ProjectService(ProjectsRepo projectsRepo) {
        this.projectsRepo = projectsRepo;
    }
    
    @Autowired
    private UsersRepo usersRepo;

    // Save or update a project
    public Project saveProject(Project project) {
       return projectsRepo.save(project);
    }


    // Get all projects
    public List<Project> getAllProjects() {
        return projectsRepo.findAll();
    }

    // Get a project by id
    public Optional<Project> getProjectById(Integer id) {
        return projectsRepo.findById(id);
    }

    // Delete a project by id
    public void deleteProject(Integer id) {
        projectsRepo.deleteById(id);
    }

    // Check if a project exists by id
    public boolean existsById(Integer id) {
        return projectsRepo.existsById(id);
    }
    
    public List<Project> getProjectsByUserId(Integer userId) {
        return projectsRepo.findByUserId(userId);
    }
    
    public Project getProject(Integer projectId) {
        Optional<Project> project = projectsRepo.findById(projectId);
        return project.orElse(null);
    }

}


