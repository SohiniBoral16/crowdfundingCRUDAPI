@Test
void getP2PRelationshipHierarchyWithNullPartyId() {
    // Mocking the root party with a null partyId
    Party mockRootParty = preparePartyObject("", "");  // passing empty string or null for the party ID
    when(codaQueryClient.getPartyWithAttributesPOST(anyString(), anyList())).thenReturn(mockRootParty);
    
    // Now call the method and ensure it handles null partyId properly
    P2PHierarchyParty result = p2PHierarchyService.getP2PRelationshipHierarchy("BBB12345457");
    
    // Assert that the result is either null or empty, depending on your method's behavior
    assertNull(result.getPartyId(), "PartyId should be null for root party with null ID");
    assertTrue(result.getP2PHierarchyRelationship().isEmpty(), "Relationships should be empty when partyId is null");
}


@Test
void testPublicMethodThatUsesP2PHierarchyRelationshipAttributesMapperWithNullValues() {
    // Mock PartyToPartyRelationship with null values for fields
    PartyToPartyRelationship mockRelationship = mock(PartyToPartyRelationship.class);
    when(mockRelationship.getPartyRelationshipType()).thenReturn(null); // Null field
    when(mockRelationship.getJapanUltimateBeneficialOwnerApplicability()).thenReturn(null); // Null field
    when(mockRelationship.getPercentOfBeneficialOwnership()).thenReturn(45); // Valid field

    // Mock the public method that calls the private mapper internally
    List<PartyToPartyRelationship> mockRelationshipList = List.of(mockRelationship);
    Party mockParty = mock(Party.class);
    when(mockParty.getRelatedPartyList()).thenReturn(mockRelationshipList);

    // Call the public method that uses the private mapper
    P2PHierarchyParty result = p2pHierarchyService.getP2PHierarchyParty(mockParty);

    // Now check the fields in result, which indirectly confirms the private mapper's output
    assertNotNull(result);
    assertEquals(1, result.getP2PHierarchyRelationship().size());

    // Access the mapped relationship attributes and validate them
    P2PHierarchyRelationshipAttributes attributes = result.getP2PHierarchyRelationship().values().stream()
            .findFirst()
            .orElseThrow()
            .getRelationshipAttributes()
            .get(0);

    // Validate that null values were handled correctly
    assertNull(attributes.getRelationshipTypeId()); // Null field
    assertNull(attributes.getJapanUltimateBeneficialOwnerApplicability()); // Null field
    assertEquals(45, attributes.getPercentOfBeneficialOwnership()); // Non-null field
}

@Test
void testPublicMethodThatUsesP2PHierarchyRelationshipAttributesMapperWithMixedValues() {
    // Mock PartyToPartyRelationship with mixed null and valid values
    PartyToPartyRelationship mockRelationship = mock(PartyToPartyRelationship.class);
    when(mockRelationship.getPartyRelationshipType()).thenReturn(null); // Null value
    when(mockRelationship.getPercentOfBeneficialOwnership()).thenReturn(null); // Null value
    when(mockRelationship.getPercentOfAnnualOperatingCostFromMajorDonor()).thenReturn(25); // Valid value

    // Mock the public method that uses the private mapper internally
    List<PartyToPartyRelationship> mockRelationshipList = List.of(mockRelationship);
    Party mockParty = mock(Party.class);
    when(mockParty.getRelatedPartyList()).thenReturn(mockRelationshipList);

    // Call the public method
    P2PHierarchyParty result = p2pHierarchyService.getP2PHierarchyParty(mockParty);

    // Now check the fields in result, which indirectly confirms the private mapper's output
    assertNotNull(result);
    assertEquals(1, result.getP2PHierarchyRelationship().size());

    // Access the mapped relationship attributes and validate them
    P2PHierarchyRelationshipAttributes attributes = result.getP2PHierarchyRelationship().values().stream()
            .findFirst()
            .orElseThrow()
            .getRelationshipAttributes()
            .get(0);

    // Validate that null values were handled correctly
    assertNull(attributes.getRelationshipTypeId()); // Null value
    assertNull(attributes.getPercentOfBeneficialOwnership()); // Null value
    assertEquals(25, attributes.getPercentOfAnnualOperatingCostFromMajorDonor()); // Non-null value
}

---------------------------------------
Optional.ofNullable(relatedParty)
        .filter(list -> !list.isEmpty()) // Check if the relatedParty list is not empty
        .ifPresent(list -> setRelatedPartyList(createRelatedParties(party, list))); // Call createRelatedParties if list is present and non-empty


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class P2PHierarchyServiceTest {

    @Mock
    private CodaQueryClient codaQueryClient;  // Mock the external dependency

    @InjectMocks
    private P2PHierarchyService p2PHierarchyService;  // The service you're testing

    private Party mockRootParty;
    private Map<String, Party> mockCodaDetails;

    @BeforeEach
    public void setUp() {
        // Initialize mockRootParty and mockCodaDetails with sample data
        mockRootParty = new Party();
        mockRootParty.setPartyID("rootPartyID");
        mockRootParty.setPartyNameList(Collections.singletonList(new PartyName("Root Party")));

        Party childParty1 = new Party();
        childParty1.setPartyID("childParty1ID");
        childParty1.setPartyNameList(Collections.singletonList(new PartyName("Child Party 1")));

        Party childParty2 = new Party();
        childParty2.setPartyID("childParty2ID");
        childParty2.setPartyNameList(Collections.singletonList(new PartyName("Child Party 2")));

        // Populate mockCodaDetails
        mockCodaDetails = new HashMap<>();
        mockCodaDetails.put(mockRootParty.getPartyID(), mockRootParty);
        mockCodaDetails.put(childParty1.getPartyID(), childParty1);
        mockCodaDetails.put(childParty2.getPartyID(), childParty2);

        // Mock the external codaQueryClient calls
        when(codaQueryClient.getPartyWithAttributesPOST("rootPartyID", "VISUALIZATION_DOM_ATTRIBUTES"))
                .thenReturn(mockRootParty);

        when(codaQueryClient.getPartiesWithAttributesPOST(anyList(), eq("VISUALIZATION_DOM_ATTRIBUTES")))
                .thenReturn(Arrays.asList(childParty1, childParty2));
    }

    @Test
    public void testGetP2PRelationshipHierarchy() {
        // Call the method under test
        P2PHierarchyParty result = p2PHierarchyService.getP2PRelationshipHierarchy("rootPartyID");

        // Assert the results - you can expand this to assert on child relationships as needed
        assertNotNull(result);
        assertEquals("rootPartyID", result.getPartyId());
        assertEquals("Root Party", result.getPartyName());

        // Verify that the mock methods were called as expected
        verify(codaQueryClient).getPartyWithAttributesPOST("rootPartyID", "VISUALIZATION_DOM_ATTRIBUTES");
        verify(codaQueryClient).getPartiesWithAttributesPOST(anyList(), eq("VISUALIZATION_DOM_ATTRIBUTES"));
    }
}




