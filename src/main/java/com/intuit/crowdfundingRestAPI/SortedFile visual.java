public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    return p2pVisualizationParties.stream()
        .flatMap(party -> Optional.ofNullable(party.getNonOwnershipRelationships())
            .filter(nonOwnerships -> !nonOwnerships.isEmpty())
            .map(nonOwnerships -> nonOwnerships.stream()
                .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .map(relationshipDetail -> {
                        // Build a new P2PRelationship with only this single relationship detail
                        P2PRelationship splitNonOwnership = P2PRelationship.builder()
                            .parentPartyId(nonOwnership.getParentPartyId())
                            .effectivePercentageValueOfOwnership(nonOwnership.getEffectivePercentageValueOfOwnership())
                            .relationshipDetails(Collections.singletonList(relationshipDetail))
                            .build();

                        // Build a new P2PVisualization instance with updated parentId and split non-ownership relationship
                        return P2PVisualization.builder()
                            .partyId(party.getPartyId())
                            .parentId(nonOwnership.getParentPartyId()) // Set parentId to parentPartyId of nonOwnership
                            .partyName(party.getPartyName())
                            .validationStatus(party.getValidationStatus())
                            .countryOfOrganization(party.getCountryOfOrganization())
                            .legalForm(party.getLegalForm())
                            .legalName(party.getLegalName())
                            .pepIndicator(party.getPepIndicator())
                            .ownershipRelationships(party.getOwnershipRelationships()) // Retain original ownership relationships
                            .nonOwnershipRelationships(Collections.singletonList(splitNonOwnership)) // Only this non-ownership relationship
                            .build();
                    })))
            .orElse(Stream.of(party))) // If no nonOwnershipRelationships, keep the original party
        .collect(Collectors.toList());
}




public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    // Step 1: Separate each nonOwnershipRelationship into its own P2PVisualization instance
    List<P2PVisualization> separatedList = p2pVisualizationParties.stream()
        .flatMap(party -> Optional.ofNullable(party.getNonOwnershipRelationships())
            .filter(nonOwnerships -> !nonOwnerships.isEmpty())
            .map(nonOwnerships -> nonOwnerships.stream()
                .map(nonOwnership -> {
                    // Sort the relationshipDetails within each nonOwnership relationship by relationshipTypeId
                    List<RelationshipDetail> sortedRelationshipDetails = Optional.ofNullable(nonOwnership.getRelationshipDetails())
                        .orElse(Collections.emptyList())
                        .stream()
                        .sorted(Comparator.comparing(RelationshipDetail::getRelationshipTypeId))
                        .collect(Collectors.toList());

                    // Build a new P2PRelationship with sorted relationship details
                    P2PRelationship sortedNonOwnership = P2PRelationship.builder()
                        .parentPartyId(nonOwnership.getParentPartyId())
                        .effectivePercentageValueOfOwnership(nonOwnership.getEffectivePercentageValueOfOwnership())
                        .relationshipDetails(sortedRelationshipDetails)
                        .build();

                    // Build a new P2PVisualization instance with updated parentId and sorted non-ownership relationship
                    return P2PVisualization.builder()
                        .partyId(party.getPartyId())
                        .parentId(nonOwnership.getParentPartyId()) // Set parentId to parentPartyId of nonOwnership
                        .partyName(party.getPartyName())
                        .validationStatus(party.getValidationStatus())
                        .countryOfOrganization(party.getCountryOfOrganization())
                        .legalForm(party.getLegalForm())
                        .legalName(party.getLegalName())
                        .pepIndicator(party.getPepIndicator())
                        .ownershipRelationships(party.getOwnershipRelationships()) // Retain original ownership relationships
                        .nonOwnershipRelationships(Collections.singletonList(sortedNonOwnership)) // Only this non-ownership relationship
                        .build();
                }))
            .orElse(Stream.of(party)) // If no nonOwnershipRelationships, keep the original party
        )
        .collect(Collectors.toList());

    // Step 2: Partition and sort - non-ownership relationships come at the end, sorted by relationshipTypeId
    return Stream.concat(
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()), // Without non-ownership
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) // With non-ownership
                .sorted(Comparator.comparing(party -> party.getNonOwnershipRelationships().get(0)
                    .getRelationshipDetails().get(0).getRelationshipTypeId())) // Sort by relationshipTypeId
        )
        .collect(Collectors.toList());
}







