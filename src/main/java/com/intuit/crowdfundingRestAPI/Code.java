
if (!targetPartyIds.isEmpty()) {
    var targetParties = fetchTargetParty(targetPartyIds);
    var validationStatuses = evaluateValidationStatus(p2PCopyRequest, targetParties);

    // Calculate the non-validated statuses
    List<P2PCopyValidationStatus> nonValidatedStatuses = p2PCopyRequest.getTargetParties().stream()
        .filter(targetParty -> !targetPartyIds.contains(targetParty.getTargetPartyId()))
        .map(targetParty -> {
            P2PCopyValidationStatus status = new P2PCopyValidationStatus();
            status.setTargetPartyId(targetParty.getTargetPartyId());
            status.setStatus(targetParty.getAction() != null ? targetParty.getAction().toString() : "NO_ACTION");
            return status;
        })
        .collect(Collectors.toList());

    // Combine validated and non-validated statuses
    validationStatuses.addAll(nonValidatedStatuses);

    p2PCopyResponse.setCopyStatus(
        validationStatuses.stream()
            .anyMatch(status -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(status.getStatus()))
            ? P2PCopyStatus.VALIDATION_FAILURE
            : P2PCopyStatus.VALIDATION_SUCCESS
    );

    p2PCopyResponse.setValidationStatus(validationStatuses);
    log.info("Validation response of Copy P2P relationship from main party: {} to target party: {}", p2pCopyRequest.getMainParty(), p2PCopyResponse);
} else {
    p2PCopyResponse = addNonValidatePartiesToCopyResponse(p2PCopyRequest);
}




else {
    boolean hasSkipAction = p2PCopyRequest.getTargetParties().stream()
        .anyMatch(targetParty -> P2PCopyAction.SKIP.equals(targetParty.getAction()));
    
    // Set the copy status based on the presence of SKIP action
    p2PCopyResponse.setCopyStatus(hasSkipAction ? P2PCopyStatus.VALIDATION_FAILURE : P2PCopyStatus.VALIDATION_SUCCESS);
    
    // Add the non-validated parties to the response
    List<P2PCopyValidationStatus> validationStatuses = addNonValidatedParties(p2PCopyRequest);
    p2PCopyResponse.setValidationStatus(validationStatuses);
    
    return p2PCopyResponse;
}


public P2PCopyResponse validateCopyRequest(P2PCopyRequest p2PCopyRequest) {
    var targetPartyIds = validateTargetPartyIds(p2PCopyRequest);
    P2PCopyResponse p2PCopyResponse = new P2PCopyResponse();

    if (!targetPartyIds.isEmpty()) {
        var targetParties = fetchTargetParty(targetPartyIds);
        var validationStatuses = evaluateValidationStatus(p2PCopyRequest, targetParties);

        // Adding non-validated parties to the validation status
        List<P2PCopyValidationStatus> nonValidatedStatuses = addNonValidatedParties(p2PCopyRequest);

        // Merge validated statuses with non-validated statuses
        validationStatuses.addAll(nonValidatedStatuses);

        p2PCopyResponse.setCopyStatus(
            validationStatuses.stream()
                .anyMatch(status -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(status.getStatus()))
                ? P2PCopyStatus.VALIDATION_FAILURE
                : P2PCopyStatus.VALIDATION_SUCCESS
        );

        p2PCopyResponse.setValidationStatus(validationStatuses);
        log.info("Validation response of Copy P2P relationship from main party: {} to target party: {}", p2pCopyRequest.getMainParty(), p2PCopyResponse);
    } else {
        p2PCopyResponse = addNonValidatePartiesToCopyResponse(p2PCopyRequest);
    }
    
    return p2PCopyResponse;
}

private List<P2PCopyValidationStatus> addNonValidatedParties(P2PCopyRequest p2PCopyRequest) {
    return p2PCopyRequest.getTargetParties().stream()
        .map(party -> {
            P2PCopyValidationStatus status = new P2PCopyValidationStatus();
            status.setTargetPartyId(party.getTargetPartyId());
            status.setStatus(party.getAction() != null ? party.getAction().toString() : "NO_ACTION");
            return status;
        })
        .collect(Collectors.toList());
}


