
private void buildHierarchyForParty(Party party, Set<String> processedParties, RelationshipGraphHierarchy graphHierarchy) {
    String partyId = party.getPartyID();

    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(partyId)) {
        return;
    }

    // Mark this party as processed
    processedParties.add(partyId);

    // Initialize the PartyForTreeStructure
    PartyForTreeStructure partyDetails = getPartyDTO(party);
    graphHierarchy.addParty(partyDetails); // Add this party to the hierarchy

    // Fetch related parties and recursively build their hierarchies
    List<Relationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());

    // Check if the relatedPartyList is empty to avoid unnecessary processing
    if (relatedPartyList.isEmpty()) {
        return; // No related parties to process, break the recursion
    }

    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        // Handle the relationship data
        Relationship relationship = getRelationshipDTO(partyRelationship);
        graphHierarchy.addRelationship(partyDetails, relationship);

        String childPartyId = partyRelationship.getRoleparty().getPartyID();

        // Check if the child party ID is not null and hasn't been processed already
        if (childPartyId != null && !processedParties.contains(childPartyId)) {
            // Retrieve all child parties associated with the childPartyId
            List<Party> childParties = getPartiesFromCoda(Collections.singletonList(childPartyId));
            
            for (Party childParty : childParties) {
                PartyForTreeStructure childPartyDetails = getPartyDTO(childParty);
                graphHierarchy.addParty(childPartyDetails);

                // Recursively build the hierarchy for each child party
                buildHierarchyForParty(childParty, processedParties, graphHierarchy);
            }
        }
    }
}

-----------------------------
private void buildHierarchyForParty(Party party, Set<String> processedParties, RelationshipGraphHierarchy graphHierarchy) {
    String partyId = party.getPartyID();

    // If the party has already been processed, return to avoid infinite loops
    if (processedParties.contains(partyId)) {
        return;
    }

    // Mark this party as processed
    processedParties.add(partyId);

    // Initialize the PartyForTreeStructure
    PartyForTreeStructure partyDetails = getPartyDTO(party);
    graphHierarchy.addParty(partyDetails); // Add this party to the hierarchy

    // Fetch related parties and recursively build their hierarchies
    List<Relationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());

    // Check if the relatedPartyList is empty to avoid unnecessary processing
    if (relatedPartyList.isEmpty()) {
        return; // No related parties to process, break the recursion
    }

    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        // Handle the relationship data
        Relationship relationship = getRelationshipDTO(partyRelationship);
        graphHierarchy.addRelationship(partyDetails, relationship);

        String childPartyId = partyRelationship.getRoleparty().getPartyID();

        // Check if the child party ID is not null and hasn't been processed already
        if (childPartyId != null && !processedParties.contains(childPartyId)) {
            // Recursively process the child party hierarchy
            List<Party> childParties = getPartiesFromCoda(Collections.singletonList(childPartyId));
            if (!childParties.isEmpty()) {
                PartyForTreeStructure childPartyDetails = getPartyDTO(childParties.get(0));
                graphHierarchy.addParty(childPartyDetails);

                // Recursively build the hierarchy for the child party
                buildHierarchyForParty(childParties.get(0), processedParties, graphHierarchy);
            }
        }
    }
}