/**
 * This method builds the hierarchical relationships for a given party using BFS traversal.
 * It converts the party relationship data retrieved from the Coda service (codaDetails) 
 * into a structured P2PHierarchyParty object, including its relationships.
 *
 * Steps:
 * 1. Fetch the root party details and its related parties.
 * 2. For each related party, recursively retrieve its attributes and relationships from 
 *    the Coda service and construct a hierarchy map.
 * 3. Use a BFS approach to avoid recursion and iteratively fetch each child party’s details 
 *    and relationships.
 * 4. Populate the hierarchy map with the party's child relationships and associated attributes.
 * 5. Return the fully populated P2PHierarchyParty object with all relationships.
 *
 * @param partyId The ID of the root party for which the hierarchy needs to be constructed.
 * @return A P2PHierarchyParty object representing the entire hierarchical structure of the party.
 */




public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Initialize the map to hold coda details and populate with root party attributes
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_OM_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    // Initialize a queue for BFS traversal
    Queue<String> p2PHierarchyPartyQueue = new LinkedList<>();
    List<String> relatedPartyIds = rootParty.getRelatedPartyList().stream()
        .map(p -> p.getRole1party().getPartyID())
        .collect(Collectors.toList());
    p2PHierarchyPartyQueue.addAll(relatedPartyIds);

    // List to keep track of child parties
    List<Party> childParties = new ArrayList<>();

    // BFS traversal to process the hierarchy
    while (!p2PHierarchyPartyQueue.isEmpty()) {
        List<String> partyIds = Utils.pollAll(p2PHierarchyPartyQueue);

        // Fetch child parties using a batch API call
        List<Party> childPartiesFromAPI = codaQueryClient.getPartiesWithAttributesPOST(partyIds, VISUALIZATION_OM_ATTRIBUTES);

        for (Party childParty : childPartiesFromAPI) {
            codaDetails.put(childParty.getPartyID(), childParty);

            // Filter and collect child related party IDs that are not already processed
            List<String> childRelatedPartyIds = childParty.getRelatedPartyList().stream()
                .filter(x -> !codaDetails.containsKey(x.getRole1party().getPartyID()))
                .map(p -> p.getRole1party().getPartyID())
                .collect(Collectors.toList());

            // Update child party's relationship and queue up for further processing
            childParty.setRelatedPartyList(childRelatedPartyIds);
            p2PHierarchyPartyQueue.addAll(childRelatedPartyIds);
        }
    }

    return mapToP2PHierarchyParty(codaDetails, rootParty);
}



private P2PHierarchyParty mapToP2PHierarchyParty(Map<String, Party> codaDetails, Party rootParty) {
    // Initialize the root hierarchy party object
    var rootHierarchyParty = getP2PHierarchyParty(rootParty);
    var hierarchyPartyQueue = new LinkedList<P2PHierarchyParty>();

    // Start BFS mapping with the root party
    hierarchyPartyQueue.add(rootHierarchyParty);

    // BFS traversal for mapping to the hierarchy structure
    while (!hierarchyPartyQueue.isEmpty()) {
        var currentHierarchyParty = hierarchyPartyQueue.poll();
        var currentParty = codaDetails.get(currentHierarchyParty.getPartyId());

        // Use Optional to avoid null checks
        Optional.ofNullable(currentParty).ifPresent(party -> {
            var currentRelationshipMap = currentHierarchyParty.getP2PHierarchyRelationship();

            // For each related party of the current party, map the relationships
            party.getRelatedPartyList().forEach(relationship -> {
                var childParty = Optional.ofNullable(codaDetails.get(relationship.getRole1party().getPartyID()));

                childParty.ifPresent(cp -> {
                    var childHierarchyParty = getP2PHierarchyParty(cp);

                    // Use computeIfAbsent to ensure new relationships are added when necessary
                    var existingRelationship = currentRelationshipMap.computeIfAbsent(
                            cp.getPartyID(),
                            id -> new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>())
                    );

                    // Add relationship attributes
                    existingRelationship.getRelationshipAttributes()
                            .add(getRelationshipAttributes(relationship));

                    // Add the child party to the BFS queue for further processing
                    hierarchyPartyQueue.add(childHierarchyParty);
                });
            });
        });
    }

    return rootHierarchyParty;
}

private P2PHierarchyRelationshipAttributes getRelationshipAttributes(PartyToPartyRelationship relationship) {
    return P2PHierarchyRelationshipAttributes.builder()
            .relationshipTypeId(relationship.getRelationshipType().getId())
            .relationshipTypeName(relationship.getRelationshipType().getName())
            .percentBeneficialOwnership(relationship.getOwnershipPercentage())
            .build();
}





