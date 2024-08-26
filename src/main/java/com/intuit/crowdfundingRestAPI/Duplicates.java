
for (Map.Entry<String, List<String>> relatedPartyIdRelationsEntry : relatedPartyIdToRelationshipIdsByTargetPartyId.entrySet()) {
    String relatedPartyId = relatedPartyIdRelationsEntry.getKey();
    List<String> targetRelationshipTypeIds = relatedPartyIdRelationsEntry.getValue();

    // Initialize lists for duplicate and non-duplicate relationships
    List<String> duplicateRelationshipIds = new ArrayList<>();
    List<String> nonDuplicateRelationshipIds = new ArrayList<>();

    if (sourceRelationshipsByPartyId.containsKey(relatedPartyId)) {
        List<String> sourceRelationshipTypeIds = sourceRelationshipsByPartyId.get(relatedPartyId);

        // Separate duplicate and non-duplicate relationships
        for (String sourceTypeId : sourceRelationshipTypeIds) {
            if (targetRelationshipTypeIds != null && targetRelationshipTypeIds.contains(sourceTypeId)) {
                duplicateRelationshipIds.add(sourceTypeId);
            } else {
                nonDuplicateRelationshipIds.add(sourceTypeId);
            }
        }
    } else {
        // If relatedPartyId is not found in target, consider all source relationships as non-duplicate
        nonDuplicateRelationshipIds.addAll(sourceRelationshipsByPartyId.getOrDefault(relatedPartyId, Collections.emptyList()));
    }

    // Add the duplicate relationships to the failed relationships list if any exist
    if (!duplicateRelationshipIds.isEmpty()) {
        copyFailedRelationships.add(new P2PCopyRelationship(relatedPartyId, duplicateRelationshipIds));
        p2pCopyStatus = P2PCopyStatus.DUPLICATE_RELATIONSHIP_EXISTS;
    }

    // Add the non-duplicate relationships to the success relationships list if any exist
    if (!nonDuplicateRelationshipIds.isEmpty()) {
        copySuccessRelationships.add(new P2PCopyRelationship(relatedPartyId, nonDuplicateRelationshipIds));
    }
}





for (Map.Entry<String, List<String>> relatedPartyIdRelationsEntry : relatedPartyIdToRelationshipIdsByTargetPartyId.entrySet()) {
    String relatedPartyId = relatedPartyIdRelationsEntry.getKey();
    List<String> targetRelationshipTypeIds = relatedPartyIdRelationsEntry.getValue();

    // Initialize lists for duplicate and non-duplicate relationships
    List<String> duplicateRelationshipIds = new ArrayList<>();
    List<String> nonDuplicateRelationshipIds = new ArrayList<>();

    if (sourceRelationshipsByPartyId.containsKey(relatedPartyId)) {
        List<String> sourceRelationshipTypeIds = sourceRelationshipsByPartyId.get(relatedPartyId);

        // Separate duplicate and non-duplicate relationships
        for (String typeId : targetRelationshipTypeIds) {
            if (sourceRelationshipTypeIds != null && sourceRelationshipTypeIds.contains(typeId)) {
                duplicateRelationshipIds.add(typeId);
            } else {
                nonDuplicateRelationshipIds.add(typeId);
            }
        }
    } else {
        // If relatedPartyId is not found in sourceRelationshipsByPartyId, consider all as non-duplicate
        nonDuplicateRelationshipIds.addAll(targetRelationshipTypeIds);
    }

    // Add the duplicate relationships to the failed relationships list if any exist
    if (!duplicateRelationshipIds.isEmpty()) {
        copyFailedRelationships.add(new P2PCopyRelationship(relatedPartyId, duplicateRelationshipIds));
        p2pCopyStatus = P2PCopyStatus.DUPLICATE_RELATIONSHIP_EXISTS;
    }

    // Add the non-duplicate relationships to the success relationships list if any exist
    if (!nonDuplicateRelationshipIds.isEmpty()) {
        copySuccessRelationships.add(new P2PCopyRelationship(relatedPartyId, nonDuplicateRelationshipIds));
    }
}
--------------------
// Calculate skipTargetParties
Set<P2PCopyTargetParty> skipTargetParties = 
    p2CopyRequest.getTargetParties() != null 
        ? p2CopyRequest.getTargetParties().stream()
            .filter(targetParty -> P2PCopyAction.SKIP.equals(targetParty.getAction()))
            .collect(Collectors.toSet())
        : Collections.emptySet();

