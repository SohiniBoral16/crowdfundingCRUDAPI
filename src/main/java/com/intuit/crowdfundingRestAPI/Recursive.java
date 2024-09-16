public P2PHierarchyParty buildP2PHierarchyRelationshipByPartyIdBFS(Party rootParty) {
    
    // Initialize the processed parties set and queue for BFS
    Set<String> processedParties = new HashSet<>();
    Queue<P2PHierarchyParty> partyQueue = new LinkedList<>();

    // Create the root hierarchy party
    P2PHierarchyParty rootHierarchyParty = getP2PHierarchyParty(rootParty);
    partyQueue.add(rootHierarchyParty);
    processedParties.add(rootParty.getPartyID());

    // While there are parties to process in the queue
    while (!partyQueue.isEmpty()) {
        // Dequeue the next party to process
        P2PHierarchyParty currentHierarchyParty = partyQueue.poll();
        Party currentParty = getPartyByHierarchyParty(currentHierarchyParty); // Assuming a method to retrieve the Party from P2PHierarchyParty

        // Fetch related parties for the current party
        List<PartyToPartyRelationship> relatedPartyList = currentParty.getRelatedPartyList();

        if (relatedPartyList.isEmpty()) {
            continue;
        }

        // Collect all child party IDs
        List<String> childPartyIds = relatedPartyList.stream()
            .map(PartyToPartyRelationship::getRole1Party)
            .map(Party::getPartyID)
            .filter(id -> id != null && !processedParties.contains(id))
            .collect(Collectors.toList());

        // Fetch all child party details in one batch call
        List<Party> childParties = codaQueryClient.getPartyAttributesWithAttributesPOST(childPartyIds, VISUALIZATION_DOM_ATTRIBUTES);

        // Iterate through the related parties
        for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyList) {
            String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

            // Find the corresponding child party
            Party childParty = childParties.stream()
                .filter(p -> childPartyId.equals(p.getPartyID()))
                .findFirst()
                .orElse(null);

            // If the child party is not found, skip it
            if (childParty == null) {
                continue;
            }

            // Check if the relationship type matches ownership types in the enum
            String relationshipTypeId = relatedPartyRelationship.getRelationshipType().getId();
            boolean isOwnershipType = P2POwnershipType.isOwnershipType(relationshipTypeId);

            // For root parties, accept all relationships; for others, check ownership type
            if (!currentParty.equals(rootParty) && !isOwnershipType) {
                continue; // Skip non-ownership types for non-root parties
            }

            // Build the hierarchy party for the child party
            P2PHierarchyParty childHierarchyParty = getP2PHierarchyParty(childParty);

            // Build the relationship attributes
            P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);

            // Create the relationship object
            P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                .childParty(childHierarchyParty)
                .relationshipAttributes(relationshipAttributes)
                .build();

            // Add the relationship to the current hierarchy party
            currentHierarchyParty.addP2PHierarchyParty(childHierarchyParty, relationship);

            // Mark this child party as processed and add it to the queue for further processing
            processedParties.add(childParty.getPartyID());
            partyQueue.add(childHierarchyParty);
        }
    }

    return rootHierarchyParty;
}


