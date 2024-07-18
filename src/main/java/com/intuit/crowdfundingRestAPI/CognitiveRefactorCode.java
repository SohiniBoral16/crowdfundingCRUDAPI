// File: RelationshipVisualizationControllerTest.java

import com.ns.clientData.p2pservice.controller.RelationshipVisualizationController;
import com.ns.clientData.p2pservice.exception.P2PServiceException;
import com.ns.clientData.p2pservice.model.visualization.PartyRelationshipVisualization;
import com.ns.clientData.p2pservice.service.RelationshipVisualizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RelationshipVisualizationControllerTest {

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    @Mock
    private RelationshipVisualizationService relationshipVisualizationService;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
    }

    @Test
    void testGetRelationshipVisualization_Success() throws P2PServiceException {
        String mainPartyId = "testPartyId";
        String feature = "testFeature";

        when(request.getParameter("mainPartyId")).thenReturn(mainPartyId);
        when(request.getParameter("x-feature")).thenReturn(feature);

        List<PartyRelationshipVisualization> expectedVisualization = new ArrayList<>();
        when(relationshipVisualizationService.getPartyRelationshipVisualization(mainPartyId)).thenReturn(expectedVisualization);

        ResponseEntity<List<PartyRelationshipVisualization>> response = relationshipVisualizationController.getRelationshipVisualization(request, mainPartyId, feature);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedVisualization, response.getBody());
    }

    @Test
    void testGetRelationshipVisualization_Exception() throws P2PServiceException {
        String mainPartyId = "testPartyId";
        String feature = "testFeature";

        when(request.getParameter("mainPartyId")).thenReturn(mainPartyId);
        when(request.getParameter("x-feature")).thenReturn(feature);

        when(relationshipVisualizationService.getPartyRelationshipVisualization(mainPartyId)).thenThrow(new P2PServiceException("GET_RELATIONSHIP_VISUALIZATION_FAILED"));

        P2PServiceException exception = assertThrows(P2PServiceException.class, () -> {
            relationshipVisualizationController.getRelationshipVisualization(request, mainPartyId, feature);
        });

        assertEquals("GET_RELATIONSHIP_VISUALIZATION_FAILED", exception.getMessage());
    }
}
