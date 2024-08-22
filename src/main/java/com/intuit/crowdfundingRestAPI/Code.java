
private List<P2PCopyValidationStatus> evaluateValidationStatus(P2PCopyRequest p2PCopyRequest, List<TargetParty> targetParties) {

// Step 1: Modify the validateCopyRequest method to calculate the map
public List<P2PCopyValidationStatus> validateCopyRequest(P2PCopyRequest p2PCopyRequest) {
    // Calculate the sourcePartyId to relationshipTypeIds map
    Map<String, List<String>> sourcePartyRelationshipsMap = p2PCopyRequest.getSourceRelationships().stream()
        .collect(Collectors.toMap(
            P2PCopyRelationship::getSourcePartyId,
            P2PCopyRelationship::getRelationshipTypeIds
        ));

    // Step 2: Fetch target parties from CODA
    var targetPartyIds = getTargetPartyIds(p2PCopyRequest);
    var targetParties = fetchTargetParty(targetPartyIds);

    // Step 3: Evaluate validation status
    var validationStatuses = evaluateValidationStatus(p2PCopyRequest, targetParties, sourcePartyRelationshipsMap);

    return validationStatuses;
}

// Step 4: Modify evaluateValidationStatus method to accept the map
private List<P2PCopyValidationStatus> evaluateValidationStatus(P2PCopyRequest p2PCopyRequest, List<TargetParty> targetParties, Map<String, List<String>> sourcePartyRelationshipsMap) {
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    for (var targetParty : targetParties) {
        var validationStatus = new P2PCopyValidationStatus();
        var failedRelationships = new ArrayList<P2PCopyRelationship>();
        boolean hasDuplicate = false;
        validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

        // Step 5: Use the map in the loop
        for (var entry : sourcePartyRelationshipsMap.entrySet()) {
            String sourcePartyId = entry.getKey();
            List<String> sourcePartyRelationshipTypeIds = entry.getValue();

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