private P2PHierarchyParty mapToP2PHierarchyParty(Map<String, Party> codaDetails, Party rootParty) {
    // Initialize the root hierarchy party object
    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);
    Queue<P2PHierarchyParty> hierarchyPartyQueue = new LinkedList<>();

    // Start BFS mapping with the root party
    hierarchyPartyQueue.add(rootHierarchyParty);

    // BFS traversal for mapping to the hierarchy structure
    while (!hierarchyPartyQueue.isEmpty()) {
        P2PHierarchyParty currentHierarchyParty = hierarchyPartyQueue.poll();
        Party currentParty = codaDetails.get(currentHierarchyParty.getPartyId());

        // Get the existing relationships for the current party
        Map<String, P2PHierarchyRelationship> currentRelationshipMap = currentHierarchyParty.getP2PHierarchyRelationship();

        // For each related party of the current party, map the relationships
        for (PartyToPartyRelationship relationship : currentParty.getRelatedPartyList()) {
            Party childParty = codaDetails.get(relationship.getRole1party().getPartyID());

            if (childParty != null) {
                P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);

                // Check if the relationship already exists for this child party
                P2PHierarchyRelationship existingRelationship = currentRelationshipMap.get(childParty.getPartyID());

                if (existingRelationship == null) {
                    // If it doesn't exist, create a new relationship
                    existingRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());
                    currentRelationshipMap.put(childParty.getPartyID(), existingRelationship);
                }

                // Add relationship attributes from PartyToPartyRelationship to the existing relationship
                existingRelationship.getRelationshipAttributes().add(getRelationshipAttributes(relationship));

                // Add the child party to the BFS queue for further processing
                hierarchyPartyQueue.add(childHierarchyParty);
            }
        }
    }

    return rootHierarchyParty;
}

private P2PHierarchyRelationshipAttributes getRelationshipAttributes(PartyToPartyRelationship relationship) {
    return P2PHierarchyRelationshipAttributes.builder()
            .relationshipTypeId(relationship.getRelationshipType().getId())
            .relationshipTypeName(relationship.getRelationshipType().getName())
            .percentBeneficialOwnership(relationship.getOwnershipPercentage())
            .build();
}




private P2PHierarchyParty mapToP2PHierarchyParty(Map<String, Party> codaDetails, Party rootParty) {
    // Initialize the root hierarchy party object
    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);
    Queue<P2PHierarchyParty> hierarchyPartyQueue = new LinkedList<>();

    // Start BFS mapping with the root party
    hierarchyPartyQueue.add(rootHierarchyParty);

    // BFS traversal for mapping to the hierarchy structure
    while (!hierarchyPartyQueue.isEmpty()) {
        P2PHierarchyParty currentHierarchyParty = hierarchyPartyQueue.poll();
        Party currentParty = codaDetails.get(currentHierarchyParty.getPartyId());

        // Get the existing relationships for the current party
        Map<String, P2PHierarchyRelationship> currentRelationshipMap = currentHierarchyParty.getP2PHierarchyRelationship();

        // For each related party of the current party, map the relationships
        for (PartyToPartyRelationship relationship : currentParty.getRelatedPartyList()) {
            Party childParty = codaDetails.get(relationship.getRole1party().getPartyID());

            if (childParty != null) {
                P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);

                // Add the child relationship to the current party
                P2PHierarchyRelationship newRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());

                // Add relationship attributes from PartyToPartyRelationship to the new relationship
                newRelationship.getRelationshipAttributes().add(getRelationshipAttributes(relationship));

                // Add the new relationship to the existing map (map is final, but its content can be modified)
                currentRelationshipMap.put(childParty.getPartyID(), newRelationship);

                // Add the child party to the BFS queue for further processing
                hierarchyPartyQueue.add(childHierarchyParty);
            }
        }
    }

    return rootHierarchyParty;
}

private P2PHierarchyRelationshipAttributes getRelationshipAttributes(PartyToPartyRelationship relationship) {
    return P2PHierarchyRelationshipAttributes.builder()
            .relationshipTypeId(relationship.getRelationshipType().getId())
            .relationshipTypeName(relationship.getRelationshipType().getName())
            .percentBeneficialOwnership(relationship.getOwnershipPercentage())
            .build();
}




public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Step 1: Get the root party and related details
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_DOM_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    // Step 2: Collect related party IDs and initialize the processing queue
    Queue<String> p2pHierarchyPartyQueue = new LinkedList<>();
    List<String> relatedPartyIds = rootParty.getRelatedPartyList()
        .stream()
        .map(p -> p.getRole1party().getPartyID())
        .collect(Collectors.toList());
    p2pHierarchyPartyQueue.addAll(relatedPartyIds);

    // Step 3: BFS traversal for fetching all party data (multi-level)
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        String currentPartyId = p2pHierarchyPartyQueue.poll();
        Party currentParty = codaDetails.get(currentPartyId);

        if (currentParty == null) {
            currentParty = codaQueryClient.getPartyWithAttributesPOST(currentPartyId, VISUALIZATION_DOM_ATTRIBUTES);
            codaDetails.put(currentPartyId, currentParty);
        }

        // Add related party IDs of the current party to the queue for further processing
        if (!currentParty.getRelatedPartyList().isEmpty()) {
            List<String> childPartyIds = currentParty.getRelatedPartyList()
                .stream()
                .map(p -> p.getRole1party().getPartyID())
                .collect(Collectors.toList());
            p2pHierarchyPartyQueue.addAll(childPartyIds);
        }
    }

    // Step 4: BFS traversal for mapping the data to the P2PHierarchyParty model
    return mapToP2PHierarchyParty(codaDetails, rootParty);
}

