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
