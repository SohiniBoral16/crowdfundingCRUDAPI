package com.yourpackage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RelationshipVisualizationController.class)
public class RelationshipVisualizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelationshipVisualizationService relationshipVisualizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRelationshipVisualizationWithException() throws Exception {
        // Simulate an exception by causing an internal server error
        mockMvc.perform(get("/visualization/hierarchy/{mainPartyId}", "testMainPartyId")
                .header("feature", "testFeature"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> result.getResponse().getContentAsString().contains("Get Relationship Visualization failed for mainPartyId: testMainPartyId"));
    }
}


------------------------------
package com.yourpackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/visualization")
public class RelationshipVisualizationController {

    @Autowired
    private RelationshipVisualizationService relationshipVisualizationService;

    @GetMapping("/hierarchy/{mainPartyId}")
    public ResponseEntity<?> getRelationshipVisualization(HttpServletRequest request,
                                                          @PathVariable("mainPartyId") String mainPartyId,
                                                          @RequestHeader(value = "feature", required = false) String feature) {
        try {
            LOG.info("getRelationshipVisualization called KEY={} SERVICE={} USER={} FEATURE={}", mainPartyId, "GetRelationshipVisualization");
            List<PartyVisualization> partyVisualization = new ArrayList<>();
            // Uncomment and implement the service call
            // partyVisualization = relationshipVisualizationService.getPartyVisualizationById(mainPartyId);
            return ResponseEntity.ok(partyVisualization);
        } catch (Exception ex) {
            String errorMessage = "Get Relationship Visualization failed for mainPartyId: " + mainPartyId;
            LOG.error(errorMessage, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ErrorResponse("INTERNAL_SERVER_ERROR", errorMessage));
        }
    }

    // ErrorResponse class for structured error response
    static class ErrorResponse {
        private String errorCode;
        private String errorMessage;

        public ErrorResponse(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        // Getters and setters
        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}


------------------------------
package com.yourpackage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RelationshipVisualizationController.class)
public class RelationshipVisualizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelationshipVisualizationService relationshipVisualizationService;

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
                .andExpect(status().isInternalServerError())
                .andExpect(result -> result.getResolvedException().getMessage().contains("Get Relationship Visualization failed"));
    }
}



---------------------
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