private P2PHierarchyParty mapToP2PHierarchyParty(Map<String, Party> codaDetails, Party rootParty) {
    // Initialize the root hierarchy party object
    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);
    Map<String, P2PHierarchyRelationship> p2pHierarchyRelationshipMap = new HashMap<>();
    Queue<P2PHierarchyParty> hierarchyPartyQueue = new LinkedList<>();

    // Start BFS mapping with the root party
    hierarchyPartyQueue.add(rootHierarchyParty);

    // BFS traversal for mapping to the hierarchy structure
    while (!hierarchyPartyQueue.isEmpty()) {
        P2PHierarchyParty currentHierarchyParty = hierarchyPartyQueue.poll();
        Party currentParty = codaDetails.get(currentHierarchyParty.getPartyId());

        // Get or initialize relationships for the current party
        Map<String, P2PHierarchyRelationship> currentRelationshipMap = currentHierarchyParty.getP2PHierarchyRelationship();
        if (currentRelationshipMap == null) {
            currentRelationshipMap = new HashMap<>();
            currentHierarchyParty.setP2PHierarchyRelationship(currentRelationshipMap);
        }

        // For each related party of the current party, map the relationships
        for (PartyToPartyRelationship relationship : currentParty.getRelatedPartyList()) {
            Party childParty = codaDetails.get(relationship.getRole1party().getPartyID());

            if (childParty != null) {
                P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);

                // Add the child relationship to the current party
                P2PHierarchyRelationship newRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());
                currentRelationshipMap.put(childParty.getPartyID(), newRelationship);

                // Add the child party to the BFS queue for further processing
                hierarchyPartyQueue.add(childHierarchyParty);
            }
        }
    }

    return rootHierarchyParty;
}




public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Step 1: Get the root party and related details
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_DOM_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    // Step 2: Collect related party IDs and initialize the processing queue
    Queue<String> p2pHierarchyPartyQueue = new LinkedList<>();
    List<String> relatedPartyIds = rootParty.getRelatedPartyList()
        .stream()
        .map(p -> p.getRole1party().getPartyID())
        .collect(Collectors.toList());
    p2pHierarchyPartyQueue.addAll(relatedPartyIds);

    // Step 3: BFS traversal for multilevel hierarchy processing
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        String currentPartyId = p2pHierarchyPartyQueue.poll();
        Party currentParty = codaDetails.get(currentPartyId);

        // Fetch the child parties for the current party if not already fetched
        if (currentParty == null) {
            currentParty = codaQueryClient.getPartyWithAttributesPOST(currentPartyId, VISUALIZATION_DOM_ATTRIBUTES);
            codaDetails.put(currentPartyId, currentParty);
        }

        // Add related party IDs of the current party to the queue for further processing
        if (!currentParty.getRelatedPartyList().isEmpty()) {
            List<String> childPartyIds = currentParty.getRelatedPartyList()
                .stream()
                .map(p -> p.getRole1party().getPartyID())
                .collect(Collectors.toList());
            p2pHierarchyPartyQueue.addAll(childPartyIds);
        }
    }

    // Step 4: Map the coda details into a hierarchical structure using the relationship map
    return mapToP2PHierarchyParty(codaDetails, rootParty);
}

private P2PHierarchyParty mapToP2PHierarchyParty(Map<String, Party> codaDetails, Party rootParty) {
    // Initialize the root hierarchy party object
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);
    Map<String, P2PHierarchyRelationship> p2pHierarchyRelationshipMap = new HashMap<>();

    // Traverse the coda details and construct the hierarchy
    for (String partyId : codaDetails.keySet()) {
        Party party = codaDetails.get(partyId);
        P2PHierarchyRelationship relationship = new P2PHierarchyRelationship(getP2PHierarchyParty(party), new ArrayList<>());
        p2pHierarchyRelationshipMap.put(partyId, relationship);

        // Process child relationships if available
        if (!party.getRelatedPartyList().isEmpty()) {
            Map<String, P2PHierarchyRelationship> childRelationshipMap = new HashMap<>();
            for (PartyToPartyRelationship relatedParty : party.getRelatedPartyList()) {
                String childPartyId = relatedParty.getRole1party().getPartyID();
                Party childParty = codaDetails.get(childPartyId);

                // Ensure the child party is processed and added
                if (childParty != null) {
                    childRelationshipMap.put(childPartyId, new P2PHierarchyRelationship(getP2PHierarchyParty(childParty), new ArrayList<>()));
                }
            }
            // Set the child relationships to the current party's relationships
            relationship.getChildParty().setP2PHierarchyRelationship(childRelationshipMap);
        }
    }

    // Attach the relationship map to the root party
    p2pHierarchyParty.getP2PHierarchyRelationship().putAll(p2pHierarchyRelationshipMap);
    return p2pHierarchyParty;
}





// This method builds the hierarchical structure using BFS without recursion
public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Step 1: Get the root party and related details
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_DOM_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    // Step 2: Collect related party IDs and initialize the processing queue
    Queue<String> p2pHierarchyPartyQueue = new LinkedList<>();
    List<String> relatedPartyIds = rootParty.getRelatedPartyList()
        .stream()
        .map(p -> p.getRole1party().getPartyID())
        .collect(Collectors.toList());
    p2pHierarchyPartyQueue.addAll(relatedPartyIds);

    // Step 3: Prepare child parties collection for BFS traversal
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        String currentPartyId = p2pHierarchyPartyQueue.poll();
        Party currentParty = codaDetails.get(currentPartyId);

        // Fetch the child parties for the current party if not already fetched
        if (currentParty == null) {
            currentParty = codaQueryClient.getPartyWithAttributesPOST(currentPartyId, VISUALIZATION_DOM_ATTRIBUTES);
            codaDetails.put(currentPartyId, currentParty);
        }

        // Check if the current party has related child parties
        if (!currentParty.getRelatedPartyList().isEmpty()) {
            // Add child party IDs to the queue for further processing
            p2pHierarchyPartyQueue.addAll(
                currentParty.getRelatedPartyList().stream()
                    .map(p -> p.getRole1party().getPartyID())
                    .collect(Collectors.toList())
            );
        }
    }

    // Step 4: After fetching all the details, map them into the hierarchy model
    return mapToP2PHierarchyParty(codaDetails, rootParty);
}