---------------------------------------------
private P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {
    
    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Early return if the party has already been processed
    if (processedParties.contains(rootPartyId)) {
        return null;
    }

    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // Fetch related parties
    List<PartyToPartyRelationship> relatedPartyListByRootParty = new ArrayList<>(rootParty.getRelatedPartyList());

    if (relatedPartyListByRootParty.isEmpty()) {
        return p2pHierarchyParty; // No related parties, return early
    }

    // Collect child party IDs
    List<String> childPartyIds = relatedPartyListByRootParty.stream()
        .map(PartyToPartyRelationship::getRole1Party)
        .map(Party::getPartyID)
        .filter(id -> id != null && !processedParties.contains(id))
        .collect(Collectors.toList());

    // Batch fetch party details from Coda (assuming batch fetch method returns a list of Party objects)
    List<Party> childParties = codaQueryClient.getPartyAttributesWithAttributesPOST(childPartyIds, VISUALIZATION_DOM_ATTRIBUTES);

    // Process each related party
    for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyListByRootParty) {
        String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

        // Find the matching child party in the childParties list
        Party childParty = childParties.stream()
            .filter(p -> childPartyId.equals(p.getPartyID()))
            .findFirst()
            .orElse(null);

        if (childParty == null) {
            continue;  // Skip if the child party is not found
        }

        // Check if the relationship type matches the ownership types in enum
        String relationshipTypeId = relatedPartyRelationship.getRelationshipType().getId();
        boolean isOwnershipType = P2POwnershipType.isOwnershipType(relationshipTypeId);

        // Only proceed with further hierarchy if it matches ownership types in enum
        if (!isOwnershipType) {
            continue; // Skip this relationship if it's not an ownership type
        }

        // Recursively build the child hierarchy
        P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);

        if (childHierarchy != null) {
            P2PHierarchyRelationshipAttributes attributes = getRelationshipAttributeDTO(relatedPartyRelationship);

            P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                .childParty(childHierarchy)
                .relationshipAttributes(attributes)
                .build();

            // Add the relationship to the root party
            p2pHierarchyParty.addP2PHierarchyParty(childHierarchy, relationship);
        }
    }

    return p2pHierarchyParty;
}




private P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {
    
    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;
    }

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties and return early if none exist
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyParty;
    }

    // Collect child party IDs that need to be fetched and filtered by the enum check
    Set<String> childPartyIds = relatedPartyList.stream()
        .map(rel -> rel.getRole1Party().getPartyID())
        .filter(id -> id != null && !processedParties.contains(id) && P2POwnershipType.isOwnershipType(rel.getRelationshipType().getId()))
        .collect(Collectors.toSet());

    // Fetch details for all child parties in a single call
    Map<String, Party> childParties = codaQueryClient.getPartyAttributesBulk(childPartyIds);

    // Process each related party relationship
    for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyList) {
        String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

        // Continue only if the child party ID was included in the bulk fetch
        if (!childParties.containsKey(childPartyId)) {
            continue;
        }

        Party childParty = childParties.get(childPartyId);

        // Recursively build the child hierarchy
        P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);

        // If child hierarchy is successfully built, add it to the root party
        if (childHierarchy != null) {
            P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);

            P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                    .childParty(childHierarchy)
                    .relationshipAttributes(relationshipAttributes)
                    .build();

            // Add the relationship to the root party
            p2pHierarchyParty.addP2PHierarchyParty(childHierarchy, relationship);
        }
    }

    return p2pHierarchyParty;
}






private P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {
    // Early return if already processed
    if (processedParties.contains(rootParty.getPartyID())) {
        return null;
    }

    processedParties.add(rootParty.getPartyID());
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // Collect child party IDs
    List<String> childPartyIds = rootParty.getRelatedPartyList().stream()
        .map(PartyToPartyRelationship::getRole1Party)
        .map(Party::getPartyID)
        .filter(id -> !processedParties.contains(id))
        .collect(Collectors.toList());

    // Batch fetch party details from Coda
    Map<String, Party> childParties = codaQueryClient.getPartyAttributesBatch(childPartyIds);

    // Process each related party
    rootParty.getRelatedPartyList().forEach(relatedPartyRelationship -> {
        Party childParty = childParties.get(relatedPartyRelationship.getRole1Party().getPartyID());
        if (childParty != null && P2POwnershipType.isOwnershipType(relatedPartyRelationship.getRelationshipType().getId())) {
            P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);
            if (childHierarchy != null) {
                P2PHierarchyRelationshipAttributes attributes = getRelationshipAttributeDTO(relatedPartyRelationship);
                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                    .childParty(childHierarchy)
                    .relationshipAttributes(attributes)
                    .build();
                p2pHierarchyParty.addP2PHierarchyParty(childHierarchy, relationship);
            }
        }
    });

    return p2pHierarchyParty;
}

