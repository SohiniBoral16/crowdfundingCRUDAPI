
@Test
public void testMixedActionParties() throws InterruptedException, JsonProcessingException {
    // Arrange
    P2PCopyRequest request = createTestP2PCopyRequestWithMixedActions();

    // Mock response for parties with OVERWRITE action
    List<Party> overwriteParties = createMockPartiesForOverwriteAction();
    
    // Mock the codaQueryClient to return the appropriate response when called
    when(codaQueryClient.getPartyWithAttributesPOST(anyString(), any())).thenReturn(overwriteParties);

    // Act
    P2PCopyResponse response = p2PService.copyRelations(request);

    // Assert
    // Verify that the validation process worked correctly
    assertNotNull(response.getValidationStatus());
    assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
    // Add additional assertions depending on the expected behavior
}

private List<Party> createMockPartiesForOverwriteAction() {
    // Create a list of Party objects that would be returned by the CodaQueryClient for OVERWRITE actions
    List<Party> mockParties = new ArrayList<>();

    Party party1 = new Party();
    party1.setPartyID("BBB02682216");
    party1.setRelatedPartyList(createMockRelatedPartyList());

    mockParties.add(party1);

    // Add more mock Party objects as needed

    return mockParties;
}

private List<RelatedParty> createMockRelatedPartyList() {
    // Create and return a list of related parties that would mimic the expected response
    RelatedParty relatedParty = new RelatedParty();
    relatedParty.setRole1Party(new PartyRole("role1", "partyID"));
    relatedParty.setPartyRelationshipType(new PartyRelationshipType("relationshipTypeID"));

    return Arrays.asList(relatedParty);
}



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class P2PServiceTest {

    @InjectMocks
    private P2PService p2pService;

    @Mock
    private CodaQueryClient codaQueryClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllPartiesSkip() {
        // Mock the CodaQueryClient to return empty data as they should be skipped
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(Collections.emptyList());

        P2PCopyRequest request = createSkipActionRequest();
        P2PCopyResponse response = p2pService.copyRequest(request);

        assertTrue(response.getValidationStatuses().isEmpty(), "No validation statuses should be generated for skipped parties.");
    }

    @Test
    void testAllPartiesOverwrite() {
        // Mock the CodaQueryClient to return relevant data for OVERWRITE actions
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockOverwritePartyData());

        P2PCopyRequest request = createOverwriteActionRequest();
        P2PCopyResponse response = p2pService.copyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for overwrite parties.");
        assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
    }

    @Test
    void testMixedActions() {
        // Mock the CodaQueryClient to return relevant data for mixed actions
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockMixedActionPartyData());

        P2PCopyRequest request = createMixedActionRequest();
        P2PCopyResponse response = p2pService.copyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for valid parties.");
        assertTrue(response.getValidationStatuses().stream().anyMatch(status -> status.getStatus() == P2PCopyStatus.VALIDATION_FAILURE), "There should be validation failures for duplicate relationships.");
        assertTrue(response.getValidationStatuses().stream().anyMatch(status -> status.getStatus() == P2PCopyStatus.VALIDATION_SUCCESS), "There should be validation successes.");
    }

    @Test
    void testPartiesWithDuplicateRelationships() {
        // Mock the CodaQueryClient to return duplicate relationship data
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockDuplicateRelationshipPartyData());

        P2PCopyRequest request = createDuplicateRelationshipRequest();
        P2PCopyResponse response = p2pService.copyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for parties with duplicates.");
        assertEquals(P2PCopyStatus.DUPLICATE_RELATIONSHIP_EXISTS, response.getCopyStatus());
    }

    @Test
    void testPartiesWithNoDuplicateRelationships() {
        // Mock the CodaQueryClient to return data with no duplicate relationships
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockNoDuplicateRelationshipPartyData());

        P2PCopyRequest request = createNoDuplicateRelationshipRequest();
        P2PCopyResponse response = p2pService.copyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for valid parties.");
        assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
    }

    // Helper Methods for Test Data

    private P2PCopyRequest createSkipActionRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02722214");
        party.setAction(P2PCopyAction.SKIP);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    private P2PCopyRequest createOverwriteActionRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02682216");
        party.setAction(P2PCopyAction.OVERWRITE);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    private P2PCopyRequest createMixedActionRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party1 = new P2PCopyTargetParty();
        party1.setTargetPartyId("BBB02682216");
        party1.setAction(P2PCopyAction.OVERWRITE);

        P2PCopyTargetParty party2 = new P2PCopyTargetParty();
        party2.setTargetPartyId("BBB02682217");
        party2.setAction(P2PCopyAction.SKIP_VALIDATION);

        P2PCopyTargetParty party3 = new P2PCopyTargetParty();
        party3.setTargetPartyId("BBB02682218");
        party3.setAction(null);

        request.setTargetParties(Arrays.asList(party1, party2, party3));
        return request;
    }

    private P2PCopyRequest createDuplicateRelationshipRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02682278");
        party.setAction(P2PCopyAction.OVERWRITE);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    private P2PCopyRequest createNoDuplicateRelationshipRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02682216");
        party.setAction(P2PCopyAction.OVERWRITE);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    // Mock Data for CodaQueryClient

    private List<PartyData> mockSkipActionParties() {
        // Return a list of PartyData objects that should be skipped
        return Collections.emptyList();
    }

    private List<PartyData> mockOverwritePartyData() {
        // Return a list of PartyData objects that have overwrite action
        return Arrays.asList(new PartyData("BBB02682216", "ROLE_1"));
    }

    private List<PartyData> mockMixedActionPartyData() {
        // Return a list of PartyData objects with mixed actions
        return Arrays.asList(
                new PartyData("BBB02682216", "ROLE_1"),
                new PartyData("BBB02682217", "ROLE_2"),
                new PartyData("BBB02682218", "ROLE_3")
        );
    }

    private List<PartyData> mockDuplicateRelationshipPartyData() {
        // Return a list of PartyData objects that have duplicate relationships
        return Arrays.asList(new PartyData("BBB02682278", "ROLE_1"));
    }

    private List<PartyData> mockNoDuplicateRelationshipPartyData() {
        // Return a list of PartyData objects with no duplicate relationships
        return Arrays.asList(new PartyData("BBB02682216", "ROLE_1"));
    }
}