private P2PHierarchyParty mapToP2PHierarchyParty(Map<String, Party> codaDetails, Party rootParty) {
    // Initialize the root hierarchy party object
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);
    Map<String, P2PHierarchyRelationship> p2pHierarchyRelationshipMap = new HashMap<>();

    // Traverse the coda details and construct the hierarchy
    for (String partyId : codaDetails.keySet()) {
        Party party = codaDetails.get(partyId);
        P2PHierarchyRelationship relationship = new P2PHierarchyRelationship(getP2PHierarchyParty(party), new ArrayList<>());
        p2pHierarchyRelationshipMap.put(partyId, relationship);

        // Process child relationships if available
        if (!party.getRelatedPartyList().isEmpty()) {
            Map<String, P2PHierarchyRelationship> childRelationshipMap = new HashMap<>();
            for (PartyToPartyRelationship relatedParty : party.getRelatedPartyList()) {
                String childPartyId = relatedParty.getRole1party().getPartyID();
                Party childParty = codaDetails.get(childPartyId);

                // Ensure the child party is processed and added
                if (childParty != null) {
                    childRelationshipMap.put(childPartyId, new P2PHierarchyRelationship(getP2PHierarchyParty(childParty), new ArrayList<>()));
                }
            }
            // Set the child relationships to the current party
            relationship.getChildParty().setP2PHierarchyRelationship(childRelationshipMap);
        }
    }

    // Attach the relationship map to the root party
    p2pHierarchyParty.getP2PHierarchyRelationship().putAll(p2pHierarchyRelationshipMap);
    return p2pHierarchyParty;
}


-------------------------------
// Check if the child party has related parties, i.e., it's not a leaf node
if (!childParty.getRelatedPartyList().isEmpty()) {
    // Ensure that the relationship map is initialized for this party
    P2PHierarchyRelationship existingRelationship = p2pHierarchyRelationshipMap.get(partyId1);
    if (existingRelationship == null) {
        existingRelationship = new P2PHierarchyRelationship(getP2PHierarchyParty(childParty), new ArrayList<>());
        p2pHierarchyRelationshipMap.put(partyId1, existingRelationship);
    }

    // Get the child party object from the existing relationship
    P2PHierarchyParty existingChildParty = existingRelationship.getChildParty();

    // If the child party's internal relationship map is null, initialize it
    if (existingChildParty.getP2PHierarchyRelationship() == null) {
        existingChildParty.setP2PHierarchyRelationship(new HashMap<>());
    }

    // Populate the child party's internal relationship map with related parties
    existingChildParty.getP2PHierarchyRelationship().putAll(
        getRelationshipAttributesBetter1(codaDetails, childParty.getRelatedPartyList())
    );

    // Add related parties to the queue for further processing
    p2pHierarchyPartyQueue2.addAll(
        childParty.getRelatedPartyList().stream()
            .map(x -> x.getRole1party().getPartyID())
            .collect(Collectors.toList())
    );
} else {
    // If no related parties, ensure the relationship map is initialized with an empty structure
    p2pHierarchyRelationshipMap.putIfAbsent(
        partyId1, 
        new P2PHierarchyRelationship(getP2PHierarchyParty(childParty), new ArrayList<>())
    );
}





while (!p2pHierarchyPartyQueue2.isEmpty()) {
    // Poll the next party ID from the queue
    for (String partyId1 : Utils.pollAll(p2pHierarchyPartyQueue2)) {
        // Retrieve the corresponding child party from codaDetails
        Party childParty = codaDetails.get(partyId1);

        // Check if the child party has related parties, i.e., it's not a leaf node
        if (!childParty.getRelatedPartyList().isEmpty()) {
            // Ensure that the relationship map is initialized for this party
            P2PHierarchyRelationship existingRelationship = p2pHierarchyRelationshipMap.get(partyId1);
            if (existingRelationship == null) {
                existingRelationship = new P2PHierarchyRelationship(getP2PHierarchyParty(childParty), new ArrayList<>());
                p2pHierarchyRelationshipMap.put(partyId1, existingRelationship);
            }

            // Get or initialize the child relationship map
            Map<String, P2PHierarchyRelationship> childRelationshipMap = existingRelationship.getP2PHierarchyRelationship();
            if (childRelationshipMap == null) {
                childRelationshipMap = new HashMap<>();
                existingRelationship.setP2PHierarchyRelationship(childRelationshipMap);
            }

            // Populate the child relationship map with related parties
            childRelationshipMap.putAll(getRelationshipAttributesBetter1(codaDetails, childParty.getRelatedPartyList()));

            // Add related parties to the queue for further processing
            p2pHierarchyPartyQueue2.addAll(
                childParty.getRelatedPartyList().stream()
                    .map(x -> x.getRole1party().getPartyID())
                    .collect(Collectors.toList())
            );
        } else {
            // If no related parties, ensure the relationship map is initialized with an empty structure
            p2pHierarchyRelationshipMap.putIfAbsent(
                partyId1, 
                new P2PHierarchyRelationship(getP2PHierarchyParty(childParty), new ArrayList<>())
            );
        }
    }
}