---------------------
if (!targetPartyIds.isEmpty()) {
    // Logic when targetPartyIds is not empty
    var targetParties = fetchTargetParty(targetPartyIds);
    var validationStatus = evaluateValidationStatus(p2pCopyRequest, targetParties);
    P2PCopyResponse p2PCopyResponse = new P2PCopyResponse();
    
    p2PCopyResponse.setCopyStatus(
        validationStatus.stream()
            .anyMatch(status -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(status.getStatus()))
            ? "VALIDATION_FAILURE"
            : "VALIDATION_SUCCESS"
    );
    
    p2PCopyResponse.setValidationStatus(validationStatus);
    log.info("Validation response of Copy P2P relationship from main party: {} to target party: {}", p2pCopyRequest.getMainParty(), p2PCopyResponse);
    
    return p2PCopyResponse;
} else if (p2pCopyRequest.getTargetParties() != null && !p2pCopyRequest.getTargetParties().isEmpty()) {
    // No validation, just set copy status to "VALIDATION_SUCCESS"
    P2PCopyResponse p2PCopyResponse = new P2PCopyResponse();
    p2PCopyResponse.setCopyStatus("VALIDATION_SUCCESS");

    // Map targetPartyId and action to the response object, retaining OVERWRITE actions as-is
    List<P2PCopyValidationStatus> validationStatuses = p2pCopyRequest.getTargetParties().stream()
        .map(targetParty -> {
            P2PCopyValidationStatus status = new P2PCopyValidationStatus();
            status.setTargetPartyId(targetParty.getTargetPartyId());
            
            // Retain OVERWRITE actions as they are
            if (P2PCopyAction.OVERWRITE.equals(targetParty.getAction())) {
                status.setStatus(P2PCopyAction.OVERWRITE.name());
            } else {
                status.setStatus(targetParty.getAction() != null ? targetParty.getAction().name() : "NO_ACTION");
            }
            
            return status;
        })
        .collect(Collectors.toList());

    p2PCopyResponse.setValidationStatus(validationStatuses);
    
    return p2PCopyResponse;
} else {
    // Handle the case where there are no target parties to validate
    P2PCopyResponse p2PCopyResponse = new P2PCopyResponse();
    p2PCopyResponse.setCopyStatus("VALIDATION_SUCCESS");
    
    return p2PCopyResponse;
}

--------------------------------------
if (!targetPartyIds.isEmpty()) {
    // Logic when targetPartyIds is not empty
    var targetParties = fetchTargetParty(targetPartyIds);
    var validationStatus = evaluateValidationStatus(p2pCopyRequest, targetParties);
    P2PCopyResponse p2PCopyResponse = new P2PCopyResponse();
    
    p2PCopyResponse.setCopyStatus(
        validationStatus.stream()
            .anyMatch(status -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(status.getStatus()))
            ? "VALIDATION_FAILURE"
            : "VALIDATION_SUCCESS"
    );
    
    p2PCopyResponse.setValidationStatus(validationStatus);
    log.info("Validation response of Copy P2P relationship from main party: {} to target party: {}", p2pCopyRequest.getMainParty(), p2PCopyResponse);
    
    return p2PCopyResponse;
} else {
    // No validation, just set copy status to "VALIDATION_SUCCESS"
    P2PCopyResponse p2PCopyResponse = new P2PCopyResponse();
    p2PCopyResponse.setCopyStatus("VALIDATION_SUCCESS");

    // Map targetPartyId and action to the response object
    List<P2PCopyValidationStatus> validationStatuses = p2pCopyRequest.getTargetParties().stream()
        .map(targetParty -> {
            P2PCopyValidationStatus status = new P2PCopyValidationStatus();
            status.setTargetPartyId(targetParty.getTargetPartyId());
            status.setStatus(targetParty.getAction() != null ? targetParty.getAction().name() : "NO_ACTION");
            return status;
        })
        .collect(Collectors.toList());

    p2PCopyResponse.setValidationStatus(validationStatuses);
    
    return p2PCopyResponse;
}