// Calculate excludeValidationTargetParties
Set<P2PCopyTargetParty> excludeValidationTargetParties = 
    p2CopyRequest.getTargetParties() != null 
        ? p2CopyRequest.getTargetParties().stream()
            .filter(targetParty -> 
                P2PCopyAction.OVERWRITE.equals(targetParty.getAction()) 
                || P2PCopyAction.SKIP_VALIDATION.equals(targetParty.getAction()))
            .collect(Collectors.toSet())
        : Collections.emptySet();

// Remove skip and exclude validation target parties from the main list
p2CopyRequest.getTargetParties().removeAll(skipTargetParties);
p2CopyRequest.getTargetParties().removeAll(excludeValidationTargetParties);


// Step 1: Filter out target parties not to be validated. Assume P2PCopyRequest has equals set on TargetPartyId
p2pCopyRequest.getTargetParties().removeAll(
    Optional.ofNullable(p2pCopyRequest.getTargetParties().stream()
        .filter(targetParty -> P2PCopyAction.SKIP.equals(targetParty.getAction()))
        .collect(Collectors.toSet())).orElse(Collections.emptySet())
);

p2pCopyRequest.getTargetParties().removeAll(
    Optional.ofNullable(p2pCopyRequest.getTargetParties().stream()
        .filter(targetParty -> List.of(P2PCopyAction.OVERWRITE, P2PCopyAction.SKIP_VALIDATION).contains(targetParty.getAction()))
        .collect(Collectors.toSet())).orElse(Collections.emptySet())
);




public List<P2PCopyValidationStatus> evaluateValidationStatus(
        Map<String, List<String>> sourcePartyRelationshipsMap,
        List<TargetParty> targetParties,
        Map<String, String> validatedPartyMap) {

    var validationStatusMap = new HashMap<String, P2PCopyValidationStatus>();

    // Iterate over sourcePartyRelationshipsMap to handle target parties
    sourcePartyRelationshipsMap.forEach((sourcePartyId, sourceRelationshipTypeIds) -> {
        for (TargetParty targetParty : targetParties) {
            var status = validationStatusMap.computeIfAbsent(targetParty.getTargetPartyId(), id -> {
                P2PCopyValidationStatus s = new P2PCopyValidationStatus();
                s.setTargetPartyId(id);
                s.setCopyFailedRelationships(new ArrayList<>());
                s.setCopySuccessRelationships(new ArrayList<>());
                return s;
            });

            var failedRelationships = status.getCopyFailedRelationships();
            var successRelationships = status.getCopySuccessRelationships();

            List<String> duplicateRelationshipIds = targetParty.getTargetPartyRelatedParties().stream()
                .filter(relatedParty -> sourcePartyId.equals(relatedParty.getRelatedPartyId()))
                .flatMap(relatedParty -> relatedParty.getRelationshipTypeId().stream())
                .filter(sourceRelationshipTypeIds::contains)
                .collect(Collectors.toList());

            List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                .filter(id -> !duplicateRelationshipIds.contains(id))
                .collect(Collectors.toList());

            if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
            }

            if (!duplicateRelationshipIds.isEmpty()) {
                failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
            }

            if (duplicateRelationshipIds.isEmpty()) {
                successRelationships.add(new P2PCopyRelationship(sourcePartyId, sourceRelationshipTypeIds));
            }
        }
    });

    // Handle already validated parties and use the provided status from the map
    validatedPartyMap.forEach((validatedPartyId, statusFromMap) -> {
        var status = validationStatusMap.computeIfAbsent(validatedPartyId, id -> {
            P2PCopyValidationStatus s = new P2PCopyValidationStatus();
            s.setTargetPartyId(id);
            s.setCopySuccessRelationships(new ArrayList<>());
            return s;
        });
        status.setStatus(statusFromMap);
    });

    return new ArrayList<>(validationStatusMap.values());
}


