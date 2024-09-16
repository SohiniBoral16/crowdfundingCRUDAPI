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