------------------------------------------
private P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {
    
    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Return early if the party has already been processed to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;
    }

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties and return early if none exist
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyParty; // No related parties, return early
    }

    // Process each related party relationship
    relatedPartyList.stream()
        .filter(relatedPartyRelationship -> {
            String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

            // For child hierarchy, filter out relationships not matching ownership types in enum
            String relationshipTypeId = relatedPartyRelationship.getRelationshipType().getId();
            boolean isOwnershipType = P2POwnershipType.isOwnershipType(relationshipTypeId);

            // Fetch all relationships for root party, apply ownership type filtering for non-root parties
            return childPartyId != null && !processedParties.contains(childPartyId) && isOwnershipType;
        })
        .forEach(relatedPartyRelationship -> {
            String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);

            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);

            // If child hierarchy is successfully built, add it to the root party
            if (childHierarchy != null) {
                P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);

                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                        .childParty(childHierarchy)
                        .relationshipAttributes(relationshipAttributes)
                        .build();

                // Add the relationship to the root party
                p2pHierarchyParty.addP2PHierarchyParty(childHierarchy, relationship);
            }
        });

    // Return the final hierarchy party object
    return p2pHierarchyParty;
}

-----------------------------------------

private P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {

    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Return early if the party has already been processed to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;
    }

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties and return early if none exist
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyParty; // No related parties, return early
    }

    // Process each related party relationship
    relatedPartyList.stream()
        .filter(relatedPartyRelationship -> {
            String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();
            return childPartyId != null && !processedParties.contains(childPartyId);
        })
        .forEach(relatedPartyRelationship -> {
            String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);

            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);

            // If child hierarchy is successfully built, add it to the root party
            if (childHierarchy != null) {
                // Correctly map the relatedPartyRelationship to get relationship attributes
                P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);

                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                        .childParty(childHierarchy)
                        .relationshipAttributes(relationshipAttributes)
                        .build();

                // Add the relationship to the root party
                p2pHierarchyParty.addP2PHierarchyParty(childHierarchy, relationship);
            }
        });

    // Return the final hierarchy party object
    return p2pHierarchyParty;
}



private P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {

    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Return early if the party has already been processed to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;
    }

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties and return early if none exist
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyParty; // No related parties, return early
    }

    // Process each related party
    relatedPartyList.stream()
        .map(PartyToPartyRelationship::getRole1Party)
        .filter(role1Party -> role1Party.getPartyID() != null && !processedParties.contains(role1Party.getPartyID()))
        .forEach(role1Party -> {
            String childPartyId = role1Party.getPartyID();

            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);

            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);

            // If child hierarchy is successfully built, add it to the root party
            if (childHierarchy != null) {
                P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(childParty);
                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                        .childParty(childHierarchy)
                        .relationshipAttributes(relationshipAttributes)
                        .build();

                // Add the relationship to the root party
                p2pHierarchyParty.addP2PHierarchyParty(childHierarchy, relationship);
            }
        });

    // Return the final hierarchy party object
    return p2pHierarchyParty;
}

----------------------------------------

public P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {

    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;  // End recursion
    }

    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties from the root party
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    // Base case to stop recursion if there are no related parties
    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyParty;  // Return built party if no relationships
    }

    // Loop through each related party and recursively build the hierarchy
    for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyList) {
        String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

        // Check if child party has already been processed
        if (childPartyId != null && !processedParties.contains(childPartyId)) {
            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);

            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);

            // Add the child hierarchy to the root party
            if (childHierarchy != null) {
                // Use getRelationshipAttributeDTO() to build relationship attributes
                P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);

                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                    .childParty(childHierarchy)
                    .relationshipAttributes(relationshipAttributes)
                    .build();

                // Use the simplified add method to add the relationship to the hierarchy
                p2pHierarchyParty.addP2PHierarchyParty(childHierarchy, relationship);
            }
        }
    }

    // Return the final hierarchy party object
    return p2pHierarchyParty;
}



public class P2PHierarchyParty {

    private final String partyId;
    private String partyName;
    private String validationStatus;
    private String countryOfOrganization;
    private String legalForm;
    private String partyAlias;
    private String legalName;
    private String countryOfDomicile;
    private String dateOfBirth;
    private String dateOfIncorporation;
    private List<CountrySpecificIdentifiers> countrySpecificIdentifiers;
    private PepIndicator pepIndicator;

    // Map to store relationships by Party ID
    private final Map<String, List<P2PHierarchyRelationshipByPartyId>> p2pHierarchyRelationship;

    public P2PHierarchyParty(String partyId) {
        this.partyId = partyId;
        this.p2pHierarchyRelationship = new HashMap<>();
    }