private Pair<Map<String, String>, List<String>> getTargetPartyIdsAndValidatedPartyIds(P2PCopyRequest p2pCopyRequest) {
    List<String> targetPartyIds = new ArrayList<>();
    Map<String, String> validatedPartiesMap = new HashMap<>();

    p2pCopyRequest.getTargetParties().stream().forEach(targetParty -> {
        Optional<P2PCopyAction> action = Optional.ofNullable(targetParty.getAction());
        if (action.isPresent() && (action.get().equals(P2PCopyAction.OVERWRITE) || action.get().equals(P2PCopyAction.SKIP_VALIDATION))) {
            validatedPartiesMap.put(targetParty.getTargetPartyId(), action.get().toString());
        } else {
            targetPartyIds.add(targetParty.getTargetPartyId());
        }
    });

    return Pair.of(validatedPartiesMap, targetPartyIds);
}

public P2PCopyResponse validateCopyRequest(P2PCopyRequest p2pCopyRequest) {
    // Step 1: Process request
    Pair<Map<String, String>, List<String>> partyIdsPair = getTargetPartyIdsAndValidatedPartyIds(p2pCopyRequest);
    var targetPartyIds = partyIdsPair.getRight();
    var validatedPartiesMap = partyIdsPair.getLeft();

    // Step 2: Get targetParty from coda
    var targetParties = fetchTargetParty(targetPartyIds);

    // Step 3: Calculate sourcePartyId to relationshipTypeIds map
    Map<String, List<String>> sourcePartyRelationshipsMap = p2pCopyRequest.getSourceRelationships().stream()
        .collect(Collectors.toMap(P2PCopyRelationship::getSourcePartyId, P2PCopyRelationship::getRelationshipTypeIds));

    // Step 4: Evaluate validation status
    var validationStatuses = evaluateValidationStatus(sourcePartyRelationshipsMap, targetParties, validatedPartiesMap);

    // Step 5: Build response
    var p2pCopyResponse = new P2PCopyResponse();
    p2pCopyResponse.setCopyStatus(
        validationStatuses.stream()
            .anyMatch(status -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(status.getStatus()) || "SKIP".equals(status.getStatus())) 
                ? P2PCopyStatus.VALIDATION_FAILURE 
                : P2PCopyStatus.VALIDATION_SUCCESS
    );
    p2pCopyResponse.setValidationStatuses(validationStatuses);

    log.info("Validation response of Copy P2P relationship from main party: {}, {}", p2pCopyRequest.getMainParty(), p2pCopyResponse);
    return p2pCopyResponse;
}

public List<P2PCopyValidationStatus> evaluateValidationStatus(Map<String, List<String>> sourcePartyRelationshipsMap, 
    List<TargetParty> targetParties, Map<String, String> validatedPartiesMap) {

    var validationStatusesMap = new HashMap<String, P2PCopyValidationStatus>();

    // Existing logic for calculating the validation statuses for target parties
    // ...

    // Handle already validated parties and use the provided status from the map
    validatedPartiesMap.forEach((validatedPartyId, statusFromMap) -> {
        P2PCopyValidationStatus validatedStatus = validationStatusesMap.computeIfAbsent(validatedPartyId, id -> {
            var s = new P2PCopyValidationStatus();
            s.setTargetPartyId(id);
            s.setStatus(statusFromMap);
            s.setCopySuccessRelationships(new ArrayList<>());
            return s;
        });

        // Add all relationships from the source that are marked as validated
        sourcePartyRelationshipsMap.forEach((sourcePartyId, relationshipTypeIds) -> {
            validatedStatus.getCopySuccessRelationships().add(
                new P2PCopyRelationship(sourcePartyId, relationshipTypeIds)
            );
        });
    });

    return new ArrayList<>(validationStatusesMap.values());
}

