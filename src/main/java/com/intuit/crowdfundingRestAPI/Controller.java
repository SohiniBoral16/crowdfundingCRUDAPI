import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@WebMvcTest(RelationshipVisualizationController.class)
public class RelationshipVisualizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private P2PHierarchyService p2pHierarchyService;

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    // Method to provide parameters for the test
    private static Stream<Arguments> provideParameters() {
        P2PVisualization successResponse = new P2PVisualization(/* Populate mock object */);
        List<P2PVisualization> successfulResponseBody = List.of(successResponse);
        List<P2PVisualization> failureResponseBody = List.of();

        return Stream.of(
                Arguments.of("validMainPartyId", 
                             Map.of(
                                 P2PRelationshipVisualizationFilter.OWNERSHIP_RELATIONSHIPS, List.of("Related parties", "Ownership till 10%"),
                                 P2PRelationshipVisualizationFilter.PEP_PARTIES, List.of("PEP indicator")
                             ), 
                             List.of(P2PRelationshipVisualizationAction.CALCPEP, P2PRelationshipVisualizationAction.CALCEFFECTIVEPERC), 
                             successfulResponseBody, 
                             HttpStatus.OK),
                Arguments.of("invalidMainPartyId", 
                             Map.of(), // Empty filters
                             List.of(), // No actions
                             failureResponseBody, 
                             HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testGetRelationshipVisualizationHierarchy(String mainPartyId, 
                                                           Map<P2PRelationshipVisualizationFilter, List<String>> filters, 
                                                           List<P2PRelationshipVisualizationAction> actions, 
                                                           List<P2PVisualization> mockResponse, 
                                                           HttpStatus expectedStatus) throws Exception {

        P2PRelationshipVisualizationRequest request = P2PRelationshipVisualizationRequest.builder()
            .mainPartyId(mainPartyId)
            .filters(filters)
            .actions(actions)
            .build();

        if (expectedStatus == HttpStatus.OK) {
            when(p2pHierarchyService.getPartyVisualizationByPartyId(mainPartyId)).thenReturn(mockResponse);

            mockMvc.perform(post("/v2/visualization")
                    .contentType(APPLICATION_JSON)
                    .content(asJsonString(request)) // Convert the request to JSON
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isOk());
        } else {
            when(p2pHierarchyService.getPartyVisualizationByPartyId(mainPartyId)).thenThrow(new P2PServiceException("Error occurred"));

            mockMvc.perform(post("/v2/visualization")
                    .contentType(APPLICATION_JSON)
                    .content(asJsonString(request)) // Convert the request to JSON
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    // Helper method to convert the object to JSON
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
--------------------
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Stream;

@WebMvcTest(RelationshipVisualizationController.class)
public class RelationshipVisualizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private P2PHierarchyService p2pHierarchyService;

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    private static Stream<Arguments> provideParameters() {
        List<P2PVisualization> successfulResponseBody = List.of(new P2PVisualization(/* ... */));
        List<P2PVisualization> failureResponseBody = List.of();
        HttpStatus successStatus = HttpStatus.OK;
        HttpStatus failureStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return Stream.of(
                Arguments.of("validMainPartyId", successfulResponseBody, successStatus),
                Arguments.of("invalidMainPartyId", failureResponseBody, failureStatus)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testGetRelationshipVisualizationHierarchy(String mainPartyId, List<P2PVisualization> mockResponse, HttpStatus expectedStatus) throws Exception {
        if (expectedStatus == HttpStatus.OK) {
            when(p2pHierarchyService.getPartyVisualizationByPartyId(mainPartyId)).thenReturn(mockResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/v2/visualization")
                    .contentType(APPLICATION_JSON)
                    .content("{\"mainPartyId\": \"" + mainPartyId + "\"}")
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(/* expected JSON structure for success */));
        } else {
            when(p2pHierarchyService.getPartyVisualizationByPartyId(mainPartyId)).thenThrow(new P2PServiceException("Error occurred"));

            mockMvc.perform(MockMvcRequestBuilders.post("/v2/visualization")
                    .contentType(APPLICATION_JSON)
                    .content("{\"mainPartyId\": \"" + mainPartyId + "\"}")
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }
}



import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

public class RelationshipVisualizationControllerTest {

    @Mock
    private RelationshipVisualizationService relationshipVisualizationService;

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    @Mock
    private MockMvc mockMvc;

    private static Stream<Arguments> provideParameters() {
        List<P2PVisualization> successfulResponseBody = List.of(new P2PVisualization(/* ... */));
        List<P2PVisualization> failureResponseBody = List.of();
        HttpStatus successStatus = HttpStatus.OK;
        HttpStatus failureStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return Stream.of(
                Arguments.of("validMainPartyId", successfulResponseBody, successStatus),
                Arguments.of("invalidMainPartyId", failureResponseBody, failureStatus)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testGetP2PRelationshipVisualizationHierarchyWithFilters(String mainPartyId, List<P2PVisualization> mockResponse, HttpStatus expectedStatus) throws Exception {
        if (expectedStatus == HttpStatus.OK) {
            when(relationshipVisualizationService.getPartyVisualization(mainPartyId)).thenReturn(mockResponse);
            mockMvc.perform(get("/visualization/v2/hierarchy")
                    .param("mainPartyId", mainPartyId)
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(/* Expected JSON result */));
        } else {
            when(relationshipVisualizationService.getPartyVisualization(mainPartyId)).thenThrow(new P2PServiceException("Error occurred"));
            mockMvc.perform(get("/visualization/v2/hierarchy")
                    .param("mainPartyId", mainPartyId)
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }
}





public interface ActionHandler {
    void execute(P2PRelationshipVisualizationRequest request);
}

public class OwnershipRelationshipHandler implements ActionHandler {
    @Override
    public void execute(P2PRelationshipVisualizationRequest request) {
        // Implement ownership relationship logic here
    }
}

public class NonOwnershipRelationshipHandler implements ActionHandler {
    @Override
    public void execute(P2PRelationshipVisualizationRequest request) {
        // Implement non-ownership relationship logic here
    }
}

public class ActionRegistry {
    private final Map<String, ActionHandler> actionHandlers = new HashMap<>();

    public ActionRegistry() {
        actionHandlers.put("RETURN_OWNERSHIP_RELATIONSHIP", new OwnershipRelationshipHandler());
        actionHandlers.put("RETURN_NON_OWNERSHIP_RELATIONSHIP", new NonOwnershipRelationshipHandler());
        // Add more actions as needed
    }

    public void executeAction(String actionName, P2PRelationshipVisualizationRequest request) {
        ActionHandler handler = actionHandlers.get(actionName);
        if (handler != null) {
            handler.execute(request);
        } else {
            throw new IllegalArgumentException("Unknown action: " + actionName);
        }
    }
}
