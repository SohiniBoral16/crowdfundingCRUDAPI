
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