    // Simple add method to add a child party and its relationship
    public void addP2PHierarchyParty(P2PHierarchyParty childParty, P2PHierarchyRelationshipByPartyId relationship) {
        // Add the relationship between the current party and the child party
        p2pHierarchyRelationship
            .computeIfAbsent(childParty.getPartyId(), k -> new ArrayList<>())
            .add(relationship);
    }

    // Getter for p2pHierarchyRelationship map
    public Map<String, List<P2PHierarchyRelationshipByPartyId>> getP2pHierarchyRelationship() {
        return p2pHierarchyRelationship;
    }

    // Getter for partyId
    public String getPartyId() {
        return partyId;
    }
}
---_----------------------
public void addP2PHierarchyRelationship(String partyId, P2PHierarchyRelationshipByPartyId relationship) {
    this.p2pHierarchyRelationship
        .computeIfAbsent(partyId, k -> new ArrayList<>())
        .add(relationship);
}


public P2PHierarchyParty buildP2PHierarchyRelationshipByPartyId(Party rootParty, Set<String> processedParties) {

    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;  // End recursion
    }

    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties from the root party
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    // Base case to stop recursion if there are no related parties
    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyParty;  // Return built party if no relationships
    }

    // Loop through each related party and recursively build the hierarchy
    for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyList) {
        String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();

        // Check if child party has already been processed
        if (childPartyId != null && !processedParties.contains(childPartyId)) {
            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);

            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyRelationshipByPartyId(childParty, processedParties);

            // Add the child hierarchy to the root party
            if (childHierarchy != null) {
                // Use getRelationshipAttributeDTO() to build relationship attributes
                P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);

                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                    .childParty(childHierarchy)
                    .relationshipAttributes(relationshipAttributes)
                    .build();

                // Here, we add the relationship to the p2pHierarchyParty
                p2pHierarchyParty.addP2PHierarchyRelationship(childPartyId, relationship);  // Assuming this method exists in the model
            }
        }
    }

    // Return the final hierarchy party object
    return p2pHierarchyParty;
}




-----------------------------------
public P2PHierarchyParty buildP2PHierarchyHierarchyByPartyId(Party rootParty, Set<String> processedParties) {
    
    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Use getP2PHierarchyParty() to build the root party details
    P2PHierarchyParty p2pHierarchyParty = getP2PHierarchyParty(rootParty);

    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;  // End recursion
    }
    
    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties from the root party
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    // Base case to stop recursion if there are no related parties
    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyParty;  // Return built party if no relationships
    }

    // Loop through each related party and recursively build the hierarchy
    for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyList) {
        String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();
        
        // Check if child party has already been processed
        if (childPartyId != null && !processedParties.contains(childPartyId)) {
            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);
            
            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyHierarchyByPartyId(childParty, processedParties);
            
            // Add the child hierarchy to the root party
            if (childHierarchy != null) {
                // Use getRelationshipAttributeDTO() to build relationship attributes
                P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);
                
                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                    .childParty(childHierarchy)
                    .relationshipAttributes(relationshipAttributes)
                    .build();
                
                p2pHierarchyParty.addP2PHierarchyRelationship(childHierarchy);  // Adding the child hierarchy
            }
        }
    }

    // Return the final hierarchy party object
    return p2pHierarchyParty;  // Return the main hierarchy object
}




