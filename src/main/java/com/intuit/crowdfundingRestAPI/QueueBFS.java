
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