public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    List<P2PVisualization> separatedList = new ArrayList<>();

    // Step 1: Iterate over each P2PVisualization and separate nonOwnershipRelationships
    for (P2PVisualization party : p2pVisualizationParties) {
        if (party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) {
            for (P2PRelationship nonOwnership : party.getNonOwnershipRelationships()) {
                // Sort the relationshipDetails within each nonOwnership relationship by relationshipTypeId
                List<RelationshipDetail> sortedRelationshipDetails = nonOwnership.getRelationshipDetails().stream()
                    .sorted(Comparator.comparing(RelationshipDetail::getRelationshipTypeId))
                    .collect(Collectors.toList());

                // Build a new P2PRelationship with sorted relationship details
                P2PRelationship sortedNonOwnership = P2PRelationship.builder()
                    .parentPartyId(nonOwnership.getParentPartyId())
                    .effectivePercentageValueOfOwnership(nonOwnership.getEffectivePercentageValueOfOwnership())
                    .relationshipDetails(sortedRelationshipDetails)
                    .build();

                // Build a new P2PVisualization instance with the updated parentId and sorted non-ownership relationship
                P2PVisualization separatedParty = P2PVisualization.builder()
                    .partyId(party.getPartyId())
                    .parentId(nonOwnership.getParentPartyId()) // Set parentId to parentPartyId of nonOwnership
                    .partyName(party.getPartyName())
                    .validationStatus(party.getValidationStatus())
                    .countryOfOrganization(party.getCountryOfOrganization())
                    .legalForm(party.getLegalForm())
                    .legalName(party.getLegalName())
                    .pepIndicator(party.getPepIndicator())
                    .ownershipRelationships(party.getOwnershipRelationships()) // Retain original ownership relationships
                    .nonOwnershipRelationships(Collections.singletonList(sortedNonOwnership)) // Only this non-ownership relationship
                    .build();

                separatedList.add(separatedParty);
            }
        } else {
            // Add the original P2PVisualization for parties without nonOwnershipRelationships
            separatedList.add(party);
        }
    }

    // Step 2: Partition and sort - non-ownership relationships come at the end, sorted by relationshipTypeId
    List<P2PVisualization> withoutNonOwnership = separatedList.stream()
        .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());

    List<P2PVisualization> withNonOwnership = separatedList.stream()
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty())
        .sorted(Comparator.comparing(party -> party.getNonOwnershipRelationships().get(0).getRelationshipDetails().get(0).getRelationshipTypeId()))
        .collect(Collectors.toList());

    // Concatenate lists - without non-ownership first, followed by sorted non-ownership list
    withoutNonOwnership.addAll(withNonOwnership);

    return withoutNonOwnership;
}






public List<P2PVisualization> sortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    // Step 1: Partition the list into those with and without nonOwnershipRelationships
    List<P2PVisualization> withNonOwnership = p2pVisualizationParties.stream()
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty())
        .map(party -> {
            // Sort non-ownership relationships by relationshipTypeId within each nonOwnership relationship
            List<P2PRelationship> sortedNonOwnerships = party.getNonOwnershipRelationships().stream()
                .map(nonOwnership -> {
                    List<RelationshipDetail> sortedRelationshipDetails = nonOwnership.getRelationshipDetails().stream()
                        .sorted(Comparator.comparing(RelationshipDetail::getRelationshipTypeId))
                        .collect(Collectors.toList());

                    // Build a new P2PRelationship with sorted relationship details
                    return P2PRelationship.builder()
                        .parentPartyId(nonOwnership.getParentPartyId())
                        .effectivePercentageValueOfOwnership(nonOwnership.getEffectivePercentageValueOfOwnership())
                        .relationshipDetails(sortedRelationshipDetails)
                        .build();
                })
                .collect(Collectors.toList());

            // Build a new P2PVisualization instance with sorted non-ownership relationships
            return P2PVisualization.builder()
                .partyId(party.getPartyId())
                .parentId(party.getParentId())
                .partyName(party.getPartyName())
                .validationStatus(party.getValidationStatus())
                .countryOfOrganization(party.getCountryOfOrganization())
                .legalForm(party.getLegalForm())
                .legalName(party.getLegalName())
                .pepIndicator(party.getPepIndicator())
                .ownershipRelationships(party.getOwnershipRelationships()) // Retain ownership relationships
                .nonOwnershipRelationships(sortedNonOwnerships) // Set sorted non-ownership relationships
                .build();
        })
        .collect(Collectors.toList());

    // Step 2: Collect parties without non-ownership relationships (these will come first)
    List<P2PVisualization> withoutNonOwnership = p2pVisualizationParties.stream()
        .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());

    // Step 3: Concatenate lists - parties without non-ownership relationships first, followed by those with sorted non-ownership relationships
    withoutNonOwnership.addAll(withNonOwnership);

    return withoutNonOwnership;
}