public P2PHierarchyParty buildP2PHierarchyHierarchyByPartyId(Party rootParty, Set<String> processedParties) {
    
    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();

    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty.P2PHierarchyPartyBuilder p2pHierarchyPartyBuilder = P2PHierarchyParty.builder()
        .partyId(rootPartyId)
        .partyName(Utils.getOrDefault(rootParty.getPartyNameList()).get(0).getName())
        .partyAlias(Utils.getOrDefault(rootParty.getPartyAlias()))
        .countryOfDomicile(Utils.getOrDefault(rootParty.getCountryOfDomicile()).getName())
        .countryOfOrganization(Utils.getOrDefault(rootParty.getCountryOfOrganization()).getName())
        .dateOfBirth(Optional.ofNullable(rootParty.getDateOfBirth()).map(Object::toString).orElse(null))
        .dateOfIncorporation(Optional.ofNullable(rootParty.getFormationDate()).map(Object::toString).orElse(null))
        .legalForm(Utils.getOrDefault(rootParty.getLegalForm()).getName())
        .legalName(Utils.getOrDefault(rootParty.getLegalName()).getName())
        .validationStatus(Utils.getOrDefault(rootParty.getPartyValidationStatus()).getName())
        .pepIndicator(getPepIndicator(rootParty))
        .countrySpecificIdentifiers(getCountrySpecificIdentifier(rootParty))
        .p2pHierarchyRelationship(new HashMap<>());
    
    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;  // End recursion
    }
    
    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Fetch related parties from the root party
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    // Base case to stop recursion if there are no related parties
    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyPartyBuilder.build();  // Return built party if no relationships
    }

    // Loop through each related party and recursively build the hierarchy
    for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyList) {
        String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();
        
        // Check if child party has already been processed
        if (childPartyId != null && !processedParties.contains(childPartyId)) {
            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);
            
            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyHierarchyByPartyId(childParty, processedParties);
            
            // Add the child hierarchy to the root party
            if (childHierarchy != null) {
                P2PHierarchyRelationshipAttributes relationshipAttributes = getRelationshipAttributeDTO(relatedPartyRelationship);
                
                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                    .childParty(childHierarchy)
                    .relationshipAttributes(relationshipAttributes)
                    .build();
                
                p2pHierarchyPartyBuilder.addP2PHierarchyRelationship(childHierarchy);
            }
        }
    }

    // Return the final hierarchy party object
    return p2pHierarchyPartyBuilder.build();
}




public P2PHierarchyParty buildP2PHierarchyHierarchyByPartyId(Party rootParty, Set<String> processedParties) {

    // Retrieve root party ID
    String rootPartyId = rootParty.getPartyID();
    
    // Create a new P2PHierarchyParty object using the builder
    P2PHierarchyParty.P2PHierarchyPartyBuilder p2pHierarchyPartyBuilder = P2PHierarchyParty.builder()
        .partyId(rootPartyId)
        .partyName(rootParty.getPartyName());
    
    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(rootPartyId)) {
        return null;  // End recursion
    }
    
    // Mark this party as processed
    processedParties.add(rootPartyId);

    // Initialize hierarchy party structure
    P2PHierarchyParty partyDetails = getP2PHierarchyParty(rootParty);
    p2pHierarchyPartyBuilder.validationStatus(partyDetails.getValidationStatus());
    p2pHierarchyPartyBuilder.countryOfOrganization(partyDetails.getCountryOfOrganization());

    // Fetch related parties from the root party
    List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList()
            .stream()
            .collect(Collectors.toList());

    // Base case to stop recursion if there are no related parties
    if (relatedPartyList.isEmpty()) {
        return p2pHierarchyPartyBuilder.build();  // Return built party if no relationships
    }

    // Loop through each related party and recursively build the hierarchy
    for (PartyToPartyRelationship relatedPartyRelationship : relatedPartyList) {
        String childPartyId = relatedPartyRelationship.getRole1Party().getPartyID();
        
        // Check if child party has already been processed
        if (childPartyId != null && !processedParties.contains(childPartyId)) {
            // Retrieve child party details from Coda
            Party childParty = codaQueryClient.getPartyAttributes(childPartyId);
            
            // Recursively build the child hierarchy
            P2PHierarchyParty childHierarchy = buildP2PHierarchyHierarchyByPartyId(childParty, processedParties);
            
            // Add the child hierarchy to the root party
            if (childHierarchy != null) {
                P2PHierarchyRelationshipAttributes attributes = P2PHierarchyRelationshipAttributes.builder()
                    .relationshipTypeId(relatedPartyRelationship.getRelationshipTypeID())
                    .relationshipTypeName(relatedPartyRelationship.getRelationshipTypeName())
                    .build();
                
                P2PHierarchyRelationshipByPartyId relationship = P2PHierarchyRelationshipByPartyId.builder()
                    .childParty(childHierarchy)
                    .relationshipAttributes(attributes)
                    .build();
                
                p2pHierarchyPartyBuilder.addP2PHierarchyRelationship(childHierarchy);
            }
        }
    }

    // Return the final hierarchy party object
    return p2pHierarchyPartyBuilder.build();
}