-------+++++-+++++++-------
if (!targetPartyIds.isEmpty()) {
    // Logic when targetPartyIds is not empty
    var targetParties = fetchTargetParty(targetPartyIds);
    var validationStatus = evaluateValidationStatus(p2pCopyRequest, targetParties);
    P2PCopyResponse p2PCopyResponse = new P2PCopyResponse();
    
    p2PCopyResponse.setCopyStatus(
        validationStatus.stream()
            .anyMatch(status -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(status.getStatus()))
            ? "VALIDATION_FAILURE"
            : "VALIDATION_SUCCESS"
    );
    
    p2PCopyResponse.setValidationStatus(validationStatus);
    log.info("Validation response of Copy P2P relationship from main party: {} to target party: {}", p2pCopyRequest.getMainParty(), p2PCopyResponse);
    
    return p2PCopyResponse;
} else {
    return Optional.ofNullable(fetchTargetParty(targetPartyIds))
        .filter(parties -> !parties.isEmpty())
        .map(parties -> {
            var validationStatus = evaluateValidationStatus(p2pCopyRequest, parties);
            P2PCopyResponse response = new P2PCopyResponse();
            response.setCopyStatus(
                validationStatus.stream()
                    .anyMatch(status -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(status.getStatus()))
                    ? "VALIDATION_FAILURE"
                    : "VALIDATION_SUCCESS"
            );
            response.setValidationStatus(validationStatus);
            return response;
        })
        .orElseGet(() -> {
            P2PCopyResponse response = new P2PCopyResponse();
            response.setCopyStatus("VALIDATION_SUCCESS");
            return response;
        });
}


List<String> targetPartyIds = p2pCopyRequest.getTargetParties().stream()
    .filter(targetParty -> targetParty.getAction() == null || targetParty.getAction() == P2PCopyAction.READY_TO_COPY)
    .map(P2PCopyTargetParty::getTargetPartyId)
    .collect(Collectors.toList());



var targetPartyIds = p2pCopyRequest.getTargetParties().stream()
    .filter(targetParty -> "".equals(targetParty.getAction()))
    .map(P2PCopyTargetParty::getTargetPartyId)
    .collect(Collectors.toCollection(ArrayList::new));


private boolean findFailedRelationships(TargetParty targetParty, String sourcePartyId, List<String> sourcePartyRelationshipTypeIds, List<P2PCopyRelationship> failedRelationships) {
    boolean hasDuplicate = false;

    for (var relatedParty : targetParty.getTargetPartyRelatedParties()) {
        if (relatedParty.getTargetPartyRelatedPartyId().equalsIgnoreCase(sourcePartyId)) {
            List<String> matchedSourceRelationshipTypeIds = sourcePartyRelationshipTypeIds.stream()
                .filter(id -> id.equals(relatedParty.getRelatedPartyRelationshipTypeId()))
                .collect(Collectors.toList());

            if (!matchedSourceRelationshipTypeIds.isEmpty()) {
                hasDuplicate = true;
                failedRelationships.add(
                    new P2PCopyRelationship(sourcePartyId, matchedSourceRelationshipTypeIds)
                );
            }
        }
    }

    return hasDuplicate;
}



