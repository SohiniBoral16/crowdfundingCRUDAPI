
import java.util.stream.Collectors;

public List<P2PCopyResponse> validateCopyResponse(List<P2PCopyRequest> p2pCopyRequests) {
    List<String> targetPartiesId = new ArrayList<>();

    for (P2PCopyRequest request : p2pCopyRequests) {
        for (P2PCopyTargetParty targetParty : request.getTargetParties()) {
            targetPartiesId.add(targetParty.getTargetPartyId());

            // Fetch the related party details based on the target party ID
            var partyDetails = codaQueryClient.getPartyWithAttributesPOST(
                targetParty.getTargetPartyId(),
                Stream.of(COPY_RELATIONSHIP_DOM_ATTRIBUTES).flatMap(Collection::stream).collect(Collectors.toList())
            );

            // Traverse the sourceRelationships list in the request
            for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                String sourcePartyId = sourceRelationship.getSourcePartyId();
                List<String> requestRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

                // Traverse the relatedPartyList to check for matching role1Party.partyID
                for (PartyToPartyRelationship relatedParty : partyDetails.getRelatedPartyList()) {
                    boolean partyIdMatches = relatedParty.getRole1Party().getPartyID().equals(sourcePartyId);

                    // Assuming partyRelationshipType in relatedParty is an object and we compare its ID with request's relationshipTypeIds
                    boolean relationshipTypeMatches = requestRelationshipTypeIds.contains(relatedParty.getPartyRelationshipType().getID());

                    if (partyIdMatches && relationshipTypeMatches) {
                        // Perform necessary actions here when a match is found
                        System.out.println("Match found for Role1Party ID: " + relatedParty.getRole1Party().getPartyID());
                        System.out.println("Matching Relationship Type ID: " + relatedParty.getPartyRelationshipType().getID());
                        // You can log this, add to a response list, etc.
                    }
                }
            }
        }
    }

    // Return the list of P2PCopyResponse or any other required data
    return null; // Adjust as necessary
}

------------------------------------
public List<P2PCopyResponse> validateCopyResponse(List<P2PCopyRequest> p2pCopyRequests) {
    List<String> targetPartiesId = new ArrayList<>();

    for (P2PCopyRequest request : p2pCopyRequests) {
        for (P2PCopyTargetParty targetParty : request.getTargetParties()) {
            targetPartiesId.add(targetParty.getTargetPartyId());

            // Fetch the related party details based on the target party ID
            var partyDetails = codaQueryClient.getPartyWithAttributesPOST(
                targetParty.getTargetPartyId(),
                Stream.of(COPY_RELATIONSHIP_DOM_ATTRIBUTES).flatMap(Collection::stream).collect(Collectors.toList())
            );

            // Traverse the sourceRelationships list in the request
            for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                String sourcePartyId = sourceRelationship.getSourcePartyId();
                List<String> requestRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

                // Traverse the relatedPartyList to check for matching role1Party.partyID
                for (Party relatedParty : partyDetails.getRelatedPartyList()) {
                    boolean partyIdMatches = relatedParty.getRole1Party().getPartyID().equals(sourcePartyId);

                    // Check if any of the relationshipTypeIds from the request match those in the related party
                    boolean relationshipTypeMatches = relatedParty.getPartyRelationshipType().stream()
                        .anyMatch(requestRelationshipTypeIds::contains);

                    if (partyIdMatches && relationshipTypeMatches) {
                        // Perform necessary actions here when a match is found
                        System.out.println("Match found for Role1Party ID: " + relatedParty.getRole1Party().getPartyID());
                        System.out.println("Matching Relationship Type IDs: " + requestRelationshipTypeIds);
                        // You can log this, add to a response list, etc.
                    }
                }
            }
        }
    }

    // Return the list of P2PCopyResponse or any other required data
    return null; // Adjust as necessary
}

--------------------------------------------
public List<P2PCopyResponse> validateCopyResponse(List<P2PCopyRequest> p2pCopyRequests) {
    List<String> targetPartiesId = new ArrayList<>();

    for (P2PCopyRequest request : p2pCopyRequests) {
        for (P2PCopyTargetParty targetParty : request.getTargetParties()) {
            targetPartiesId.add(targetParty.getTargetPartyId());

            // Fetch the related party details based on the target party ID
            var partyDetails = codaQueryClient.getPartyWithAttributesPOST(
                targetParty.getTargetPartyId(),
                Stream.of(COPY_RELATIONSHIP_DOM_ATTRIBUTES).flatMap(Collection::stream).collect(Collectors.toList())
            );

            // Traverse the sourceRelationships list in the request
            for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                String sourcePartyId = sourceRelationship.getSourcePartyId();

                // Traverse the relatedPartyList to check for matching role1Party.partyID
                for (Party relatedParty : partyDetails.getRelatedPartyList()) {
                    if (relatedParty.getRole1Party().getPartyID().equals(sourcePartyId)) {
                        // Perform necessary actions here when a match is found
                        System.out.println("Match found for Role1Party ID: " + relatedParty.getRole1Party().getPartyID());
                        // You can log this, add to a response list, etc.
                    }
                }
            }
        }
    }

    // Return the list of P2PCopyResponse or any other required data
    return null; // Adjust as necessary
}

-------------------------
public List<P2PCopyResponse> validateCopyResponse(List<P2PCopyRequest> p2pCopyRequests) {
    List<String> targetPartiesId = new ArrayList<>();

    for (P2PCopyRequest request : p2pCopyRequests) {
        for (P2PCopyTargetParty targetParty : request.getTargetParties()) {
            targetPartiesId.add(targetParty.getTargetPartyId());

            // Fetch the related party details based on the target party ID
            var partyDetails = codaQueryClient.getPartyWithAttributesPOST(
                targetParty.getTargetPartyId(),
                Stream.of(COPY_RELATIONSHIP_DOM_ATTRIBUTES).flatMap(Collection::stream).collect(Collectors.toList())
            );

            // Traverse the relatedPartyList to check for matching role1Party.partyID and sourcePartyId
            for (Party relatedParty : partyDetails.getRelatedPartyList()) {
                if (relatedParty.getRole1Party().getPartyID().equals(request.getSourcePartyId())) {
                    // Perform necessary actions here when a match is found
                    System.out.println("Match found for Role1Party ID: " + relatedParty.getRole1Party().getPartyID());
                }
            }
        }
    }

    // Return the list of P2PCopyResponse or any other required data
    return null; // Adjust as necessary
}