----------------------------------------
public List<P2PCopyValidationStatus> evaluateValidationStatus(
    Map<String, List<String>> sourcePartyRelationshipsMap, 
    List<TargetParty> targetParties, 
    List<String> validatedParties) {

    var validationStatusMap = new HashMap<String, P2PCopyValidationStatus>();

    // Process the target parties for validation status
    sourcePartyRelationshipsMap.forEach((sourcePartyId, sourceRelationshipTypeIds) -> {
        targetParties.forEach(targetParty -> {
            P2PCopyValidationStatus status = validationStatusMap.computeIfAbsent(targetParty.getTargetPartyId(), id -> {
                var s = new P2PCopyValidationStatus();
                s.setTargetPartyId(id);
                s.setCopyFailedRelationships(new ArrayList<>());
                s.setCopySuccessRelationships(new ArrayList<>());
                return s;
            });

            List<P2PCopyRelationship> failedRelationships = status.getCopyFailedRelationships();
            List<P2PCopyRelationship> successRelationships = status.getCopySuccessRelationships();
            boolean isMatchFound = false;

            for (TargetPartyRelatedParties relatedParty : targetParty.getTargetPartyRelatedParties()) {
                if (sourcePartyId.equals(relatedParty.getRelatedPartyId())) {
                    isMatchFound = true;

                    List<String> duplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(sourceRelationshipTypeIds::contains)
                        .collect(Collectors.toList());

                    List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                        .filter(id -> !duplicateRelationshipIds.contains(id))
                        .collect(Collectors.toList());

                    if (!duplicateRelationshipIds.isEmpty()) {
                        failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
                    }
                    if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                        successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
                    }
                }
            }

            if (!isMatchFound) {
                successRelationships.add(new P2PCopyRelationship(sourcePartyId, sourceRelationshipTypeIds));
            }

            status.setStatus(failedRelationships.isEmpty() ? "READY_TO_COPY" : "DUPLICATE_RELATIONSHIP_EXISTS");
        });
    });

    // Handle already validated parties
    validatedParties.forEach(validatedPartyId -> {
        P2PCopyValidationStatus validatedStatus = validationStatusMap.computeIfAbsent(validatedPartyId, id -> {
            var s = new P2PCopyValidationStatus();
            s.setTargetPartyId(id);
            s.setCopySuccessRelationships(new ArrayList<>());
            return s;
        });

        // Add all relationships from the source that are marked as validated
        sourcePartyRelationshipsMap.forEach((sourcePartyId, relationshipTypeIds) -> {
            validatedStatus.getCopySuccessRelationships().add(new P2PCopyRelationship(
                sourcePartyId,
                relationshipTypeIds
            ));
        });
    });

    return new ArrayList<>(validationStatusMap.values());
}

------------------------------------------
public List<P2PCopyValidationStatus> evaluateValidationStatus(
    List<TargetParty> targetParties, Map<String, List<String>> sourcePartyRelationshipsMap) {

    // Map to hold validation status for each targetPartyId
    Map<String, P2PCopyValidationStatus> validationStatusMap = new HashMap<>();

    sourcePartyRelationshipsMap.forEach((sourcePartyId, sourceRelationshipTypeIds) -> {
        targetParties.forEach(targetParty -> {
            // Get or initialize the validation status for the target party
            P2PCopyValidationStatus validationStatus = validationStatusMap
                .computeIfAbsent(targetParty.getTargetPartyId(), id -> {
                    P2PCopyValidationStatus status = new P2PCopyValidationStatus();
                    status.setTargetPartyId(id);
                    status.setCopyFailedRelationships(new ArrayList<>());
                    status.setCopySuccessRelationships(new ArrayList<>());
                    return status;
                });

            List<P2PCopyRelationship> failedRelationships = validationStatus.getCopyFailedRelationships();
            List<P2PCopyRelationship> successRelationships = validationStatus.getCopySuccessRelationships();

            boolean isMatchFound = targetParty.getTargetPartyRelatedParties().stream()
                .filter(relatedParty -> sourcePartyId.equals(relatedParty.getRelatedPartyId()))
                .peek(relatedParty -> {
                    // Identify duplicate relationships
                    List<String> duplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(sourceRelationshipTypeIds::contains)
                        .collect(Collectors.toList());

                    // Add non-duplicate relationships to success relationships
                    List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                        .filter(id -> !duplicateRelationshipIds.contains(id))
                        .collect(Collectors.toList());

                    if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                        successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
                    }

                    // Add duplicate relationships to failed relationships
                    if (!duplicateRelationshipIds.isEmpty()) {
                        failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
                    }
                })
                .findAny()
                .isPresent();

            // If no match was found for the sourcePartyId, treat all relationships as non-duplicate
            if (!isMatchFound) {
                successRelationships.add(new P2PCopyRelationship(sourcePartyId, sourceRelationshipTypeIds));
            }

            // Set the status based on whether any failed relationships were found
            validationStatus.setStatus(failedRelationships.isEmpty() ? "READY_TO_COPY" : "DUPLICATE_RELATIONSHIP_EXISTS");
        });
    });

    return new ArrayList<>(validationStatusMap.values());
}