-------------------------++-------------+
while (!p2pHierarchyPartyQueue2.isEmpty()) {
    // Poll the next party from the queue
    for (String partyId1 : Utils.pollAll(p2pHierarchyPartyQueue2)) {
        Party childParty = codaDetails.get(partyId1);  // Fetch child party from codaDetails

        // Check if the relationship is empty or null
        if (p2pHierarchyRelationshipMap.get(partyId1) == null || 
            p2pHierarchyRelationshipMap.get(partyId1).getP2PHierarchyRelationship().isEmpty()) {

            // If the relationship is empty, create a new relationship entry
            P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);  // Convert to P2PHierarchyParty
            P2PHierarchyRelationship newRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());
            
            // Insert this new relationship into the map
            p2pHierarchyRelationshipMap.put(partyId1, newRelationship);

            // Insert the child party’s related party list into the queue for further processing
            List<String> relatedPartyIds = childParty.getRelatedPartyList().stream()
                .map(x -> x.getRole1party().getPartyID())
                .collect(Collectors.toList());

            // If there are related parties, add them to the queue
            if (!relatedPartyIds.isEmpty()) {
                p2pHierarchyPartyQueue2.addAll(relatedPartyIds);
            }

            // Also, populate the relationship with attributes (if any)
            p2pHierarchyRelationshipMap.get(partyId1).getP2PHierarchyRelationship()
                .putAll(getRelationshipAttributesBetter1(codaDetails, childParty.getRelatedPartyList()));
        }
    }
}





public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Step 1: Get the root party details from coda and store them in codaDetails
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_JOIN_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    // Convert root party into P2PHierarchyParty object
    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);

    // Step 2: Initialize queue for BFS
    Queue<P2PHierarchyParty> p2pHierarchyPartyQueue = new LinkedList<>();
    List<String> relatedPartyIds = rootParty.getRelatedPartyList().stream()
            .map(p -> p.getRole1party().getPartyID())
            .collect(Collectors.toList());

    // Add the related parties to the queue for BFS processing
    p2pHierarchyPartyQueue.addAll(getP2PHierarchyParties(relatedPartyIds));

    List<PartyToPartyRelationship> childPartiesRelationships;

    // Step 3: BFS traversal to map the hierarchy
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        P2PHierarchyParty currentHierarchyParty = p2pHierarchyPartyQueue.poll();  // Dequeue a party
        
        // Step 3a: Fetch child parties for the current hierarchy party
        childPartiesRelationships = currentHierarchyParty.getRelatedPartyList();  // Get the related party list

        // Fetch child party IDs from relationships
        List<String> childPartyIds = childPartiesRelationships.stream()
            .map(p -> p.getRole1party().getPartyID())
            .collect(Collectors.toList());

        // Fetch child parties from codaDetails (already fetched) 
        List<Party> childParties = childPartyIds.stream()
            .map(codaDetails::get)  // Get the Party object from codaDetails
            .filter(Objects::nonNull)  // Ensure we only process non-null entries
            .collect(Collectors.toList());

        // Step 3b: For each child party, convert to P2PHierarchyParty and map the relationships
        for (Party childParty : childParties) {
            P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);  // Convert Party to P2PHierarchyParty

            // Create the relationship between parent and child
            P2PHierarchyRelationship p2pHierarchyRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());
            currentHierarchyParty.getP2PHierarchyRelationship().put(childParty.getPartyID(), p2pHierarchyRelationship);

            // If the child party has its own related parties (children), add to the queue for further processing
            List<String> childRelatedPartyIds = childParty.getRelatedPartyList().stream()
                .map(p -> p.getRole1party().getPartyID())  // Fetch Role1party (child)
                .collect(Collectors.toList());

            if (!childRelatedPartyIds.isEmpty()) {
                // Set the related parties for the child party and add it to the queue
                childHierarchyParty.setRelatedPartyList(childParty.getRelatedPartyList());
                p2pHierarchyPartyQueue.add(childHierarchyParty);  // Enqueue the child party for further processing
            }
        }
    }

    // Step 4: After processing all the parties, return the root of the hierarchy
    return rootHierarchyParty;
}





public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Step 1: Get the root party details
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_JOIN_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    // Convert the root party to P2PHierarchyParty and add to the hierarchy
    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);

    // Step 2: Initialize queue to maintain BFS traversal for multilevel data
    Queue<P2PHierarchyParty> p2pHierarchyPartyQueue = new LinkedList<>();
    p2pHierarchyPartyQueue.add(rootHierarchyParty);  // Add the root to the queue

    // Step 3: Begin BFS traversal
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        P2PHierarchyParty currentHierarchyParty = p2pHierarchyPartyQueue.poll();  // Process each party in the queue
        
        // Fetch child party IDs for the current hierarchy party
        List<String> childPartyIds = currentHierarchyParty.getRelatedPartyList().stream()
            .filter(p -> p.getRole1party() != null)  // Only process if Role1party exists (i.e., child exists)
            .map(p -> p.getRole1party().getPartyID())
            .collect(Collectors.toList());

        // Fetch child parties from codaDetails or make an API call if not present
        List<Party> childParties = codaQueryClient.getPartyWithAttributesPOST(childPartyIds, VISUALIZATION_JOIN_ATTRIBUTES);
        childParties.forEach(childParty -> codaDetails.putIfAbsent(childParty.getPartyID(), childParty));

        // Convert each child Party to P2PHierarchyParty and map relationships
        for (Party childParty : childParties) {
            P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);  // Convert Party to P2PHierarchyParty

            // Handle relationship mapping: create or update the P2PHierarchyRelationship for the current party
            P2PHierarchyRelationship p2pHierarchyRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());
            currentHierarchyParty.getP2PHierarchyRelationship().put(childParty.getPartyID(), p2pHierarchyRelationship);

            // Set up the related party list for the child party and add it to the queue for further processing
            List<String> childRelatedPartyIds = childParty.getRelatedPartyList().stream()
                .map(p -> p.getRole1party().getPartyID())  // Fetch child party IDs
                .collect(Collectors.toList());

            // If there are children, add the child party to the queue
            if (!childRelatedPartyIds.isEmpty()) {
                childHierarchyParty.setRelatedPartyList(childParty.getRelatedPartyList());  // Set the child parties
                p2pHierarchyPartyQueue.add(childHierarchyParty);  // Add child to the queue for further processing
            }
        }
    }

    return rootHierarchyParty;
}