-----------------------------
public List<P2PVisualization> filterAndSortNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
    return p2pVisualizationParties.stream()
        // Map each party to a new P2PVisualization instance with sorted non-ownership relationships
        .map(party -> {
            // Sort non-ownership relationships by relationshipTypeId within each nonOwnership relationship
            List<P2PRelationship> sortedNonOwnerships = party.getNonOwnershipRelationships().stream()
                .map(nonOwnership -> {
                    List<RelationshipDetail> sortedRelationshipDetails = nonOwnership.getRelationshipDetails().stream()
                        .sorted(Comparator.comparing(RelationshipDetail::getRelationshipTypeId))
                        .collect(Collectors.toList());

                    // Build a new P2PRelationship with sorted relationship details
                    return P2PRelationship.builder()
                        .parentPartyId(nonOwnership.getParentPartyId())
                        .effectivePercentageValueOfOwnership(nonOwnership.getEffectivePercentageValueOfOwnership())
                        .relationshipDetails(sortedRelationshipDetails)
                        .build();
                })
                .collect(Collectors.toList());

            // Build a new P2PVisualization instance, keeping both ownership and sorted non-ownership relationships
            return P2PVisualization.builder()
                .partyId(party.getPartyId())
                .parentId(party.getParentId())
                .partyName(party.getPartyName())
                .validationStatus(party.getValidationStatus())
                .countryOfOrganization(party.getCountryOfOrganization())
                .legalForm(party.getLegalForm())
                .legalName(party.getLegalName())
                .pepIndicator(party.getPepIndicator())
                .ownershipRelationships(party.getOwnershipRelationships()) // Retain original ownership relationships
                .nonOwnershipRelationships(sortedNonOwnerships) // Set the sorted non-ownership relationships
                .build();
        })
        .collect(Collectors.toList());
}





---------------------------
public List<P2PVisualization> filterAndSortNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
    return p2pVisualizationParties.stream()
        // Step 1: Filter parties that have only non-ownership relationships and no ownership relationships
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()
            && (party.getOwnershipRelationships() == null || party.getOwnershipRelationships().isEmpty()))
        // Step 2: Map each party to a new P2PVisualization instance with sorted non-ownership relationships
        .map(party -> {
            List<P2PRelationship> sortedNonOwnerships = party.getNonOwnershipRelationships().stream()
                .map(nonOwnership -> {
                    // Sort relationship details by relationshipTypeId within each nonOwnership relationship
                    List<RelationshipDetail> sortedRelationshipDetails = nonOwnership.getRelationshipDetails().stream()
                        .sorted(Comparator.comparing(RelationshipDetail::getRelationshipTypeId))
                        .collect(Collectors.toList());

                    // Build a new P2PRelationship with sorted relationship details
                    return P2PRelationship.builder()
                        .parentPartyId(nonOwnership.getParentPartyId())
                        .effectivePercentageValueOfOwnership(nonOwnership.getEffectivePercentageValueOfOwnership())
                        .relationshipDetails(sortedRelationshipDetails)
                        .build();
                })
                .collect(Collectors.toList());

            // Build a new P2PVisualization instance with the sorted non-ownership relationships
            return P2PVisualization.builder()
                .partyId(party.getPartyId())
                .parentId(party.getParentId())
                .partyName(party.getPartyName())
                .validationStatus(party.getValidationStatus())
                .countryOfOrganization(party.getCountryOfOrganization())
                .legalForm(party.getLegalForm())
                .legalName(party.getLegalName())
                .pepIndicator(party.getPepIndicator())
                .ownershipRelationships(party.getOwnershipRelationships())
                .nonOwnershipRelationships(sortedNonOwnerships) // Set the sorted non-ownership relationships
                .build();
        })
        .collect(Collectors.toList());
}