public List<P2PCopyValidationStatus> evaluateCopyRequest(List<P2PCopyRequest> p2pCopyRequests, List<TargetParty> targetParties) {
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    for (var targetParty : targetParties) {
        var validationStatus = new P2PCopyValidationStatus();
        validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

        boolean hasDuplicate = false;
        List<P2PCopyRelationship> failedRelationships = new ArrayList<>();

        for (P2PCopyRelationship sourceRelationship : targetParty.getSourceRelationships()) {
            String sourcePartyId = sourceRelationship.getSourcePartyId();
            List<String> sourcePartyRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

            hasDuplicate |= findFailedRelationships(targetParty, sourcePartyId, sourcePartyRelationshipTypeIds, failedRelationships);
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


--------------------
public List<P2PCopyValidationStatus> evaluateCopyRequest(List<P2PCopyRequest> p2pCopyRequests, List<TargetParty> targetParties) {
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    for (var targetParty : targetParties) {
        var validationStatus = new P2PCopyValidationStatus();
        validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

        List<P2PCopyRelationship> failedRelationships = new ArrayList<>();
        boolean hasDuplicate = false;

        for (P2PCopyRelationship sourceRelationship : targetParty.getSourceRelationships()) {
            String sourcePartyId = sourceRelationship.getSourcePartyId();
            List<String> sourcePartyRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

            // Call the refactored method
            List<P2PCopyRelationship> newFailedRelationships = findFailedRelationships(targetParty, sourcePartyId, sourcePartyRelationshipTypeIds);
            if (!newFailedRelationships.isEmpty()) {
                hasDuplicate = true;
                failedRelationships.addAll(newFailedRelationships);
            }
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




public List<P2PCopyValidationStatus> evaluateCopyRequest(List<P2PCopyRequest> p2pCopyRequests, List<TargetParty> targetParties) {
    List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

    for (var targetParty : targetParties) {
        var validationStatus = new P2PCopyValidationStatus();
        validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

        List<P2PCopyRelationship> failedRelationships = new ArrayList<>();

        for (P2PCopyRelationship sourceRelationship : targetParty.getSourceRelationships()) {
            String sourcePartyId = sourceRelationship.getSourcePartyId();
            List<String> sourcePartyRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

            failedRelationships.addAll(
                findFailedRelationships(targetParty, sourcePartyId, sourcePartyRelationshipTypeIds)
            );
        }

        if (!failedRelationships.isEmpty()) {
            validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
            validationStatus.setCopyFailedRelationships(failedRelationships);
        } else {
            validationStatus.setStatus("READY_TO_COPY");
        }

        validationStatuses.add(validationStatus);
    }

    return validationStatuses;
}


private List<P2PCopyRelationship> findFailedRelationships(TargetParty targetParty, String sourcePartyId, List<String> sourcePartyRelationshipTypeIds) {
    List<P2PCopyRelationship> failedRelationships = new ArrayList<>();

    for (var relatedParty : targetParty.getTargetPartyRelatedParties()) {
        boolean sourcePartyIdMatches = relatedParty.getTargetPartyRelatedPartyId().equalsIgnoreCase(sourcePartyId);
        List<String> matchedSourceRelationshipTypeIds = sourcePartyRelationshipTypeIds.stream()
            .filter(id -> id.equals(relatedParty.getRelatedPartyRelationshipTypeId()))
            .collect(Collectors.toList());

        if (sourcePartyIdMatches && !matchedSourceRelationshipTypeIds.isEmpty()) {
            P2PCopyRelationship failedRelationship = new P2PCopyRelationship();
            failedRelationship.setSourcePartyId(sourcePartyId);
            failedRelationship.setRelationshipTypeIds(matchedSourceRelationshipTypeIds);
            failedRelationships.add(failedRelationship);
        }
    }

    return failedRelationships;
}

----------------------------------
public TargetParty mapToTargetParty(String targetPartyId) {
    // Fetch the party data using the common method
    List<Party> parties = fetchPartyData(Collections.singletonList(targetPartyId));

    // Assuming the first element is the party we need
    Party party = parties.get(0);

    // Map the data to TargetParty using the builder pattern
    TargetParty targetParty = TargetParty.builder()
        .targetPartyId(party.getPartyID())
        .targetPartyRelatedParties(
            party.getRelatedPartyList().stream()
                .map(relatedParty -> TargetPartyRelatedParties.builder()
                    .targetPartyRelatedPartyId(relatedParty.getRole1Party().getPartyID())
                    .relatedPartyRelationshipTypeId(relatedParty.getPartyRelationshipType().getID())
                    .relatedPartyRelationshipTypeName(relatedParty.getPartyRelationshipType().getName())
                    .build())
                .collect(Collectors.toList())
        )
        .build();

    return targetParty;
}


----------------
public class CodaPartyDetails {
    private String partyId;
    private String partyName;
    private List<String> relationshipTypeIds;
    private String validationStatus;
    // Add other relevant attributes based on what `codaQueryClient.getPartyAttributes` returns

    // Getters and Setters
}
-------------------------
public List<P2PCopyResponse> validateCopyResponse(List<P2PCopyRequest> p2pCopyRequests) {
    List<P2PCopyResponse> responseList = new ArrayList<>();

    for (P2PCopyRequest request : p2pCopyRequests) {
        List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

        for (P2PCopyTargetParty targetParty : request.getTargetParties()) {
            // Validate only if the action is "READY_TO_COPY"
            if ("READY_TO_COPY".equalsIgnoreCase(targetParty.getAction())) {
                P2PCopyValidationStatus validationStatus = new P2PCopyValidationStatus();
                validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

                List<P2PCopyRelationship> failedRelationships = new ArrayList<>();

                // Fetch the related party details based on the target party ID
                var partyDetails = codaQueryClient.getPartyWithAttributesPOST(
                    targetParty.getTargetPartyId(),
                    Stream.of(COPY_RELATIONSHIP_DOM_ATTRIBUTES).flatMap(Collection::stream).collect(Collectors.toList())
                );

                boolean hasDuplicate = false;

                // Traverse the sourceRelationships list in the request
                for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                    String sourcePartyId = sourceRelationship.getSourcePartyId();
                    List<String> requestRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

                    // Traverse the relatedPartyList to check for matching role1Party.partyID
                    for (PartyToPartyRelationship relatedParty : partyDetails.getRelatedPartyList()) {
                        boolean partyIdMatches = relatedParty.getRole1Party().getPartyID().equals(sourcePartyId);

                        // Assuming partyRelationshipType in relatedParty is an object and we compare its ID with request's relationshipTypeIds
                        List<String> matchedRelationshipTypeIds = requestRelationshipTypeIds.stream()
                            .filter(id -> id.equals(relatedParty.getPartyRelationshipType().getID()))
                            .collect(Collectors.toList());

                        if (partyIdMatches && !matchedRelationshipTypeIds.isEmpty()) {
                            hasDuplicate = true;

                            // Add to failedRelationships
                            P2PCopyRelationship failedRelationship = new P2PCopyRelationship();
                            failedRelationship.setSourcePartyId(sourcePartyId);
                            failedRelationship.setRelationshipTypeIds(matchedRelationshipTypeIds);
                            failedRelationships.add(failedRelationship);
                        }
                    }
                }

                if (hasDuplicate) {
                    validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
                    validationStatus.setCopyFailedRelationships(failedRelationships);
                } else {
                    validationStatus.setStatus("READY_TO_COPY");
                }

                validationStatuses.add(validationStatus);
            }
        }

        P2PCopyResponse response = new P2PCopyResponse();
        response.setCopyStatus(validationStatuses.stream().anyMatch(vs -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(vs.getStatus()))
            ? "VALIDATION_FAILURE"
            : "VALIDATION_SUCCESS");
        response.setValidationStatus(validationStatuses);

        responseList.add(response);
    }

    return responseList;
}

---------------------------------------
public List<P2PCopyResponse> validateCopyResponse(List<P2PCopyRequest> p2pCopyRequests) {
    List<P2PCopyResponse> responseList = new ArrayList<>();

    for (P2PCopyRequest request : p2pCopyRequests) {
        List<P2PCopyValidationStatus> validationStatuses = new ArrayList<>();

        for (P2PCopyTargetParty targetParty : request.getTargetParties()) {
            P2PCopyValidationStatus validationStatus = new P2PCopyValidationStatus();
            validationStatus.setTargetPartyId(targetParty.getTargetPartyId());

            List<P2PCopyRelationship> failedRelationships = new ArrayList<>();

            // Fetch the related party details based on the target party ID
            var partyDetails = codaQueryClient.getPartyWithAttributesPOST(
                targetParty.getTargetPartyId(),
                Stream.of(COPY_RELATIONSHIP_DOM_ATTRIBUTES).flatMap(Collection::stream).collect(Collectors.toList())
            );

            boolean hasDuplicate = false;

            // Traverse the sourceRelationships list in the request
            for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                String sourcePartyId = sourceRelationship.getSourcePartyId();
                List<String> requestRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

                // Traverse the relatedPartyList to check for matching role1Party.partyID
                for (PartyToPartyRelationship relatedParty : partyDetails.getRelatedPartyList()) {
                    boolean partyIdMatches = relatedParty.getRole1Party().getPartyID().equals(sourcePartyId);

                    // Assuming partyRelationshipType in relatedParty is an object and we compare its ID with request's relationshipTypeIds
                    List<String> matchedRelationshipTypeIds = requestRelationshipTypeIds.stream()
                        .filter(id -> id.equals(relatedParty.getPartyRelationshipType().getID()))
                        .collect(Collectors.toList());

                    if (partyIdMatches && !matchedRelationshipTypeIds.isEmpty()) {
                        hasDuplicate = true;

                        // Add to failedRelationships
                        P2PCopyRelationship failedRelationship = new P2PCopyRelationship();
                        failedRelationship.setSourcePartyId(sourcePartyId);
                        failedRelationship.setRelationshipTypeIds(matchedRelationshipTypeIds);
                        failedRelationships.add(failedRelationship);
                    }
                }
            }

            if (hasDuplicate) {
                validationStatus.setStatus("DUPLICATE_RELATIONSHIP_EXISTS");
                validationStatus.setCopyFailedRelationships(failedRelationships);
            } else {
                validationStatus.setStatus("READY_TO_COPY");
            }

            validationStatuses.add(validationStatus);
        }

        P2PCopyResponse response = new P2PCopyResponse();
        response.setCopyStatus(validationStatuses.stream().anyMatch(vs -> "DUPLICATE_RELATIONSHIP_EXISTS".equals(vs.getStatus()))
            ? "VALIDATION_FAILURE"
            : "VALIDATION_SUCCESS");
        response.setValidationStatus(validationStatuses);

        responseList.add(response);
    }

    return responseList;
}

-----------------------------
import java.util.stream.Collectors;

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

            // Traverse the sourceRelationships list in the request
            for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                String sourcePartyId = sourceRelationship.getSourcePartyId();
                List<String> requestRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

                // Traverse the relatedPartyList to check for matching role1Party.partyID
                for (PartyToPartyRelationship relatedParty : partyDetails.getRelatedPartyList()) {
                    boolean partyIdMatches = relatedParty.getRole1Party().getPartyID().equals(sourcePartyId);

                    // Assuming partyRelationshipType in relatedParty is an object and we compare its ID with request's relationshipTypeIds
                    boolean relationshipTypeMatches = requestRelationshipTypeIds.contains(relatedParty.getPartyRelationshipType().getID());

                    if (partyIdMatches && relationshipTypeMatches) {
                        // Perform necessary actions here when a match is found
                        System.out.println("Match found for Role1Party ID: " + relatedParty.getRole1Party().getPartyID());
                        System.out.println("Matching Relationship Type ID: " + relatedParty.getPartyRelationshipType().getID());
                        // You can log this, add to a response list, etc.
                    }
                }
            }
        }
    }

    // Return the list of P2PCopyResponse or any other required data
    return null; // Adjust as necessary
}

