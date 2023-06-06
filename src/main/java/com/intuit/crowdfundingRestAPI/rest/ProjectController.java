package com.intuit.crowdfundingRestAPI.rest;

import com.intuit.crowdfundingRestAPI.DTO.ProjectDTO;
import com.intuit.crowdfundingRestAPI.entity.Project;
import com.intuit.crowdfundingRestAPI.exception.ProjectNotFound;
import com.intuit.crowdfundingRestAPI.exception.UserNotFoundException;
import com.intuit.crowdfundingRestAPI.service.ProjectService;
import com.intuit.crowdfundingRestAPI.service.TransactionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private TransactionService transactionService;

    // 1. Create Project
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.saveProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    // 2. View All Projects
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }
    
    @GetMapping("/projectsList")
    public List<ProjectDTO> getProjectsWithoutUser() {
    	return projectService.getAllProjects().stream()
    	        .map(project -> {
//    	            Integer userId = null;
//    	            if (project.getUser() != null) {
//    	                userId = project.getUser().getId();
//    	            }
    	            return new ProjectDTO(
    	                project.getId(),
    	                project.getName(),
    	                project.getDescription(),
    	                project.getGoalAmount(),
    	                project.getRaisedAmount(),
    	                (project.getUser()!=null && project.getUser().getId() != null)
    	                   ? project.getUser().getId()
    	                		   : null 
    	            );
    	        })
    	        .collect(Collectors.toList());
    }


    // 3. Find Project with Project Id
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") Integer id) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()){
            return new ResponseEntity<>(project.get(), HttpStatus.OK);
        } else {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 4. Delete Project
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Integer id) {
//        if (projectService.existsById(id)) {
//            projectService.deleteProject(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
    	if(projectService.existsById(id)) {
    		if(transactionService.existsTransactionForProjId(id)) {
    			transactionService.deleteTransactionByProjectId(id);
    		}
    		projectService.deleteProject(id);
            return new ResponseEntity<String>("Project Data Deleted", HttpStatus.OK);
    	} else {
    		throw new ProjectNotFound("No Project found");
    	}
    }

    // 5. Update Project from Innovator screen
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") Integer id, @RequestBody Project projectDetails) {
        Optional<Project> project = projectService.getProjectById(id);
        if (!project.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
        	Project proj = project.get();
        	if (projectDetails.getName() != null) {
        		proj.setName(projectDetails.getName());
            }
            if (projectDetails.getDescription() != null) {
            	proj.setDescription(projectDetails.getDescription());
            }
            if (projectDetails.getGoalAmount() != null) {
            	proj.setGoalAmount(projectDetails.getGoalAmount());
            }
            if (projectDetails.getRaisedAmount() != null) {
            	proj.setRaisedAmount(projectDetails.getRaisedAmount());
            }
            // Don't update the user field
            Project updatedProject = projectService.saveProject(proj);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        }
    }
    
    // 6. Find Projects by User Id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getProjectsByUserId(@PathVariable("userId") Integer userId) {
        List<Project> projects = projectService.getProjectsByUserId(userId);
        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
    }
}

