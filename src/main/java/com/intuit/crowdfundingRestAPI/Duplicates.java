
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
