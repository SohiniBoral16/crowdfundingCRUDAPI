
private List<P2PCopyValidationStatus> evaluateValidationStatus(P2PCopyRequest p2PCopyRequest, List<TargetParty> targetParties) {
    // Step 1: Iterate over sourceRelationships
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    for (var targetParty : targetParties) {
        var validationStatus = new P2PCopyValidationStatus();
        var failedRelationships = new ArrayList<P2PCopyRelationship>();
        boolean hasDuplicate = false;
        validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

        for (P2PCopyRelationship sourceRelationship : p2PCopyRequest.getSourceRelationships()) {
            String sourcePartyId = sourceRelationship.getSourcePartyId();
            List<String> sourcePartyRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

            // Step 2: Iterate over targetParty and find failed relationships
            hasDuplicate = findFailedRelationships(targetParty, sourcePartyId, sourcePartyRelationshipTypeIds, failedRelationships) || hasDuplicate;
        }

        if (hasDuplicate) {
            validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
            validationStatus.setCopyFailedRelationships(failedRelationships);
        } else {
            validationStatus.setStatus("READY_TO_COPY");
        }

        validationStatuses.add(validationStatus);
    }

    return validationStatuses;
}

private boolean findFailedRelationships(TargetParty targetParty, String sourcePartyId, List<String> sourcePartyRelationshipTypeIds, List<P2PCopyRelationship> failedRelationships) {
    boolean hasDuplicate = false;

    for (var relatedParty : targetParty.getTargetPartyRelatedParties()) {
        boolean sourcePartyIdMatches = relatedParty.getRelatedPartyId().equalsIgnoreCase(sourcePartyId);
        List<String> matchedSourceRelationshipTypeIds = sourcePartyRelationshipTypeIds.stream()
            .filter(id -> id.equals(relatedParty.getRelationshipTypeId()))
            .collect(Collectors.toList());

        if (sourcePartyIdMatches && !matchedSourceRelationshipTypeIds.isEmpty()) {
            hasDuplicate = true;
            failedRelationships.add(new P2PCopyRelationship(sourcePartyId, matchedSourceRelationshipTypeIds));
        }
    }

    return hasDuplicate;
}

--------------------------------------

private List<TargetParty> fetchTargetParty(List<String> targetPartyIds) {
    var targetPartiesData = getPartiesFromCoda(targetPartyIds, TARGET_PARTY_DOM_ATTRIBUTES);

    List<TargetParty> targetParties = targetPartiesData.stream()
        .map(this::transformToTargetParty)
        .collect(Collectors.toList());

    log.info("Target party details: {}", targetParties);
    return targetParties;
}

private TargetParty transformToTargetParty(Party targetPartyData) {
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

    return TargetParty.builder()
        .targetPartyId(targetPartyData.getPartyID())
        .targetPartyRelatedParties(relatedParties)
        .build();
}



------------------------------
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
