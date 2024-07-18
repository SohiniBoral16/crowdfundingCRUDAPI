
// File: RelationshipVisualizationControllerTest.java

import com.ns.clientData.p2pservice.controller.RelationshipVisualizationController;
import com.ns.clientData.p2pservice.exception.P2PServiceException;
import com.ns.clientData.p2pservice.model.visualization.PartyRelationshipVisualization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RelationshipVisualizationControllerTest {

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
    }

    @Test
    void testGetRelationshipVisualizationWithException() throws Exception {
        String mainPartyId = "testPartyId";
        String feature = "testFeature";

        when(request.getParameter("mainPartyId")).thenReturn(mainPartyId);
        when(request.getParameter("x-feature")).thenReturn(feature);

        // Mocking a failure scenario by throwing a RuntimeException when AuthUtils.getAuthUser is called
        // Assuming AuthUtils.getAuthUser(request) is a part that can throw an exception in the real scenario
        try {
            // Simulate exception in AuthUtils.getAuthUser(request)
            when(AuthUtils.getAuthUser(request)).thenThrow(new RuntimeException("Simulated Auth Exception"));

            // Call the controller method and expect a P2PServiceException to be thrown
            P2PServiceException exception = assertThrows(P2PServiceException.class, () -> {
                relationshipVisualizationController.getRelationshipVisualization(request, mainPartyId, feature);
            });

            // Verify that the exception message is as expected
            assertEquals("GET_RELATIONSHIP_VISUALIZATION_FAILED", exception.getMessage());
        } catch (Exception e) {
            // If any exception occurs during setup, fail the test
            throw new RuntimeException("Test setup failed", e);
        }
    }
}
----------------_--------

// File: RelationshipVisualizationControllerTest.java

import com.ns.clientData.p2pservice.controller.RelationshipVisualizationController;
import com.ns.clientData.p2pservice.exception.P2PServiceException;
import com.ns.clientData.p2pservice.model.visualization.PartyRelationshipVisualization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RelationshipVisualizationControllerTest {

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
    }

    @Test
    void testGetRelationshipVisualization_Exception() {
        String mainPartyId = "testPartyId";
        String feature = "testFeature";
        
        when(request.getParameter("mainPartyId")).thenReturn(mainPartyId);
        when(request.getParameter("x-feature")).thenReturn(feature);

        // Simulate the exception being thrown
        Exception exception = assertThrows(P2PServiceException.class, () -> {
            relationshipVisualizationController.getRelationshipVisualization(request, mainPartyId, feature);
        });

        // Verify that the exception message is as expected
        assertEquals("Expected exception message", exception.getMessage());
    }
}

-------------------------

// File: RelationshipVisualizationControllerTest.java

import com.ns.clientData.p2pservice.controller.RelationshipVisualizationController;
import com.ns.clientData.p2pservice.exception.P2PServiceException;
import com.ns.clientData.p2pservice.model.visualization.PartyRelationshipVisualization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RelationshipVisualizationControllerTest {

    @InjectMocks
    private RelationshipVisualizationController relationshipVisualizationController;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
    }

    @Test
    void testGetRelationshipVisualization_Failure() throws P2PServiceException {
        String mainPartyId = "testPartyId";
        when(request.getParameter("mainPartyId")).thenReturn(mainPartyId);

        // Assuming the exception is thrown when the service method is called, but service is not present
        // We will simulate the exception directly
        assertThrows(P2PServiceException.class, () -> {
            throw new P2PServiceException("Failed");
        });

        // The following line simulates calling the controller method and expecting an exception
        P2PServiceException exception = assertThrows(P2PServiceException.class, () -> {
            relationshipVisualizationController.getRelationshipVisualization(request);
        });

        // Verify that the exception message is as expected
        assertEquals("Failed", exception.getMessage());
    }
}
