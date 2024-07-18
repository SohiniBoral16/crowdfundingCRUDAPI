// File: RelationshipVisualizationControllerTest.java

import com.ns.clientData.p2pservice.controller.RelationshipVisualizationController;
import com.ns.clientData.p2pservice.service.RelationshipVisualizationService;
import com.ns.clientData.p2pservice.exception.P2PServiceException;
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

    @Mock
    private RelationshipVisualizationService relationshipVisualizationService;

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
    }

    @Test
    void testGetRelationshipVisualization_Success() throws P2PServiceException {
        String mainPartyId = "testPartyId";
        List<PartyRelationshipVisualization> mockResponse = new ArrayList<>();
        when(request.getParameter("mainPartyId")).thenReturn(mainPartyId);
        when(relationshipVisualizationService.getPartyRelationshipVisualization(mainPartyId)).thenReturn(mockResponse);

        List<PartyRelationshipVisualization> response = relationshipVisualizationController.getRelationshipVisualization(request);

        assertEquals(mockResponse, response);
        verify(relationshipVisualizationService, times(1)).getPartyRelationshipVisualization(mainPartyId);
    }

    @Test
    void testGetRelationshipVisualization_Failure() throws P2PServiceException {
        String mainPartyId = "testPartyId";
        when(request.getParameter("mainPartyId")).thenReturn(mainPartyId);
        when(relationshipVisualizationService.getPartyRelationshipVisualization(mainPartyId)).thenThrow(new P2PServiceException("Failed"));

        P2PServiceException exception = assertThrows(P2PServiceException.class, () -> {
            relationshipVisualizationController.getRelationshipVisualization(request);
        });

        assertEquals("Failed", exception.getMessage());
        verify(relationshipVisualizationService, times(1)).getPartyRelationshipVisualization(mainPartyId);
    }
}