----------------------------------
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class P2PServiceTest {

    @InjectMocks
    private P2PService p2pService;

    @Mock
    private CodaQueryClient codaQueryClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllPartiesSkip() {
        // Mock the CodaQueryClient to return empty data as they should be skipped
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(Collections.emptyList());

        P2PCopyRequest request = createSkipActionRequest();
        P2PCopyResponse response = p2pService.validateCopyRequest(request);

        assertTrue(response.getValidationStatuses().isEmpty(), "No validation statuses should be generated for skipped parties.");
    }

    @Test
    void testAllPartiesOverwrite() {
        // Mock the CodaQueryClient to return relevant data for OVERWRITE actions
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockOverwritePartyData());

        P2PCopyRequest request = createOverwriteActionRequest();
        P2PCopyResponse response = p2pService.validateCopyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for overwrite parties.");
        assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
    }

    @Test
    void testMixedActions() {
        // Mock the CodaQueryClient to return relevant data for mixed actions
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockMixedActionPartyData());

        P2PCopyRequest request = createMixedActionRequest();
        P2PCopyResponse response = p2pService.validateCopyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for valid parties.");
        assertTrue(response.getValidationStatuses().stream().anyMatch(status -> status.getStatus() == P2PCopyStatus.VALIDATION_FAILURE), "There should be validation failures for duplicate relationships.");
        assertTrue(response.getValidationStatuses().stream().anyMatch(status -> status.getStatus() == P2PCopyStatus.VALIDATION_SUCCESS), "There should be validation successes.");
    }

    @Test
    void testPartiesWithDuplicateRelationships() {
        // Mock the CodaQueryClient to return duplicate relationship data
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockDuplicateRelationshipPartyData());

        P2PCopyRequest request = createDuplicateRelationshipRequest();
        P2PCopyResponse response = p2pService.validateCopyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for parties with duplicates.");
        assertEquals(P2PCopyStatus.DUPLICATE_RELATIONSHIP_EXISTS, response.getCopyStatus());
    }

    @Test
    void testPartiesWithNoDuplicateRelationships() {
        // Mock the CodaQueryClient to return data with no duplicate relationships
        when(codaQueryClient.getPartyWithAttributesPOST(anyList(), anyList()))
                .thenReturn(mockNoDuplicateRelationshipPartyData());

        P2PCopyRequest request = createNoDuplicateRelationshipRequest();
        P2PCopyResponse response = p2pService.validateCopyRequest(request);

        assertFalse(response.getValidationStatuses().isEmpty(), "Validation statuses should be generated for valid parties.");
        assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
    }

    // Helper Methods for Test Data

    private P2PCopyRequest createSkipActionRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02722214");
        party.setAction(P2PCopyAction.SKIP);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    private P2PCopyRequest createOverwriteActionRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02682216");
        party.setAction(P2PCopyAction.OVERWRITE);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    private P2PCopyRequest createMixedActionRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party1 = new P2PCopyTargetParty();
        party1.setTargetPartyId("BBB02682216");
        party1.setAction(P2PCopyAction.OVERWRITE);

        P2PCopyTargetParty party2 = new P2PCopyTargetParty();
        party2.setTargetPartyId("BBB02682217");
        party2.setAction(P2PCopyAction.SKIP_VALIDATION);

        P2PCopyTargetParty party3 = new P2PCopyTargetParty();
        party3.setTargetPartyId("BBB02682218");
        party3.setAction(null);

        request.setTargetParties(Arrays.asList(party1, party2, party3));
        return request;
    }

    private P2PCopyRequest createDuplicateRelationshipRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02682278");
        party.setAction(P2PCopyAction.OVERWRITE);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    private P2PCopyRequest createNoDuplicateRelationshipRequest() {
        P2PCopyRequest request = new P2PCopyRequest();
        request.setMainPartyId("BBB02722214");

        P2PCopyTargetParty party = new P2PCopyTargetParty();
        party.setTargetPartyId("BBB02682216");
        party.setAction(P2PCopyAction.OVERWRITE);

        request.setTargetParties(Arrays.asList(party));
        return request;
    }

    // Mock Data for CodaQueryClient

    private List<PartyData> mockSkipActionParties() {
        // Return a list of PartyData objects that should be skipped
        return Collections.emptyList();
    }

    private List<PartyData> mockOverwritePartyData() {
        // Return a list of PartyData objects that have overwrite action
        return Arrays.asList(new PartyData("BBB02682216", "ROLE_1"));
    }

    private List<PartyData> mockMixedActionPartyData() {
        // Return a list of PartyData objects with mixed actions
        return Arrays.asList(
                new PartyData("BBB02682216", "ROLE_1"),
                new PartyData("BBB02682217", "ROLE_2"),
                new PartyData("BBB02682218", "ROLE_3")
        );
    }

    private List<PartyData> mockDuplicateRelationshipPartyData() {
        // Return a list of PartyData objects that have duplicate relationships
        return Arrays.asList(new PartyData("BBB02682278", "ROLE_1"));
    }

    private List<PartyData> mockNoDuplicateRelationshipPartyData() {
        // Return a list of PartyData objects with no duplicate relationships
        return Arrays.asList(new PartyData("BBB02682216", "ROLE_1"));
    }
}



