
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
