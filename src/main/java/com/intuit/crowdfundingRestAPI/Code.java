
private List<TargetParty> fetchTargetParty(List<String> targetPartyIds) {
    var targetPartiesData = getPartiesFromCoda(targetPartyIds, TARGET_PARTY_DOM_ATTRIBUTES);
    List<TargetParty> targetParties = new ArrayList<>();

    for (Party targetPartyData : targetPartiesData) {
        // Collect all related parties and their associated relationship type IDs
        List<TargetPartyRelatedParties> relatedParties = targetPartyData.getRelatedPartyList().stream()
            .map(relatedParty -> {
                List<String> relationshipTypeIds = relatedParty.getPartyRelationshipType().stream()
                    .map(type -> type.getID())
                    .collect(Collectors.toList());
                
                return TargetPartyRelatedParties.builder()
                    .relatedPartyId(relatedParty.getRole1Party().getPartyID())
                    .relationshipTypeId(relationshipTypeIds)
                    .build();
            })
            .collect(Collectors.toList());

        // Build the TargetParty object
        TargetParty targetParty = TargetParty.builder()
            .targetPartyId(targetPartyData.getPartyID())
            .targetPartyRelatedParties(relatedParties)
            .build();

        targetParties.add(targetParty);
    }

    log.info("Target party details: {}", targetParties);
    return targetParties;
}