import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class P2PServiceTest {

    @InjectMocks
    private P2PService p2pService;

    @Mock
    private CodaQueryClient codaQueryClient;

    @Mock
    private P2P00MTransformer p2p00MTransformer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllPartiesSkip() {
        // Mock the CodaQueryClient to return a list of parties with SKIP actions
        when(codaQueryClient.getPartyWithAttributesPOST(any(), any()))
            .thenReturn(mockSkipActionParties());

        // Call the method and assert results
        P2PCopyResponse response = p2pService.validateCopyRequest(createSkipActionRequest());

        // Assert that no parties were validated
        assertTrue(response.getValidationStatuses().isEmpty());
    }

    @Test
    void testAllPartiesOverwrite() {
        // Mock the CodaQueryClient to return a list of parties with OVERWRITE actions
        when(codaQueryClient.getPartyWithAttributesPOST(any(), any()))
            .thenReturn(mockOverwriteActionParties());

        // Call the method and assert results
        P2PCopyResponse response = p2pService.validateCopyRequest(createOverwriteActionRequest());

        // Assert that the validation was successful and statuses are as expected
        assertFalse(response.getValidationStatuses().isEmpty());
        assertEquals(P2PCopyStatus.VALIDATION_SUCCESS, response.getCopyStatus());
    }

    // Repeat for other scenarios...

    private P2PCopyRequest createSkipActionRequest() {
        // Create a request where all parties have SKIP action
        // Implement this method based on your request format
    }

    private P2PCopyRequest createOverwriteActionRequest() {
        // Create a request where all parties have OVERWRITE action
        // Implement this method based on your request format
    }

    // Mock data helpers
    private List<PartyData> mockSkipActionParties() {
        // Mock your party data with SKIP actions
    }

    private List<PartyData> mockOverwriteActionParties() {
        // Mock your party data with OVERWRITE actions
    }
}




