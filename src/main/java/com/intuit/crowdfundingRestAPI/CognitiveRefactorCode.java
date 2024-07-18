package com.example;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.controller.RelationshipVisualizationController;
import com.example.service.PartyRelationshipVisualizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

public class RelationshipVisualizationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PartyRelationshipVisualizationService partyRelationshipVisualizationService;

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(relationshipVisualizationController).build();
    }

    @Test
    public void testGetRelationshipVisualizationThrowsException() throws Exception {
        // Mocking the service to throw an exception
        when(partyRelationshipVisualizationService.getPartyVisualizationById("testPartyId"))
            .thenThrow(new RuntimeException("Service Exception"));

        mockMvc.perform(get("/hierarchy/testPartyId")
                .header("x-feature", "featureType")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