public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Step 1: Get the root party details
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_JOIN_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    // Convert the root party to P2PHierarchyParty and add to the hierarchy
    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);

    // Step 2: Initialize queue to maintain BFS traversal for multilevel data
    Queue<P2PHierarchyParty> p2pHierarchyPartyQueue = new LinkedList<>();
    List<String> relatedPartyIds = rootParty.getRelatedPartyList().stream()
            .map(p -> p.getRole1party().getPartyID())
            .collect(Collectors.toList());

    // Add related parties to the queue
    p2pHierarchyPartyQueue.addAll(getP2PHierarchyParties(relatedPartyIds));

    // Process the queue
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        P2PHierarchyParty currentHierarchyParty = p2pHierarchyPartyQueue.poll();
        
        // Fetch child party IDs
        List<String> childPartyIds = currentHierarchyParty.getRelatedPartyList().stream()
            .filter(p -> p.getRole1party() != null)  // Filter null child parties
            .map(p -> p.getRole1party().getPartyID())
            .collect(Collectors.toList());

        // Fetch child parties from codaDetails or make an API call if not present
        List<Party> childParties = codaQueryClient.getPartyWithAttributesPOST(childPartyIds, VISUALIZATION_JOIN_ATTRIBUTES);
        childParties.forEach(childParty -> codaDetails.putIfAbsent(childParty.getPartyID(), childParty));

        // Convert each Party to P2PHierarchyParty
        for (Party childParty : childParties) {
            P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);

            // Handle relationship mapping
            P2PHierarchyRelationship p2pHierarchyRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());
            currentHierarchyParty.getP2PHierarchyRelationship().put(childParty.getPartyID(), p2pHierarchyRelationship);

            // Add the child party to the queue for further processing (BFS)
            p2pHierarchyPartyQueue.add(childHierarchyParty);
        }
    }

    // Step 3: Map the data from codaDetails to the P2PHierarchyParty Model
    Map<String, P2PHierarchyRelationship> p2pHierarchyRelationshipMap = new HashMap<>();
    p2pHierarchyRelationshipMap.put(rootParty.getPartyID(),
            new P2PHierarchyRelationship(rootHierarchyParty, getRelationshipAttributesBetter(codaDetails, rootParty.getRelatedPartyList())));

    return rootHierarchyParty;
}


----------------------------------------

public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    // Initialize the data structures
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_JOIN_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);

    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);

    // Initialize the queue for BFS traversal
    Queue<P2PHierarchyParty> p2pHierarchyPartyQueue = new LinkedList<>();
    p2pHierarchyPartyQueue.add(rootHierarchyParty);

    // Process each party in BFS manner
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        // Poll the next party in the queue
        P2PHierarchyParty currentHierarchyParty = p2pHierarchyPartyQueue.poll();

        // Get the list of related child parties for the current party
        List<String> childPartyIds = currentHierarchyParty.getRelatedPartyList().stream()
            .map(p -> p.getRole1party().getPartyID())
            .collect(Collectors.toList());

        // Fetch the child parties' details and update codaDetails
        List<Party> childParties = codaQueryClient.getPartyWithAttributesPOST(childPartyIds, VISUALIZATION_JOIN_ATTRIBUTES);
        childParties.forEach(childParty -> codaDetails.put(childParty.getPartyID(), childParty));

        // For each child party, convert to P2PHierarchyParty and add to the queue
        for (Party childParty : childParties) {
            P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);

            // Create and store the P2PHierarchyRelationship for the current party
            P2PHierarchyRelationship p2pHierarchyRelationship = new P2PHierarchyRelationship(childHierarchyParty, new ArrayList<>());
            currentHierarchyParty.getP2PHierarchyRelationship().put(childParty.getPartyID(), p2pHierarchyRelationship);

            // Add the child party to the queue for further processing
            p2pHierarchyPartyQueue.add(childHierarchyParty);
        }
    }

    return rootHierarchyParty;
}


----------------------------------------
private Map<String, P2PHierarchyRelationship> getRelationshipAttributesBetter(Map<String, Party> codaDetails, 
    List<PartyToPartyRelationship> partyRelationships) {

    Map<String, P2PHierarchyRelationship> p2pHierarchyRelationshipMap = new HashMap<>();

    for (PartyToPartyRelationship partyRelationship : partyRelationships) {
        // Convert PartyToPartyRelationship into P2PHierarchyRelationshipAttributes
        P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributes(partyRelationship);

        // Fetch the child party (Role1party) from codaDetails and convert to P2PHierarchyParty
        Party childPartyFromCoda = codaDetails.get(partyRelationship.getRole1party().getPartyID());
        if (childPartyFromCoda == null) {
            continue; // Skip if the child party isn't present in codaDetails
        }

        P2PHierarchyParty childParty = getP2PHierarchyParty(childPartyFromCoda);  // Convert Party to P2PHierarchyParty

        // Use Optional to get the existing P2PHierarchyRelationship or create a new one
        P2PHierarchyRelationship p2pHierarchyRelationship = Optional.ofNullable(
                p2pHierarchyRelationshipMap.get(partyRelationship.getRole1party().getPartyID()))
                .orElseGet(() -> new P2PHierarchyRelationship(childParty, new ArrayList<>()));

        // Add the relationship attributes to the P2PHierarchyRelationship
        p2pHierarchyRelationship.getRelationshipAttributes().add(relationshipAttributes);

        // Update the map with the P2PHierarchyRelationship
        p2pHierarchyRelationshipMap.put(partyRelationship.getRole1party().getPartyID(), p2pHierarchyRelationship);
    }

    return p2pHierarchyRelationshipMap;
}