----------------------------------------
public List<P2PCopyValidationStatus> evaluateValidationStatus(
    List<TargetParty> targetParties, Map<String, List<String>> sourcePartyRelationshipsMap) {

    Map<String, P2PCopyValidationStatus> validationStatusMap = new HashMap<>();

    // Iterate over each entry in the sourcePartyRelationshipsMap
    for (Map.Entry<String, List<String>> sourceEntry : sourcePartyRelationshipsMap.entrySet()) {
        String sourcePartyId = sourceEntry.getKey();
        List<String> sourceRelationshipTypeIds = sourceEntry.getValue();

        // Iterate over target parties
        for (TargetParty targetParty : targetParties) {
            // Get or create the P2PCopyValidationStatus for the targetParty
            P2PCopyValidationStatus validationStatus = validationStatusMap
                .computeIfAbsent(targetParty.getTargetPartyId(), id -> {
                    P2PCopyValidationStatus status = new P2PCopyValidationStatus();
                    status.setTargetPartyId(id);
                    status.setCopyFailedRelationships(new ArrayList<>());
                    status.setCopySuccessRelationships(new ArrayList<>());
                    return status;
                });

            List<P2PCopyRelationship> failedRelationships = validationStatus.getCopyFailedRelationships();
            List<P2PCopyRelationship> successRelationships = validationStatus.getCopySuccessRelationships();

            boolean matchFound = false;

            // Iterate over each related party in the target party
            for (TargetPartyRelatedParties relatedParty : targetParty.getTargetPartyRelatedParties()) {
                if (sourcePartyId.equals(relatedParty.getRelatedPartyId())) {
                    matchFound = true;

                    // Identify duplicate relationships
                    List<String> duplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(sourceRelationshipTypeIds::contains)
                        .collect(Collectors.toList());

                    // Identify relationships from source that are not duplicates
                    List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                        .filter(id -> !duplicateRelationshipIds.contains(id))
                        .collect(Collectors.toList());

                    // Add non-duplicate relationships to success relationships
                    if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                        successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
                    }

                    // Add duplicate relationships to failed relationships
                    if (!duplicateRelationshipIds.isEmpty()) {
                        failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
                    }
                }
            }

            // If no match was found for the sourcePartyId, treat all relationships as non-duplicate
            if (!matchFound) {
                successRelationships.add(new P2PCopyRelationship(sourcePartyId, sourceRelationshipTypeIds));
            }

            // Set the status for the current target party based on failed relationships
            if (!failedRelationships.isEmpty()) {
                validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
            } else {
                validationStatus.setStatus("READY_TO_COPY");
            }
        }
    }

    return new ArrayList<>(validationStatusMap.values());
}




