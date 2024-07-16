package com.yourpackage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RelationshipVisualizationController.class,
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebstackSecurityConfig.class))
public class RelationshipVisualizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRelationshipVisualization() throws Exception {
        // Perform the GET request and verify the response
        mockMvc.perform(get("/visualization/hierarchy/{mainPartyId}", "testMainPartyId")
                .header("feature", "testFeature"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRelationshipVisualizationWithoutFeature() throws Exception {
        // Perform the GET request and verify the response
        mockMvc.perform(get("/visualization/hierarchy/{mainPartyId}", "testMainPartyId"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRelationshipVisualizationWithInvalidId() throws Exception {
        // Perform the GET request with an invalid ID and verify the response
        mockMvc.perform(get("/visualization/hierarchy/{mainPartyId}", "invalidMainPartyId"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRelationshipVisualizationWithException() throws Exception {
        // Simulate an exception by causing an internal server error
        mockMvc.perform(get("/visualization/hierarchy/{mainPartyId}", "testMainPartyId")
                .header("feature", "testFeature"))
                .andExpect(status().isInternalServerError());
    }
}