------------------------------------
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

            // Traverse the sourceRelationships list in the request
            for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                String sourcePartyId = sourceRelationship.getSourcePartyId();
                List<String> requestRelationshipTypeIds = sourceRelationship.getRelationshipTypeIds();

                // Traverse the relatedPartyList to check for matching role1Party.partyID
                for (Party relatedParty : partyDetails.getRelatedPartyList()) {
                    boolean partyIdMatches = relatedParty.getRole1Party().getPartyID().equals(sourcePartyId);

                    // Check if any of the relationshipTypeIds from the request match those in the related party
                    boolean relationshipTypeMatches = relatedParty.getPartyRelationshipType().stream()
                        .anyMatch(requestRelationshipTypeIds::contains);

                    if (partyIdMatches && relationshipTypeMatches) {
                        // Perform necessary actions here when a match is found
                        System.out.println("Match found for Role1Party ID: " + relatedParty.getRole1Party().getPartyID());
                        System.out.println("Matching Relationship Type IDs: " + requestRelationshipTypeIds);
                        // You can log this, add to a response list, etc.
                    }
                }
            }
        }
    }

    // Return the list of P2PCopyResponse or any other required data
    return null; // Adjust as necessary
}

--------------------------------------------
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

            // Traverse the sourceRelationships list in the request
            for (P2PCopyRelationship sourceRelationship : request.getSourceRelationships()) {
                String sourcePartyId = sourceRelationship.getSourcePartyId();

                // Traverse the relatedPartyList to check for matching role1Party.partyID
                for (Party relatedParty : partyDetails.getRelatedPartyList()) {
                    if (relatedParty.getRole1Party().getPartyID().equals(sourcePartyId)) {
                        // Perform necessary actions here when a match is found
                        System.out.println("Match found for Role1Party ID: " + relatedParty.getRole1Party().getPartyID());
                        // You can log this, add to a response list, etc.
                    }
                }
            }
        }
    }

    // Return the list of P2PCopyResponse or any other required data
    return null; // Adjust as necessary
}

-------------------------
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
