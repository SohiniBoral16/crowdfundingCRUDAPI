
var p2pHierarchyPartyQueue = new LinkedList<P2PHierarchyParty>();
p2pHierarchyPartyQueue.add(rootPartyHierarchy);

int iterationCount = 0;  // Counter for the number of iterations

while (!p2pHierarchyPartyQueue.isEmpty()) {
    iterationCount++;  // Increment the counter

    if (iterationCount > 1000) {
        throw new RuntimeException("Iteration limit exceeded: Loop executed more than 1000 times.");
    }

    var p2pHierarchyParty = p2pHierarchyPartyQueue.poll();
    var codaParty = codaDetails.get(p2pHierarchyParty.getPartyId());

    Optional.ofNullable(codaParty).ifPresent(party -> {
        var p2pHierarchyRelationshipsByPartyId = party.getRelatedParties().stream()
            .filter(p2pRel -> relatedPartiesIdsToProcess.contains(p2pRel.getRole1Party().getPartyID()))
            .collect(Collectors.toList());
        
        p2pHierarchyRelationshipsByPartyId.forEach(p2pRel -> {
            // Your existing logic here for processing related parties

            var childHierarchyParty = mapToP2PHierarchyParty(child);
            p2pHierarchyPartyQueue.add(childHierarchyParty);
        });
    });
}




-------------------------------------------
private TargetParty mapToTargetParty(Party targetPartyData) {
    // Ensure that targetPartyData and its methods return non-null values
    var relatedPartiesMap = Optional.ofNullable(targetPartyData.getRelatedPartyList())
        .map(Collection::stream)
        .orElseGet(Stream::empty)
        .collect(Collectors.toMap(
            relatedParty -> Optional.ofNullable(relatedParty.getRoleParty())
                .map(RoleParty::getPartyID)
                .orElse(null), // If RoleParty or PartyID is null, return null as the key
            relatedParty -> Optional.ofNullable(relatedParty.getPartyRelationshipType())
                .map(types -> types.stream()
                    .map(RelationshipType::getID)
                    .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new), // If PartyRelationshipType or its stream is null, return an empty list
            (existing, newEntry) -> {
                Optional.ofNullable(newEntry).ifPresent(existing::addAll);
                return existing;
            }
        ));

    var relatedParties = relatedPartiesMap.entrySet().stream()
        .map(entry -> TargetPartyRelatedParties.builder()
            .relatedPartyId(entry.getKey())
            .relationshipTypeId(entry.getValue())
            .build()
        )
        .collect(Collectors.toList());

    return TargetParty.builder()
        .targetPartyId(Optional.ofNullable(targetPartyData.getPartyID()).orElse(""))
        .targetPartyRelatedParties(relatedParties)
        .build();
}


----------------------------------
private TargetParty mapToTargetParty(Party targetPartyData) {
    // Mapping related parties with null checks
    var relatedPartiesMap = Optional.ofNullable(targetPartyData.getRelatedPartyList())
        .map(Collection::stream)
        .orElseGet(Stream::empty)
        .collect(Collectors.toMap(
            relatedParty -> Optional.ofNullable(relatedParty.getRoleParty()).map(RoleParty::getPartyID).orElse(null),
            relatedParty -> Optional.ofNullable(relatedParty.getPartyRelationshipType()).map(types -> 
                types.stream().map(RelationshipType::getID).collect(Collectors.toList())
            ).orElseGet(ArrayList::new),
            (existing, newEntry) -> {
                existing.addAll(newEntry);
                return existing;
            }
        ));

    // Converting related parties map to a list
    var relatedParties = relatedPartiesMap.entrySet().stream()
        .map(entry -> TargetPartyRelatedParties.builder()
            .relatedPartyId(entry.getKey())
            .relationshipTypeId(entry.getValue())
            .build()
        )
        .collect(Collectors.toList());

    // Building the TargetParty object
    return TargetParty.builder()
        .targetPartyId(Optional.ofNullable(targetPartyData.getPartyID()).orElse(""))
        .targetPartyRelatedParties(relatedParties)
        .build();
}




private Pair<List<String>, List<String>> getTargetPartyIdsAndValidatedPartyIds(P2PCopyRequest p2pCopyRequest) {
    List<String> targetPartyIds = new ArrayList<>();
    List<String> validatedParties = new ArrayList<>();

    p2pCopyRequest.getTargetParties().stream()
        .forEach(targetParty -> {
            Optional<P2PCopyAction> action = Optional.ofNullable(targetParty.getAction());
            if (action.isPresent() &&
                (action.get().equals(P2PCopyAction.SKIP) ||
                 action.get().equals(P2PCopyAction.OVERWRITE) ||
                 action.get().equals(P2PCopyAction.SKIP_VALIDATION))) {
                validatedParties.add(targetParty.getTargetPartyId());
            } else {
                targetPartyIds.add(targetParty.getTargetPartyId());
            }
        });

    return Pair.of(targetPartyIds, validatedParties);
}

