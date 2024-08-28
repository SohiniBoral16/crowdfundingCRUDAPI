
@Test
public void testSourceRelationshipsMapping() {
    // Create a test P2PCopyRequest with source relationships
    P2PCopyRequest copyRequest = new P2PCopyRequest();
    copyRequest.setMainPartyId("BBBB02722214");

    // Setup source relationships
    List<P2PCopyRelationship> sourceRelationships = new ArrayList<>();
    
    P2PCopyRelationship relationship1 = new P2PCopyRelationship();
    relationship1.setSourcePartyId("SourceParty1");
    relationship1.setRelationshipTypeIds(Arrays.asList("Type1", "Type2"));
    
    P2PCopyRelationship relationship2 = new P2PCopyRelationship();
    relationship2.setSourcePartyId("SourceParty2");
    relationship2.setRelationshipTypeIds(Arrays.asList("Type3", "Type4"));
    
    sourceRelationships.add(relationship1);
    sourceRelationships.add(relationship2);
    
    copyRequest.setSourceRelationships(sourceRelationships);

    // Validate the copy request
    Map<String, List<String>> relationshipIdsBySourcePartyId = copyRequest.getSourceRelationships().stream()
        .collect(Collectors.toMap(
            P2PCopyRelationship::getSourcePartyId,
            P2PCopyRelationship::getRelationshipTypeIds
        ));

    // Assertions to ensure the map has been correctly populated
    assertNotNull(relationshipIdsBySourcePartyId);
    assertEquals(2, relationshipIdsBySourcePartyId.size());
    assertTrue(relationshipIdsBySourcePartyId.containsKey("SourceParty1"));
    assertTrue(relationshipIdsBySourcePartyId.containsKey("SourceParty2"));
    assertEquals(Arrays.asList("Type1", "Type2"), relationshipIdsBySourcePartyId.get("SourceParty1"));
    assertEquals(Arrays.asList("Type3", "Type4"), relationshipIdsBySourcePartyId.get("SourceParty2"));
}



@Test
public void testValidateCopyRequest_AllSkipActions() {
    P2PCopyRequest copyRequest = createTestP2PCopyRequestWithAllSkipActions();
    P2PCopyResponse response = p2PService.validateCopyRequest(copyRequest);

    List<String> validatePartyIds = response.getValidationStatus();
    assertTrue(validatePartyIds.isEmpty());
    assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
}

@Test
public void testValidateCopyRequest_SomeNullAndOverwriteActions() {
    P2PCopyRequest copyRequest = createTestP2PCopyRequestWithNullAndOverwriteActions();
    P2PCopyResponse response = p2PService.validateCopyRequest(copyRequest);

    List<String> validatePartyIds = response.getValidationStatus();
    assertEquals(2, validatePartyIds.size()); // Adjust according to your expected output
    assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
}

@Test
public void testValidateCopyRequest_MixedActions() {
    P2PCopyRequest copyRequest = createTestP2PCopyRequestWithMixedActions();
    P2PCopyResponse response = p2PService.validateCopyRequest(copyRequest);

    List<String> validatePartyIds = response.getValidationStatus();
    assertEquals(3, validatePartyIds.size()); // Adjust according to your expected output
    assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
}

@Test
public void testValidateCopyRequest_NoTargetParties() {
    P2PCopyRequest copyRequest = createTestP2PCopyRequestWithNoTargetParties();
    P2PCopyResponse response = p2PService.validateCopyRequest(copyRequest);

    List<String> validatePartyIds = response.getValidationStatus();
    assertTrue(validatePartyIds.isEmpty());
    assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
}

// Helper methods to create test objects based on your requirements
private P2PCopyRequest createTestP2PCopyRequestWithAllSkipActions() {
    P2PCopyRequest request = new P2PCopyRequest();
    request.setMainPartyId("BBBB02722214");
    
    List<P2PCopyTargetParty> targetParties = new ArrayList<>();
    P2PCopyTargetParty party1 = new P2PCopyTargetParty();
    party1.setTargetPartyId("BBBB02722214");
    party1.setAction(P2PCopyAction.SKIP);
    targetParties.add(party1);
    
    P2PCopyTargetParty party2 = new P2PCopyTargetParty();
    party2.setTargetPartyId("BBBB02682216");
    party2.setAction(P2PCopyAction.SKIP);
    targetParties.add(party2);
    
    request.setTargetParties(targetParties);
    
    return request;
}

// Similarly, create other helper methods for different scenarios



-------------------+-_--------
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

class P2PServiceTest {

    private P2PService p2pService = new P2PService();

