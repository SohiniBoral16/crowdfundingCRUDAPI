

private List<TargetParty> fetchTargetParty(List<String> targetPartyIds) {
    var targetPartiesData = getPartiesFromCoda(targetPartyIds, TARGET_PARTY_DOM_ATTRIBUTES);
    List<TargetParty> targetParties = new ArrayList<>();

    for (Party targetPartyData : targetPartiesData) {
        TargetParty targetParty = TargetParty.builder()
            .targetPartyId(targetPartyData.getPartyID())
            .targetPartyRelatedParties(
                targetPartyData.getRelatedPartyList().stream()
                    .map(relatedParty -> TargetPartyRelatedParties.builder()
                        .relatedPartyId(relatedParty.getRole1Party().getPartyID())
                        .relationshipTypeId(
                            Collections.singletonList(relatedParty.getPartyRelationshipType().getID())
                        )
                        .build()
                    )
                    .collect(Collectors.toList())
            )
            .build();

        targetParties.add(targetParty);
    }

    log.info("Target party details: {}", targetParties);
    return targetParties;
}


private List<TargetParty> fetchTargetParty(List<String> targetPartyIds) {
    var targetPartiesData = getPartiesFromCoda(targetPartyIds, TARGET_PARTY_DOM_ATTRIBUTES);
    List<TargetParty> targetParties = new ArrayList<>();

    for (Party targetPartyData : targetPartiesData) {
        TargetParty targetParty = TargetParty.builder()
            .targetPartyId(targetPartyData.getPartyID())
            .targetPartyRelatedParties(
                targetPartyData.getRelatedPartyList().stream()
                    .map(relatedParty -> TargetPartyRelatedParties.builder()
                        .relatedPartyId(relatedParty.getRole1Party().getPartyID())
                        .relationshipTypeId(
                            relatedParty.getPartyRelationshipType().stream()
                                .map(relationshipType -> relationshipType.getID())
                                .collect(Collectors.toList())
                        )
                        .build()
                    )
                    .collect(Collectors.toList())
            )
            .build();

        targetParties.add(targetParty);
    }

    log.info("Target party details: {}", targetParties);
    return targetParties;
}