import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class P2PServiceTest {

    @Mock
    private CodaQueryClient codaQueryClient; // Mocking the Coda Query Client

    @InjectMocks
    private P2PService p2pService; // Injecting the service under test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchTargetPartyAndRelationshipMapping() {
        // Mocking the coda response
        Map<String, List<String>> codaResponse = new HashMap<>();
        codaResponse.put("BBB02682978", Arrays.asList("8021501", "8021585", "8021765", "8058647", "8058658"));
        codaResponse.put("BBB02668742", Arrays.asList("8021761"));
        codaResponse.put("BBB03882887", Arrays.asList("8021761"));
        codaResponse.put("BBB02532943", Arrays.asList("8021501", "8058654"));
        codaResponse.put("BBB02633914", Arrays.asList("8058658"));

        when(codaQueryClient.getPartiesByIdWithAttributes(anyList(), anyString())).thenReturn(codaResponse);

        // Call the method under test
        List<String> validatePartyIds = Arrays.asList("BBB02682978", "BBB02668742", "BBB03882887", "BBB02532943", "BBB02633914");
        List<TargetParty> targetParties = p2pService.fetchTargetParty(validatePartyIds);

        // Assertions to validate the relationships
        assertEquals(5, targetParties.size());

        TargetParty party1 = targetParties.get(0);
        assertEquals("BBB02682978", party1.getTargetPartyId());
        assertEquals(5, party1.getTargetPartyRelatedParties().size());

        TargetParty party2 = targetParties.get(1);
        assertEquals("BBB02668742", party2.getTargetPartyId());
        assertEquals(1, party2.getTargetPartyRelatedParties().size());

        // Additional assertions for other parties as necessary
    }
}


private TargetParty relationshipIdsByRelatedParty(List<String> codaResponse) {
    // Logic to parse the list of strings and create the TargetParty object
    // This is a placeholder; actual implementation depends on your logic.
    // Example:
    String targetPartyId = "BBB02682978"; // Extract from first entry
    List<String> relatedParties = Arrays.asList("8021501", "8021585", "8021765", "8058647", "8058658"); // Parse from first entry

    return TargetParty.builder()
            .targetPartyId(targetPartyId)
            .targetPartyRelatedParties(relatedParties.stream()
                    .map(id -> new TargetPartyRelatedParty(id))
                    .collect(Collectors.toList()))
            .build();
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class P2PServiceTest {

    @Mock
    private CodaQueryClient codaQueryClient; // Mocking the Coda Query Client

    @InjectMocks
    private P2PService p2pService; // Injecting the service under test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRelationshipIdsByRelatedParty() {
        // Mocking the coda response as a list of strings
        List<String> codaResponse = Arrays.asList(
            "BBB02682978=8021501,8021585,8021765,8058647,8058658",
            "BBB02668742=8021761",
            "BBB03882887=8021761",
            "BBB02532943=8021501,8058654",
            "BBB02633914=8058658"
        );

        when(codaQueryClient.getPartiesByIdWithAttributes(anyList(), anyString())).thenReturn(codaResponse);

        // Call the method under test
        TargetParty targetParty = p2pService.relationshipIdsByRelatedParty(codaResponse);

        // Assertions
        assertEquals("BBB02682978", targetParty.getTargetPartyId());
        assertEquals(5, targetParty.getTargetPartyRelatedParties().size());
        
        // Additional assertions can be added based on expected output
    }
}

---------------------

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class P2PServiceTest {

    @Mock
    private CodaQueryClient codaQueryClient; // Mocking the Coda Query Client

    @InjectMocks
    private P2PService p2pService; // Injecting the service under test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRelationshipIdsByRelatedParty() {
        // Mocking the coda response
        Map<String, List<String>> codaResponse = new HashMap<>();
        codaResponse.put("BBB02682978", Arrays.asList("8021501", "8021585", "8021765", "8058647", "8058658"));
        codaResponse.put("BBB02668742", Collections.singletonList("8021761"));
        codaResponse.put("BBB03882887", Collections.singletonList("8021761"));
        codaResponse.put("BBB02532943", Arrays.asList("8021501", "8058654"));
        codaResponse.put("BBB02633914", Collections.singletonList("8058658"));

        when(codaQueryClient.getPartiesByIdWithAttributes(anyList(), anyString())).thenReturn(codaResponse);

        // Calling the method under test
        TargetParty targetParty = p2pService.relationshipIdsByRelatedParty(codaResponse);

        // Assertions
        assertEquals("BBB02682978", targetParty.getTargetPartyId());
        assertEquals(5, targetParty.getTargetPartyRelatedParties().size());
        // You can continue to assert other expected values here
    }
}




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

