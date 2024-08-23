import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class P2PServiceTest {

    @InjectMocks
    private P2PService p2pService;

    @Mock
    private CodaQueryClient codaQueryClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateCopyRequest() {
        // Prepare the P2PCopyRequest object
        P2PCopyRequest request = prepareCopyValidationObject();

        // Mocking the fetchTargetParty method (assumes data is retrieved from some external service)
        TargetParty targetParty1Data = new TargetParty();
        targetParty1Data.setTargetPartyId("BBB02682216");
        TargetPartyRelatedParties relatedParties1 = new TargetPartyRelatedParties();
        relatedParties1.setRelatedPartyId("BBB02633088");
        relatedParties1.setRelationshipTypeId(Arrays.asList("8091777", "8021585"));
        targetParty1Data.setTargetPartyRelatedParties(Collections.singletonList(relatedParties1));

        TargetParty targetParty2Data = new TargetParty();
        targetParty2Data.setTargetPartyId("BBB02682978");
        TargetPartyRelatedParties relatedParties2 = new TargetPartyRelatedParties();
        relatedParties2.setRelatedPartyId("BBB03040109");
        relatedParties2.setRelationshipTypeId(Arrays.asList("8058650"));
        targetParty2Data.setTargetPartyRelatedParties(Collections.singletonList(relatedParties2));

        when(codaQueryClient.getPartiesFromCoda(any(), any()))
            .thenReturn(Arrays.asList(targetParty1Data, targetParty2Data));

        // Execute the method under test
        P2PCopyResponse response = p2pService.validateCopyRequest(request);

        // Assertions
        assertNotNull(response);
        assertEquals("BBB02722214", response.getMainPartyId());
        assertEquals(2, response.getValidationStatuses().size());
        
        // Add more assertions as needed based on expected output
    }

    private P2PCopyRequest prepareCopyValidationObject() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainParty("BBB02722214");

        List<P2PCopyTargetParty> targetParties = new ArrayList<>();

        P2PCopyTargetParty targetParty1 = new P2PCopyTargetParty();
        targetParty1.setTargetPartyId("BBB02722214");
        targetParty1.setAction(P2PCopyAction.SKIP);
        targetParties.add(targetParty1);

        P2PCopyTargetParty targetParty2 = new P2PCopyTargetParty();
        targetParty2.setTargetPartyId("BBB02682216");
        targetParty2.setAction(P2PCopyAction.OVERWRITE);
        targetParties.add(targetParty2);

        P2PCopyTargetParty targetParty3 = new P2PCopyTargetParty();
        targetParty3.setTargetPartyId("BBB02682978");
        targetParty3.setAction(P2PCopyAction.SKIP_VALIDATION);
        targetParties.add(targetParty3);

        P2PCopyTargetParty targetParty4 = new P2PCopyTargetParty();
        targetParty4.setTargetPartyId("BBB03064394");
        targetParty4.setAction(P2PCopyAction.SKIP_VALIDATION);
        targetParties.add(targetParty4);

        request.setTargetParties(targetParties);

        List<P2PCopyRelationship> sourceRelationships = new ArrayList<>();

        P2PCopyRelationship relationship1 = new P2PCopyRelationship();
        relationship1.setSourcePartyId("BBB02633088");
        relationship1.setRelationshipTypeIds(Arrays.asList("8091777", "8021585", "8021761"));
        sourceRelationships.add(relationship1);

        P2PCopyRelationship relationship2 = new P2PCopyRelationship();
        relationship2.setSourcePartyId("BBB03040109");
        relationship2.setRelationshipTypeIds(Arrays.asList("8058650", "8058779", "8058654"));
        sourceRelationships.add(relationship2);

        request.setSourceRelationships(sourceRelationships);

        return request;
    }
}
