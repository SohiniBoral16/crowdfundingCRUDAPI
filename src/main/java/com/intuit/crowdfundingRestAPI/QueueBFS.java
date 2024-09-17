
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