----------------------------------
private Map<String, P2PHierarchyRelationship> getRelationshipAttributesBetter(Map<String, Party> codaDetails, 
    List<PartyToPartyRelationship> partyRelationships) {

    Map<String, P2PHierarchyRelationship> p2pHierarchyRelationshipMap = new HashMap<>();

    for (PartyToPartyRelationship partyRelationship : partyRelationships) {
        P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributes(partyRelationship);

        // Ensure the child party exists in codaDetails
        P2PHierarchyParty childParty = codaDetails.get(partyRelationship.getRole2party().getPartyID());
        if (childParty == null) {
            continue; // Skip if child party is missing
        }

        // Check for an existing relationship, or create a new one
        P2PHierarchyRelationship p2pHierarchyRelationship = p2pHierarchyRelationshipMap.computeIfAbsent(
            partyRelationship.getRole1party().getPartyID(), 
            k -> new P2PHierarchyRelationship(childParty, new ArrayList<>()));

        // Add the relationship attributes to the existing or new relationship
        p2pHierarchyRelationship.getRelationshipAttributes().add(relationshipAttributes);
    }

    return p2pHierarchyRelationshipMap;
}



---------------+-----------------
public P2PHierarchyParty buildP2PRelationshipHierarchy(@NonNull String partyId) {
    Map<String, Party> codaDetails = new LinkedHashMap<>();
    Party rootParty = codaQueryClient.getPartyWithAttributesPOST(partyId, VISUALIZATION_JOIN_ATTRIBUTES);
    codaDetails.put(rootParty.getPartyID(), rootParty);
    
    Queue<P2PHierarchyParty> p2pHierarchyPartyQueue = new LinkedList<>();
    Set<String> processedParties = new HashSet<>(); // To track processed parties

    // Step 1: Maintain queue to process parties
    List<String> relatedPartyIds = rootParty.getRelatedPartyList().stream()
        .map(p -> p.getRole1party().getPartyID()).collect(Collectors.toList());
    p2pHierarchyPartyQueue.addAll(getP2PHierarchyParties(relatedPartyIds));
    
    // Mark the root as processed
    processedParties.add(rootParty.getPartyID());
    
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        // Poll the current party from the queue
        P2PHierarchyParty currentParty = p2pHierarchyPartyQueue.poll();
        
        // Get the child parties of the current party
        List<String> childPartyIds = currentParty.getRelatedPartyList().stream()
            .map(p -> p.getRole1party().getPartyID())
            .collect(Collectors.toList());

        // Add the children to the codaDetails map by making a batch coda call
        List<Party> childParties = codaQueryClient.getPartyWithAttributesPOST(childPartyIds, VISUALIZATION_JOIN_ATTRIBUTES);
        childParties.forEach(childParty -> codaDetails.put(childParty.getPartyID(), childParty));

        // Only add children that haven't been processed
        childParties.stream()
            .map(this::getP2PHierarchyParty) // Convert to hierarchy parties
            .filter(p -> !processedParties.contains(p.getPartyId())) // Filter out already processed parties
            .forEach(p -> {
                p2pHierarchyPartyQueue.add(p); // Add unprocessed children to the queue
                processedParties.add(p.getPartyId()); // Mark them as processed
            });

        // Perform your mapping logic here for hierarchy creation
        // You can call your getRelationshipAttributesBetter method for mapping
    }

    // Build and return the hierarchy based on your codaDetails and processed parties
    return createHierarchyStructure(rootParty, codaDetails);
}

private List<P2PHierarchyParty> getP2PHierarchyParties(List<String> partyIds) {
    // Create and return P2PHierarchyParty objects from party IDs
    return partyIds.stream()
        .map(this::getP2PHierarchyParty)
        .collect(Collectors.toList());
}


----------------------------------
private Map<String, P2PHierarchyRelationship> getRelationshipAttributesBetter(Map<String, Party> codaDetails, 
    List<PartyToPartyRelationship> partyRelationships) {

    Map<String, P2PHierarchyRelationship> p2pHierarchyRelationshipMap = new HashMap<>();

    for (PartyToPartyRelationship partyRelationship : partyRelationships) {
        P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributes(partyRelationship);
        
        // Get or create P2PHierarchyRelationship using Optional
        P2PHierarchyRelationship p2pHierarchyRelationship = Optional.ofNullable(
                p2pHierarchyRelationshipMap.get(partyRelationship.getRole1party().getPartyID()))
                .orElseGet(() -> {
                    // Create a new relationship if it doesn't exist
                    return new P2PHierarchyRelationship(
                            codaDetails.get(partyRelationship.getRole1party().getPartyID()), 
                            List.of(relationshipAttributes));
                });

        // If already exists, add attributes to the existing relationship
        if (!p2pHierarchyRelationshipMap.containsKey(partyRelationship.getRole1party().getPartyID())) {
            p2pHierarchyRelationshipMap.put(partyRelationship.getRole1party().getPartyID(), p2pHierarchyRelationship);
        } else {
            p2pHierarchyRelationship.getRelationshipAttributes().add(relationshipAttributes);
        }
    }

    // Create child parties from the relationships
    List<P2PHierarchyParty> childParties = partyRelationships.stream()
            .map(p -> codaDetails.get(p.getRole1party().getPartyID()))
            .map(this::getP2PHierarchyParty)
            .collect(Collectors.toList());

    return p2pHierarchyRelationshipMap;
}

-------------------------------------------
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public static <T> List<T> pollAll(Queue<T> queue) {
    List<T> result = new ArrayList<>();
    
    while (!queue.isEmpty()) {
        T element = queue.poll();
        // Add the element to the result list
        result.add(element);
        
        // Process the polled element (optional, if you want to do something during polling)
        System.out.println("Processing element: " + element);
    }
    
    return result;  // Return the list of all polled elements
}

public static <T> void pollAll(Queue<T> queue) {
    while (!queue.isEmpty()) {
        T element = queue.poll();
        // Process the polled element
        System.out.println("Processing element: " + element);
        
        // Add your processing logic here for each polled element
    }
}