Pair<List<String>, List<String>> partyIdsPair = getTargetPartyIdsAndValidatedPartyIds(p2pCopyRequest);

List<String> targetPartyIds = partyIdsPair.getLeft();
List<String> validatedParties = partyIdsPair.getRight();

// Use targetPartyIds and validatedParties as needed

----------------------------------------
private List<String> getSpecialActionParties(P2PCopyRequest p2pCopyRequest) {
    return p2pCopyRequest.getTargetParties().stream()
        .filter(targetParty -> {
            Optional<P2PCopyAction> action = Optional.ofNullable(targetParty.getAction());
            return action.isPresent() && (action.get() == P2PCopyAction.SKIP || action.get() == P2PCopyAction.OVERWRITE || action.get() == P2PCopyAction.SKIP_VALIDATION);
        })
        .map(P2PCopyTargetParty::getTargetPartyId)
        .collect(Collectors.toList());
}

private List<String> getTargetPartyIds(P2PCopyRequest p2pCopyRequest) {
    return p2pCopyRequest.getTargetParties().stream()
        .filter(targetParty -> {
            Optional<P2PCopyAction> action = Optional.ofNullable(targetParty.getAction());
            return action.isEmpty() || (action.get() != P2PCopyAction.SKIP && action.get() != P2PCopyAction.OVERWRITE && action.get() != P2PCopyAction.SKIP_VALIDATION);
        })
        .map(P2PCopyTargetParty::getTargetPartyId)
        .collect(Collectors.toList());
}


----------+++++++++---------
private Map<String, List<String>> getTargetPartyIdsAndSpecialActions(P2PCopyRequest p2pCopyRequest) {
    List<String> targetPartyIds = new ArrayList<>();
    List<String> specialActionParties = new ArrayList<>();

    p2pCopyRequest.getTargetParties().stream()
        .forEach(targetParty -> {
            Optional<P2PCopyAction> action = Optional.ofNullable(targetParty.getAction());
            if (action.isPresent() && 
                (action.get().equals(P2PCopyAction.SKIP) || 
                 action.get().equals(P2PCopyAction.OVERWRITE) || 
                 action.get().equals(P2PCopyAction.SKIP_VALIDATION))) {
                specialActionParties.add(targetParty.getTargetPartyId());
            } else {
                targetPartyIds.add(targetParty.getTargetPartyId());
            }
        });

    Map<String, List<String>> result = new HashMap<>();
    result.put("targetPartyIds", targetPartyIds);
    result.put("specialActionParties", specialActionParties);
    return result;
}

-------------------------
private List<P2PCopyValidationStatus> evaluateValidationStatus(
    Map<String, List<String>> sourcePartyRelationshipsMap, 
    List<TargetParty> targetParties
) {
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    // Iterate over each entry in the sourcePartyRelationshipsMap
    for (Map.Entry<String, List<String>> sourceEntry : sourcePartyRelationshipsMap.entrySet()) {
        String sourcePartyId = sourceEntry.getKey();
        List<String> sourceRelationshipTypeIds = sourceEntry.getValue();

        // Iterate over target parties
        for (TargetParty targetParty : targetParties) {
            P2PCopyValidationStatus validationStatus = new P2PCopyValidationStatus();
            validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

            List<P2PCopyRelationship> failedRelationships = new ArrayList<>();
            List<P2PCopyRelationship> successRelationships = new ArrayList<>();

            // Iterate over each related party in the target party
            for (TargetPartyRelatedParties relatedParty : targetParty.getTargetPartyRelatedParties()) {
                if (sourcePartyId.equals(relatedParty.getRelatedPartyId())) {
                    // Check for duplicate relationships
                    List<String> duplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(sourceRelationshipTypeIds::contains)
                        .collect(Collectors.toList());

                    if (!duplicateRelationshipIds.isEmpty()) {
                        // If duplicate relationships are found, add to failed relationships
                        failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
                    } else {
                        // If no duplicates, add all relationships as success
                        successRelationships.add(new P2PCopyRelationship(sourcePartyId, relatedParty.getRelationshipTypeId()));
                    }
                }
            }

            // Set the status and relationships for the current target party
            if (!failedRelationships.isEmpty()) {
                validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
                validationStatus.setCopyFailedRelationships(failedRelationships);
            } else {
                validationStatus.setStatus("READY_TO_COPY");
            }

            validationStatus.setCopySuccessRelationships(successRelationships);
            validationStatuses.add(validationStatus);
        }
    }

    return validationStatuses;
}


