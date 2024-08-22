
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