public List<P2PVisualization> filterNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
    return p2pVisualizationParties.stream()
        // Filter out parties that only have non-ownership relationships
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()
            && (party.getOwnershipRelationships() == null || party.getOwnershipRelationships().isEmpty()))
        // Further filter non-ownership relationships by relationshipTypeId dynamically fetched from the response
        .map(party -> {
            List<P2PRelationship> filteredNonOwnerships = party.getNonOwnershipRelationships().stream()
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails() != null)
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .anyMatch(detail -> shouldIncludeBasedOnTypeId(detail.getRelationshipTypeId())))
                .collect(Collectors.toList());

            // Build a new P2PVisualization instance with the filtered non-ownership relationships
            return P2PVisualization.builder()
                .partyId(party.getPartyId())
                .parentId(party.getParentId())
                .partyName(party.getPartyName())
                .validationStatus(party.getValidationStatus())
                .countryOfOrganization(party.getCountryOfOrganization())
                .legalForm(party.getLegalForm())
                .legalName(party.getLegalName())
                .pepIndicator(party.getPepIndicator())
                .ownershipRelationships(party.getOwnershipRelationships())
                .nonOwnershipRelationships(filteredNonOwnerships) // Set the filtered non-ownership relationships
                .build();
        })
        // Ensure we return only parties with at least one matching relationship in nonOwnershipRelationships
        .filter(party -> !party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());
}

// Helper method to decide whether to include based on relationshipTypeId
private boolean shouldIncludeBasedOnTypeId(String relationshipTypeId) {
    // Implement your logic here based on the relationshipTypeId
    // For example, if you want to include only specific type IDs, use a condition:
    return relationshipTypeId != null && (relationshipTypeId.equals("YOUR_DESIRED_TYPE_ID") || relationshipTypeId.equals("ANOTHER_TYPE_ID"));
}







public List<P2PVisualization> filterNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties, String requiredRelationshipTypeId) {
    return p2pVisualizationParties.stream()
        // Filter out parties that only have non-ownership relationships
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()
            && (party.getOwnershipRelationships() == null || party.getOwnershipRelationships().isEmpty()))
        // Further filter non-ownership relationships by relationshipTypeId
        .map(party -> {
            List<P2PRelationship> filteredNonOwnerships = party.getNonOwnershipRelationships().stream()
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails() != null)
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .anyMatch(detail -> requiredRelationshipTypeId.equals(detail.getRelationshipTypeId())))
                .collect(Collectors.toList());

            // Build a new P2PVisualization instance with the filtered non-ownership relationships
            return P2PVisualization.builder()
                .partyId(party.getPartyId())
                .parentId(party.getParentId())
                .partyName(party.getPartyName())
                .validationStatus(party.getValidationStatus())
                .countryOfOrganization(party.getCountryOfOrganization())
                .legalForm(party.getLegalForm())
                .legalName(party.getLegalName())
                .pepIndicator(party.getPepIndicator())
                .ownershipRelationships(party.getOwnershipRelationships())
                .nonOwnershipRelationships(filteredNonOwnerships) // Set the filtered non-ownership relationships
                .build();
        })
        // Ensure we return only parties with at least one matching relationship in nonOwnershipRelationships
        .filter(party -> !party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());
}




public List<P2PVisualization> filterNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties, String requiredRelationshipTypeId) {
    return p2pVisualizationParties.stream()
        // Filter out parties that only have non-ownership relationships
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()
            && (party.getOwnershipRelationships() == null || party.getOwnershipRelationships().isEmpty()))
        // Further filter non-ownership relationships by relationshipTypeId
        .map(party -> {
            List<P2PRelationship> filteredNonOwnerships = party.getNonOwnershipRelationships().stream()
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails() != null)
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .anyMatch(detail -> requiredRelationshipTypeId.equals(detail.getRelationshipTypeId())))
                .collect(Collectors.toList());

            // Set the filtered non-ownership relationships back to the party
            party.setNonOwnershipRelationships(filteredNonOwnerships);
            return party;
        })
        // Ensure we return only parties with at least one matching relationship in nonOwnershipRelationships
        .filter(party -> !party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());
}


-------------------

import java.util.List;
import java.util.stream.Collectors;

public List<P2PVisualization> filterNonOwnershipPartiesByRelationshipTypeId(List<P2PVisualization> p2pVisualizationParties, String relationshipTypeId) {
    return p2pVisualizationParties.stream()
        // Filter out parties with nonOwnershipRelationships only
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty())
        .map(party -> {
            // Filter nonOwnershipRelationships based on relationshipTypeId
            List<P2PRelationship> filteredNonOwnershipRelationships = party.getNonOwnershipRelationships().stream()
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .anyMatch(detail -> relationshipTypeId.equals(detail.getRelationshipTypeId())))
                .collect(Collectors.toList());
            
            // Create a new P2PVisualization object with filtered nonOwnershipRelationships
            P2PVisualization filteredParty = new P2PVisualization(party);
            filteredParty.setNonOwnershipRelationships(filteredNonOwnershipRelationships);
            return filteredParty;
        })
        // Ensure only parties with the specified relationshipTypeId remain
        .filter(party -> !party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());
}