private List<P2PCopyValidationStatus> evaluateValidationStatus(
    Map<String, List<String>> sourcePartyRelationshipsMap, 
    List<TargetParty> targetParties
) {
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    for (Map.Entry<String, List<String>> sourceEntry : sourcePartyRelationshipsMap.entrySet()) {
        String sourcePartyId = sourceEntry.getKey();
        List<String> sourceRelationshipTypeIds = sourceEntry.getValue();

        for (TargetParty targetParty : targetParties) {
            P2PCopyValidationStatus validationStatus = new P2PCopyValidationStatus();
            validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

            List<P2PCopyRelationship> failedRelationships = new ArrayList<>();
            List<P2PCopyRelationship> successRelationships = new ArrayList<>();

            for (TargetPartyRelatedParties relatedParty : targetParty.getTargetPartyRelatedParties()) {
                if (relatedParty.getRelatedPartyId().equals(sourcePartyId)) {
                    // Compare the source relationship types with the target party's related relationship types
                    List<String> matchedRelationshipTypeIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(sourceRelationshipTypeIds::contains)
                        .collect(Collectors.toList());

                    if (!matchedRelationshipTypeIds.isEmpty()) {
                        // If any matching relationship type IDs are found, it is a duplicate
                        failedRelationships.add(new P2PCopyRelationship(sourcePartyId, matchedRelationshipTypeIds));
                    }

                    // Add non-duplicate relationships from sourceParty to successRelationships
                    List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                        .filter(typeId -> !matchedRelationshipTypeIds.contains(typeId))
                        .collect(Collectors.toList());

                    if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                        successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
                    }
                } else {
                    // If the related party ID does not match any source party ID, add it as a successful relationship
                    successRelationships.add(new P2PCopyRelationship(relatedParty.getRelatedPartyId(), relatedParty.getRelationshipTypeId()));
                }
            }

            // Set status and relationships for the current target party
            if (!failedRelationships.isEmpty()) {
                validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
                validationStatus.setCopyFailedRelationships(failedRelationships);
            } else {
                validationStatus.setStatus("READY_TO_COPY");
            }
            
            // Add all success relationships
            validationStatus.setCopySuccessRelationships(successRelationships);

            validationStatuses.add(validationStatus);
        }
    }

    return validationStatuses;
}


private List<P2PCopyValidationStatus> evaluateValidationStatus(
    Map<String, List<String>> sourcePartyRelationshipsMap, 
    List<TargetParty> targetParties
) {
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    for (Map.Entry<String, List<String>> sourceEntry : sourcePartyRelationshipsMap.entrySet()) {
        String sourcePartyId = sourceEntry.getKey();
        List<String> sourceRelationshipTypeIds = sourceEntry.getValue();

        for (TargetParty targetParty : targetParties) {
            P2PCopyValidationStatus validationStatus = new P2PCopyValidationStatus();
            validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

            List<P2PCopyRelationship> failedRelationships = new ArrayList<>();
            List<P2PCopyRelationship> successRelationships = new ArrayList<>();

            for (TargetPartyRelatedParties relatedParty : targetParty.getTargetPartyRelatedParties()) {
                if (relatedParty.getRelatedPartyId().equals(sourcePartyId)) {
                    // Compare the source relationship types with the target party's related relationship types
                    List<String> matchedRelationshipTypeIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(sourceRelationshipTypeIds::contains)
                        .collect(Collectors.toList());

                    if (!matchedRelationshipTypeIds.isEmpty()) {
                        // If any matching relationship type IDs are found, it is a duplicate
                        failedRelationships.add(new P2PCopyRelationship(sourcePartyId, matchedRelationshipTypeIds));
                    } else {
                        // No duplicates found, it's ready to copy
                        successRelationships.add(new P2PCopyRelationship(sourcePartyId, relatedParty.getRelationshipTypeId()));
                    }
                } else {
                    // If the related party ID does not match any source party ID, it's ready to copy
                    successRelationships.add(new P2PCopyRelationship(relatedParty.getRelatedPartyId(), relatedParty.getRelationshipTypeId()));
                }
            }

            // Set status and relationships for the current target party
            if (!failedRelationships.isEmpty()) {
                validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
                validationStatus.setCopyFailedRelationships(failedRelationships);
            } else {
                validationStatus.setStatus("READY_TO_COPY");
                validationStatus.setCopySuccessRelationships(successRelationships);
            }

            validationStatuses.add(validationStatus);
        }
    }

    return validationStatuses;
}

-------------------------------
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
