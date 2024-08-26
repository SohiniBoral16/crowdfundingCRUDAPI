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