    @Test
    void testValidateCopyRequest() {
        // Prepare the input data
        P2PCopyRequest copyRequest = new P2PCopyRequest();
        copyRequest.setMainParty("BBB02722214");

        // Create Target Parties
        TargetParty targetParty1 = new TargetParty("BBB02722214", P2PCopyAction.SKIP);
        TargetParty targetParty2 = new TargetParty("BBB02682216", P2PCopyAction.OVERWRITE);
        TargetParty targetParty3 = new TargetParty("BBB02682217", P2PCopyAction.SKIP_VALIDATION);
        copyRequest.setTargetParties(List.of(targetParty1, targetParty2, targetParty3));

        // Create Source Relationships
        SourceRelationship sourceRelationship = new SourceRelationship();
        sourceRelationship.setSourcePartyId("BBB02532943");
        sourceRelationship.setRelationshipTypeIds(List.of("8021501", "8021761"));
        copyRequest.setSourceRelationships(List.of(sourceRelationship));

        // Call the validateCopyRequest method
        P2PCopyResponse response = p2pService.validateCopyRequest(copyRequest);

        // Expected response structure
        P2PCopyResponse expectedResponse = new P2PCopyResponse();
        expectedResponse.setCopyStatus("VALIDATION_FAILURE");
        expectedResponse.setMainParty("BBB02722214");

        // Fill the expected Validation Statuses, failed and successful relationships as per your expected JSON

        // Validate the response with the expected one
        assertEquals(expectedResponse.getCopyStatus(), response.getCopyStatus());
        assertEquals(expectedResponse.getMainParty(), response.getMainParty());
        // Add more assertions to check the detailed structure
    }
}



public P2PCopyRequest createTestP2PCopyRequest() {
    P2PCopyRequest request = new P2PCopyRequest();
    request.setMainPartyId("BBB02722214");

    List<P2PCopyTargetParty> targetParties = new ArrayList<>();
    
    P2PCopyTargetParty party1 = new P2PCopyTargetParty();
    party1.setTargetPartyId("BBB02722214");
    party1.setAction(P2PCopyAction.SKIP);
    targetParties.add(party1);

    P2PCopyTargetParty party2 = new P2PCopyTargetParty();
    party2.setTargetPartyId("BBB02682216");
    party2.setAction(P2PCopyAction.OVERWRITE);
    targetParties.add(party2);

    P2PCopyTargetParty party3 = new P2PCopyTargetParty();
    party3.setTargetPartyId("BBB02682217");
    party3.setAction(P2PCopyAction.SKIP_VALIDATION);
    targetParties.add(party3);

    request.setTargetParties(targetParties);

    List<P2PCopySourceRelationship> sourceRelationships = new ArrayList<>();
    P2PCopySourceRelationship sourceRelationship = new P2PCopySourceRelationship();
    sourceRelationship.setSourcePartyId("BBB02532943");
    sourceRelationship.setRelationshipTypeIds(Arrays.asList("8021501", "8021761"));
    sourceRelationships.add(sourceRelationship);

    request.setSourceRelationships(sourceRelationships);

    return request;
}


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;

public class P2PServiceTest {

    @InjectMocks
    private P2PService p2pService;

    @BeforeEach
    public void setup() {
        p2pService = new P2PService();
    }

    @Test
    public void testValidateCopyRequest() {
        P2PCopyRequest request = createTestP2PCopyRequest();

        P2PCopyResponse response = p2pService.validateCopyRequest(request);

        assertNotNull(response);
        assertEquals("BBB02722214", response.getMainPartyId());

        List<P2PCopyValidationStatus> validationStatuses = response.getValidationStatus();
        assertFalse(validationStatuses.isEmpty());

        // Add additional assertions based on expected validation results
        validationStatuses.forEach(status -> {
            if (status.getTargetPartyId().equals("BBB02722214")) {
                assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, status.getStatus());
            } else if (status.getTargetPartyId().equals("BBB02682216")) {
                assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, status.getStatus());
            } else if (status.getTargetPartyId().equals("BBB02682217")) {
                assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, status.getStatus());
            }
        });
    }

    // Helper method to create test data
    private P2PCopyRequest createTestP2PCopyRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        List<P2PCopyTargetParty> targetParties = new ArrayList<>();

        P2PCopyTargetParty party1 = new P2PCopyTargetParty();
        party1.setTargetPartyId("BBB02722214");
        party1.setAction(P2PCopyAction.SKIP);
        targetParties.add(party1);

        P2PCopyTargetParty party2 = new P2PCopyTargetParty();
        party2.setTargetPartyId("BBB02682216");
        party2.setAction(P2PCopyAction.OVERWRITE);
        targetParties.add(party2);

        P2PCopyTargetParty party3 = new P2PCopyTargetParty();
        party3.setTargetPartyId("BBB02682217");
        party3.setAction(P2PCopyAction.SKIP_VALIDATION);
        targetParties.add(party3);

        request.setTargetParties(targetParties);

        List<P2PCopySourceRelationship> sourceRelationships = new ArrayList<>();
        P2PCopySourceRelationship sourceRelationship = new P2PCopySourceRelationship();
        sourceRelationship.setSourcePartyId("BBB02532943");
        sourceRelationship.setRelationshipTypeIds(Arrays.asList("8021501", "8021761"));
        sourceRelationships.add(sourceRelationship);

        request.setSourceRelationships(sourceRelationships);

        return request;
    }
}

