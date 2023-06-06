package com.intuit.crowdfundingRestAPI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.crowdfundingRestAPI.entity.Project;
import com.intuit.crowdfundingRestAPI.service.ProjectService;

@SpringBootTest
@AutoConfigureMockMvc
class CrowdfundingCrudapiProjectsTests {

	@MockBean
    private ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllProjects() throws Exception {
        // Arrange
        Project project1 = new Project();
        // Set any necessary properties of project1
        Project project2 = new Project();
        // Set any necessary properties of project2
        List<Project> projects = Arrays.asList(project1, project2);

        when(projectService.getAllProjects()).thenReturn(projects);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
    
	@Test
    public void testCreateProject() throws Exception {
        // Arrange
        Project project = new Project();
        // Set any necessary properties of the project object
        
        Project createdProject = new Project();
        // Set any necessary properties of the createdProject object
        
        Mockito.when(projectService.saveProject(any(Project.class))).thenReturn(createdProject);
        
        ObjectMapper objectMapper = new ObjectMapper();
        String projectJson = objectMapper.writeValueAsString(project);
        
        // Act
        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectJson)
        ).andExpect(status().isCreated())
        .andReturn().getResponse();

        // Assert
        Project returnedProject = objectMapper.readValue(response.getContentAsString(), Project.class);
        //assertThat(returnedProject).isEqualToComparingFieldByField(createdProject);
        assertThat(returnedProject).usingRecursiveComparison().isEqualTo(createdProject);

    }

}
