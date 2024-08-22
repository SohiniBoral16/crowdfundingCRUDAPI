private List<TargetParty> fetchTargetParty(List<String> targetPartyIds) {
    var targetPartiesData = getPartiesFromCoda(targetPartyIds, TARGET_PARTY_DOM_ATTRIBUTES);
    List<TargetParty> targetParties = new ArrayList<>();

    for (Party targetPartyData : targetPartiesData) {
        Map<String, List<String>> relatedPartiesMap = targetPartyData.getRelatedPartyList().stream()
            .collect(Collectors.toMap(
                relatedParty -> relatedParty.getRole1Party().getPartyID(),
                relatedParty -> new ArrayList<>(List.of(relatedParty.getPartyRelationshipType().getID())),
                (existing, newEntry) -> {
                    existing.addAll(newEntry);
                    return existing;
                }
            ));

        List<TargetPartyRelatedParties> relatedParties = relatedPartiesMap.entrySet().stream()
            .map(entry -> TargetPartyRelatedParties.builder()
                .relatedPartyId(entry.getKey())
                .relationshipTypeIds(entry.getValue())
                .build())
            .collect(Collectors.toList());

        TargetParty targetParty = TargetParty.builder()
            .targetPartyId(targetPartyData.getPartyID())
            .targetPartyRelatedParties(relatedParties)
            .build();

        targetParties.add(targetParty);
    }

    log.info("Target party details: {}", targetParties);
    return targetParties;
}