public List<P2PCopyValidationStatus> evaluateValidationStatus(
    List<TargetParty> targetParties, Map<String, List<String>> sourcePartyRelationshipsMap) {

    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    // Iterate over each entry in the sourcePartyRelationshipsMap
    for (Map.Entry<String, List<String>> sourceEntry : sourcePartyRelationshipsMap.entrySet()) {
        String sourcePartyId = sourceEntry.getKey();
        List<String> sourceRelationshipTypeIds = sourceEntry.getValue();

        // Iterate over target parties
        for (TargetParty targetParty : targetParties) {
            // Check if a validation status for this targetParty already exists
            P2PCopyValidationStatus validationStatus = validationStatuses.stream()
                .filter(status -> status.getTargetPartyId().equals(targetParty.getTargetPartyId()))
                .findFirst()
                .orElseGet(() -> {
                    P2PCopyValidationStatus newStatus = new P2PCopyValidationStatus();
                    newStatus.setTargetPartyId(targetParty.getTargetPartyId());
                    validationStatuses.add(newStatus);
                    return newStatus;
                });

            List<P2PCopyRelationship> failedRelationships = validationStatus.getCopyFailedRelationships();
            List<P2PCopyRelationship> successRelationships = validationStatus.getCopySuccessRelationships();

            // Iterate over each related party in the target party
            for (TargetPartyRelatedParties relatedParty : targetParty.getTargetPartyRelatedParties()) {
                if (sourcePartyId.equals(relatedParty.getRelatedPartyId())) {
                    // Check for duplicate relationships
                    List<String> duplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(sourceRelationshipTypeIds::contains)
                        .collect(Collectors.toList());

                    // Add to failed relationships if duplicates are found
                    if (!duplicateRelationshipIds.isEmpty()) {
                        failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
                        validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
                    }

                    // Add non-duplicate relationships to success list
                    List<String> nonDuplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                        .filter(id -> !duplicateRelationshipIds.contains(id))
                        .collect(Collectors.toList());

                    if (!nonDuplicateRelationshipIds.isEmpty()) {
                        successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipIds));
                    }
                } else {
                    // If the sourcePartyId does not match, add all as non-duplicates
                    successRelationships.add(new P2PCopyRelationship(sourcePartyId, sourceRelationshipTypeIds));
                }
            }

            // Set status if there are no failed relationships
            if (failedRelationships.isEmpty()) {
                validationStatus.setStatus("READY_TO_COPY");
            }

            // Update the relationships in the validation status
            validationStatus.setCopyFailedRelationships(failedRelationships);
            validationStatus.setCopySuccessRelationships(successRelationships);
        }
    }

    return validationStatuses;
}



----------------------------
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

        boolean matchFound = false;

        // Iterate over each related party in the target party
        for (TargetPartyRelatedParties relatedParty : targetParty.getTargetPartyRelatedParties()) {
            if (sourcePartyId.equals(relatedParty.getRelatedPartyId())) {
                matchFound = true;

                // Identify duplicate relationships
                List<String> duplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                    .filter(sourceRelationshipTypeIds::contains)
                    .collect(Collectors.toList());

                // Identify relationships from source that are not duplicates
                List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                    .filter(id -> !duplicateRelationshipIds.contains(id))
                    .collect(Collectors.toList());

                // Add non-duplicate relationships to success relationships
                if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                    successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
                }

                // Add duplicate relationships to failed relationships
                if (!duplicateRelationshipIds.isEmpty()) {
                    failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
                }
            }
        }

        // If no match was found for the sourcePartyId, treat all relationships as non-duplicate
        if (!matchFound) {
            successRelationships.add(new P2PCopyRelationship(sourcePartyId, sourceRelationshipTypeIds));
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


----------+------------------------
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

                // Calculate non-duplicate relationships from the sourcePartyId relationshipTypeIds list
                List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                    .filter(id -> !duplicateRelationshipIds.contains(id))
                    .collect(Collectors.toList());

                if (!duplicateRelationshipIds.isEmpty()) {
                    // If duplicate relationships are found, add to failed relationships
                    failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
                }

                if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                    // Add non-duplicate relationships as success relationships
                    successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
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

----------------------

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
                
                // Identify duplicate relationships
                List<String> duplicateRelationshipIds = relatedParty.getRelationshipTypeId().stream()
                    .filter(sourceRelationshipTypeIds::contains)
                    .collect(Collectors.toList());

                // Identify relationships from source that are not duplicates
                List<String> nonDuplicateRelationshipTypeIds = sourceRelationshipTypeIds.stream()
                    .filter(id -> !duplicateRelationshipIds.contains(id))
                    .collect(Collectors.toList());

                // Add non-duplicate relationships to success relationships
                if (!nonDuplicateRelationshipTypeIds.isEmpty()) {
                    successRelationships.add(new P2PCopyRelationship(sourcePartyId, nonDuplicateRelationshipTypeIds));
                }

                // Add duplicate relationships to failed relationships
                if (!duplicateRelationshipIds.isEmpty()) {
                    failedRelationships.add(new P2PCopyRelationship(sourcePartyId, duplicateRelationshipIds));
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
