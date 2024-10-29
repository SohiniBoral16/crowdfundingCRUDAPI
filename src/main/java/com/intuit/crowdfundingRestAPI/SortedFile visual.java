
return Stream.concat(
        // First, add entries with ownership relationships or no non-ownership relationships
        p2pVisualizationParties.stream()
            .filter(party -> Optional.ofNullable(party.getNonOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty()
                || !Optional.ofNullable(party.getOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty()),

        // Then, add split entries at the end, sorted by the first relationshipTypeId in non-ownership relationships
        splitList.stream()
            .sorted(Comparator.comparing(
                party -> party.getNonOwnershipRelationships().stream()
                    .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream())
                    .map(RelationshipDetail::getRelationshipTypeId)
                    .sorted() // Sort by relationshipTypeId directly and access the first element
                    .iterator().next() // Access the first element directly, assuming there's always data
            ))
    )
    .collect(Collectors.toList());



import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class P2PVisualizationProcessor {

    public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
        // Step 1: Filter entries that only have non-ownership relationships
        List<P2PVisualization> nonOwnershipOnlyList = p2pVisualizationParties.stream()
            .filter(party -> Optional.ofNullable(party.getOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty())
            .collect(Collectors.toList());

        // Step 2: Split entries based on different parentPartyIds in nonOwnershipRelationships
        List<P2PVisualization> splitList = nonOwnershipOnlyList.stream()
            .flatMap(party -> {
                // Group non-ownership relationships by parentPartyId
                Map<String, List<P2PRelationship>> groupedRelationships = party.getNonOwnershipRelationships().stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId));

                // Create a new P2PVisualization entry for each unique parentPartyId
                return groupedRelationships.values().stream().map(relationships -> {
                    List<RelationshipDetail> sortedDetails = relationships.stream()
                        .flatMap(rel -> rel.getRelationshipDetails().stream())
                        .sorted(Comparator.comparing(RelationshipDetail::getRelationshipTypeId))
                        .collect(Collectors.toList());

                    // Use any relationship to get representative values for parentPartyId and effectivePercentageValueOfOwnership
                    P2PRelationship representativeRelationship = relationships.stream().findFirst().orElse(null);

                    // Build a new P2PVisualization entry
                    return P2PVisualization.builder()
                        .partyId(party.getPartyId())
                        .parentId(party.getParentId())
                        .partyName(party.getPartyName())
                        .validationStatus(party.getValidationStatus())
                        .countryOfOrganization(party.getCountryOfOrganization())
                        .legalForm(party.getLegalForm())
                        .legalName(party.getLegalName())
                        .pepIndicator(party.getPepIndicator())
                        .ownershipRelationships(party.getOwnershipRelationships()) // Empty ownership relationships
                        .nonOwnershipRelationships(Collections.singletonList(
                            P2PRelationship.builder()
                                .parentPartyId(representativeRelationship.getParentPartyId())
                                .effectivePercentageValueOfOwnership(representativeRelationship.getEffectivePercentageValueOfOwnership())
                                .relationshipDetails(sortedDetails)
                                .build()
                        ))
                        .build();
                });
            })
            .collect(Collectors.toList());

        // Step 3: Sort and combine results
        return Stream.concat(
                // First, add entries with ownership relationships or no non-ownership relationships
                p2pVisualizationParties.stream()
                    .filter(party -> Optional.ofNullable(party.getNonOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty()
                        || !Optional.ofNullable(party.getOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty()),
                
                // Then, add split entries at the end, sorted by relationshipTypeId of the first relationshipDetail
                splitList.stream()
                    .sorted(Comparator.comparing(
                        party -> party.getNonOwnershipRelationships().stream()
                            .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream())
                            .map(RelationshipDetail::getRelationshipTypeId)
                            .findFirst()
                            .orElse(""),
                        Comparator.naturalOrder())
                    )
            )
            .collect(Collectors.toList());
    }
}




import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class P2PVisualizationProcessor {

    public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
        // Step 1: Filter entries with only non-ownership relationships
        List<P2PVisualization> nonOwnershipOnlyList = p2pVisualizationParties.stream()
            .filter(party -> Optional.ofNullable(party.getOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty())
            .collect(Collectors.toList());

        // Step 2: Split entries based on different parentPartyIds in nonOwnershipRelationships
        List<P2PVisualization> splitList = nonOwnershipOnlyList.stream()
            .flatMap(party -> {
                Map<String, List<P2PRelationship>> groupedRelationships = party.getNonOwnershipRelationships().stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId));

                // If multiple parentPartyIds, create a separate P2PVisualization entry for each group
                return groupedRelationships.values().stream().map(relationships -> {
                    List<RelationshipDetail> sortedDetails = relationships.stream()
                        .flatMap(rel -> rel.getRelationshipDetails().stream())
                        .sorted(Comparator.comparing(RelationshipDetail::getRelationshipTypeId)) // Sort by relationshipTypeId
                        .collect(Collectors.toList());

                    // Build a new P2PRelationship with sorted relationship details
                    P2PRelationship sortedNonOwnership = P2PRelationship.builder()
                        .parentPartyId(relationships.get(0).getParentPartyId()) // Use any representative parentPartyId
                        .effectivePercentageValueOfOwnership(relationships.get(0).getEffectivePercentageValueOfOwnership())
                        .relationshipDetails(sortedDetails)
                        .build();

                    // Create a new P2PVisualization entry with the sorted non-ownership relationships
                    return P2PVisualization.builder()
                        .partyId(party.getPartyId())
                        .parentId(party.getParentId())
                        .partyName(party.getPartyName())
                        .validationStatus(party.getValidationStatus())
                        .countryOfOrganization(party.getCountryOfOrganization())
                        .legalForm(party.getLegalForm())
                        .legalName(party.getLegalName())
                        .pepIndicator(party.getPepIndicator())
                        .ownershipRelationships(party.getOwnershipRelationships()) // Empty ownership relationships
                        .nonOwnershipRelationships(Collections.singletonList(sortedNonOwnership)) // Single sorted non-ownership relationship
                        .build();
                });
            })
            .collect(Collectors.toList());

        // Step 3: Sort the split entries by relationshipTypeId of the first relationshipDetail and position at end
        return Stream.concat(
                // First, add entries that have ownership relationships or no nonOwnershipRelationships
                p2pVisualizationParties.stream()
                    .filter(party -> Optional.ofNullable(party.getOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty()
                        || Optional.ofNullable(party.getNonOwnershipRelationships()).orElse(Collections.emptyList()).isEmpty()),
                
                // Then, add splitList with sorted non-ownership entries at the end
                splitList.stream()
                    .sorted(Comparator.comparing(
                        party -> party.getNonOwnershipRelationships().get(0).getRelationshipDetails().get(0).getRelationshipTypeId(),
                        Comparator.naturalOrder())
                    )
            )
            .collect(Collectors.toList());
    }
}


-------_------------
return Stream.concat(
        // First, add parties without non-ownership relationships
        p2pVisualizations.stream()
            .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()),
        
        // Then, add parties with non-ownership relationships, sorted by custom criteria
        p2pVisualizations.stream()
            .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty())
            .sorted(Comparator.comparing(
                party -> {
                    // Check if there are multiple relationships for the same parentPartyId
                    boolean hasMultipleRelationships = party.getNonOwnershipRelationships().stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values().stream()
                        .anyMatch(relationships -> relationships.size() > 1);

                    if (hasMultipleRelationships) {
                        // Place entries with multiple relationships at the end
                        return Integer.MAX_VALUE;
                    } else {
                        // For entries with a single relationship, get relationshipTypeId directly
                        return party.getNonOwnershipRelationships().stream()
                            .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream())
                            .map(RelationshipDetail::getRelationshipTypeId)
                            .collect(Collectors.joining()); // Directly join the type IDs if there’s only one
                    }
                }
            ))
    )
    .collect(Collectors.toList());






return Stream.concat(
        // First, add parties without non-ownership relationships
        p2pVisualizations.stream()
            .filter(party -> Objects.isNull(party.getNonOwnershipRelationships()) || party.getNonOwnershipRelationships().isEmpty()),
        
        // Then, add parties with non-ownership relationships, sorted by custom criteria
        p2pVisualizations.stream()
            .filter(party -> Objects.nonNull(party.getNonOwnershipRelationships()) && !party.getNonOwnershipRelationships().isEmpty())
            .sorted(Comparator.comparing(
                party -> {
                    // Check if there are multiple relationships for the same parentPartyId
                    boolean hasMultipleRelationships = party.getNonOwnershipRelationships().stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values().stream()
                        .anyMatch(relationships -> relationships.size() > 1);

                    if (hasMultipleRelationships) {
                        // Place entries with multiple relationships at the end
                        return Integer.MAX_VALUE;
                    } else {
                        // For entries with a single relationship, get relationshipTypeId directly
                        return party.getNonOwnershipRelationships().stream()
                            .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream())
                            .map(RelationshipDetail::getRelationshipTypeId)
                            .collect(Collectors.joining()); // Directly join the type IDs if there’s only one
                    }
                },
                Comparator.nullsLast(Comparator.naturalOrder()) // Handle nulls by placing them at the end
            ))
    )
    .collect(Collectors.toList());



return Stream.concat(
        // First, add parties without non-ownership relationships
        p2pVisualizations.stream()
            .filter(party -> Objects.isNull(party.getNonOwnershipRelationships()) || party.getNonOwnershipRelationships().isEmpty()),
        
        // Then, add parties with non-ownership relationships, sorted by custom criteria
        p2pVisualizations.stream()
            .filter(party -> Objects.nonNull(party.getNonOwnershipRelationships()) && !party.getNonOwnershipRelationships().isEmpty())
            .sorted(Comparator.comparing(
                party -> {
                    // Check if there are multiple relationships for the same parentPartyId
                    boolean hasMultipleRelationships = party.getNonOwnershipRelationships().stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values().stream()
                        .anyMatch(relationships -> relationships.size() > 1);

                    if (hasMultipleRelationships) {
                        // Place entries with multiple relationships at the end
                        return Integer.MAX_VALUE;
                    } else {
                        // For entries with a single relationship, directly map relationshipTypeId without findFirst
                        return party.getNonOwnershipRelationships().stream()
                            .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream())
                            .map(RelationshipDetail::getRelationshipTypeId)
                            .findAny().orElse(""); // Use findAny to avoid ordering concerns
                    }
                },
                Comparator.nullsLast(Comparator.naturalOrder()) // Handle nulls by placing them at the end
            ))
    )
    .collect(Collectors.toList());





return Stream.concat(
        // First, add parties without non-ownership relationships
        p2pVisualizations.stream()
            .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()),
        
        // Then, add parties with non-ownership relationships, sorted by custom criteria
        p2pVisualizations.stream()
            .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty())
            .sorted(Comparator.comparing(
                party -> {
                    // Check if there are multiple relationships for the same parentPartyId
                    boolean hasMultipleRelationships = party.getNonOwnershipRelationships().stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values().stream()
                        .anyMatch(relationships -> relationships.size() > 1);

                    if (hasMultipleRelationships) {
                        // Place entries with multiple relationships at the end
                        return Integer.MAX_VALUE;
                    } else {
                        // For entries with a single relationship, sort by relationshipTypeId
                        return party.getNonOwnershipRelationships().stream()
                            .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream())
                            .map(RelationshipDetail::getRelationshipTypeId)
                            .findAny()
                            .orElse("");
                    }
                },
                Comparator.nullsLast(Comparator.naturalOrder()) // Handle nulls by placing them at the end
            ))
    )
    .collect(Collectors.toList());




public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    // Step 1: Separate entries with different parentPartyIds in nonOwnershipRelationships
    List<P2PVisualization> separatedList = p2pVisualizationParties.stream()
        .flatMap(party -> Optional.ofNullable(party.getNonOwnershipRelationships())
            .filter(nonOwnerships -> !nonOwnerships.isEmpty())
            .map(nonOwnerships -> {
                // Group relationships by parentPartyId to check if they are the same or different
                Map<String, List<P2PRelationship>> relationshipsByParentPartyId = nonOwnerships.stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId));

                // If there are multiple parentPartyIds, create separate P2PVisualization entries for each group
                if (relationshipsByParentPartyId.size() > 1) {
                    return relationshipsByParentPartyId.values().stream()
                        .map(groupedRelationships -> {
                            List<RelationshipDetail> combinedDetails = groupedRelationships.stream()
                                .flatMap(rel -> rel.getRelationshipDetails().stream())
                                .collect(Collectors.toList());

                            String parentPartyId = groupedRelationships.stream()
                                .map(P2PRelationship::getParentPartyId)
                                .findAny()
                                .orElse(null);

                            Float effectivePercentageValueOfOwnership = groupedRelationships.stream()
                                .map(P2PRelationship::getEffectivePercentageValueOfOwnership)
                                .findAny()
                                .orElse(null);

                            // Build a new P2PVisualization instance for each unique parentPartyId
                            return P2PVisualization.builder()
                                .partyId(party.getPartyId())
                                .parentId(parentPartyId) // Set parentId to the unique parentPartyId
                                .partyName(party.getPartyName())
                                .validationStatus(party.getValidationStatus())
                                .countryOfOrganization(party.getCountryOfOrganization())
                                .legalForm(party.getLegalForm())
                                .legalName(party.getLegalName())
                                .pepIndicator(party.getPepIndicator())
                                .ownershipRelationships(party.getOwnershipRelationships())
                                .nonOwnershipRelationships(Collections.singletonList(
                                    P2PRelationship.builder()
                                        .parentPartyId(parentPartyId)
                                        .effectivePercentageValueOfOwnership(effectivePercentageValueOfOwnership)
                                        .relationshipDetails(combinedDetails)
                                        .build()
                                ))
                                .build();
                        });
                } else {
                    // If only one unique parentPartyId, retain as a single P2PVisualization entry
                    List<P2PRelationship> combinedNonOwnerships = relationshipsByParentPartyId.values().stream()
                        .map(relationships -> {
                            List<RelationshipDetail> combinedDetails = relationships.stream()
                                .flatMap(rel -> rel.getRelationshipDetails().stream())
                                .collect(Collectors.toList());

                            String parentPartyId = relationships.stream()
                                .map(P2PRelationship::getParentPartyId)
                                .findAny()
                                .orElse(null);

                            Float effectivePercentageValueOfOwnership = relationships.stream()
                                .map(P2PRelationship::getEffectivePercentageValueOfOwnership)
                                .findAny()
                                .orElse(null);

                            return P2PRelationship.builder()
                                .parentPartyId(parentPartyId)
                                .effectivePercentageValueOfOwnership(effectivePercentageValueOfOwnership)
                                .relationshipDetails(combinedDetails)
                                .build();
                        })
                        .collect(Collectors.toList());

                    return Stream.of(P2PVisualization.builder()
                        .partyId(party.getPartyId())
                        .parentId(party.getParentId())
                        .partyName(party.getPartyName())
                        .validationStatus(party.getValidationStatus())
                        .countryOfOrganization(party.getCountryOfOrganization())
                        .legalForm(party.getLegalForm())
                        .legalName(party.getLegalName())
                        .pepIndicator(party.getPepIndicator())
                        .ownershipRelationships(party.getOwnershipRelationships())
                        .nonOwnershipRelationships(combinedNonOwnerships)
                        .build());
                }
            })
            .orElse(Stream.of(party))) // If no nonOwnershipRelationships, keep the original party
        .collect(Collectors.toList());

    // Step 2: Partition and sort
    return Stream.concat(
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()), // Without non-ownership relationships
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) // With non-ownership relationships
                .sorted(Comparator.comparing(party -> {
                    List<P2PRelationship> nonOwnerships = party.getNonOwnershipRelationships();
                    boolean hasMultipleRelationships = nonOwnerships.stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values().stream()
                        .anyMatch(details -> details.size() > 1);

                    if (hasMultipleRelationships) {
                        return Integer.MAX_VALUE; // Keep entries with multiple relationships at the end
                    } else {
                        // Sort by relationshipTypeId without accessing specific elements
                        return nonOwnerships.stream()
                            .flatMap(rel -> rel.getRelationshipDetails().stream())
                            .map(RelationshipDetail::getRelationshipTypeId)
                            .findAny().orElse(""); // Use findAny to avoid ordering concerns
                    }
                }, Comparator.nullsLast(Comparator.naturalOrder())))
        )
        .collect(Collectors.toList());
}






public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    // Step 1: Combine relationships with the same parentPartyId but retain groups as they are
    List<P2PVisualization> separatedList = p2pVisualizationParties.stream()
        .flatMap(party -> Optional.ofNullable(party.getNonOwnershipRelationships())
            .filter(nonOwnerships -> !nonOwnerships.isEmpty())
            .map(nonOwnerships -> {
                // Group nonOwnership relationships by parentPartyId
                List<P2PRelationship> groupedNonOwnerships = nonOwnerships.stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                    .values().stream()
                    .map(relationships -> {
                        // If multiple relationships exist for a parentPartyId, keep them grouped together
                        if (relationships.size() > 1) {
                            List<RelationshipDetail> combinedDetails = relationships.stream()
                                .flatMap(rel -> rel.getRelationshipDetails().stream())
                                .collect(Collectors.toList());
                            return P2PRelationship.builder()
                                .parentPartyId(relationships.get(0).getParentPartyId()) // Retain the same parentPartyId
                                .effectivePercentageValueOfOwnership(relationships.get(0).getEffectivePercentageValueOfOwnership())
                                .relationshipDetails(combinedDetails) // Combine all relationshipDetails
                                .build();
                        } else {
                            // For single relationships, keep as is
                            return relationships.get(0);
                        }
                    })
                    .collect(Collectors.toList());

                // Build a new P2PVisualization instance with grouped non-ownership relationships
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
                    .nonOwnershipRelationships(groupedNonOwnerships) // Set grouped non-ownership relationships
                    .build();
            })
            .orElse(Stream.of(party))) // If no nonOwnershipRelationships, keep the original party
        .collect(Collectors.toList());

    // Step 2: Partition and sort
    return Stream.concat(
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()), // Without non-ownership relationships
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) // With non-ownership relationships
                .sorted(Comparator.comparing(party -> {
                    List<P2PRelationship> nonOwnerships = party.getNonOwnershipRelationships();
                    boolean hasMultipleRelationships = nonOwnerships.stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values().stream()
                        .anyMatch(details -> details.size() > 1);

                    if (hasMultipleRelationships) {
                        return Integer.MAX_VALUE; // Keep entries with multiple relationships at the end
                    } else {
                        // Sort by relationshipTypeId without accessing specific elements
                        return nonOwnerships.stream()
                            .flatMap(rel -> rel.getRelationshipDetails().stream())
                            .map(RelationshipDetail::getRelationshipTypeId)
                            .findAny().orElse(""); // Use findAny to avoid ordering concerns
                    }
                }, Comparator.nullsLast(Comparator.naturalOrder())))
        )
        .collect(Collectors.toList());
}







public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    // Step 1: Separate entries with multiple relationship details under the same parentPartyId
    List<P2PVisualization> separatedList = p2pVisualizationParties.stream()
        .flatMap(party -> Optional.ofNullable(party.getNonOwnershipRelationships())
            .filter(nonOwnerships -> !nonOwnerships.isEmpty())
            .map(nonOwnerships -> {
                // If there are multiple relationships with the same parentPartyId, retain them as is
                List<P2PRelationship> combinedNonOwnerships = nonOwnerships.stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                    .values().stream()
                    .map(relationships -> {
                        if (relationships.size() > 1) {
                            // Combine multiple relationships under the same parentPartyId into a single entry
                            List<RelationshipDetail> combinedDetails = relationships.stream()
                                .flatMap(rel -> rel.getRelationshipDetails().stream())
                                .collect(Collectors.toList());
                            return P2PRelationship.builder()
                                .parentPartyId(relationships.get(0).getParentPartyId())
                                .effectivePercentageValueOfOwnership(relationships.get(0).getEffectivePercentageValueOfOwnership())
                                .relationshipDetails(combinedDetails)
                                .build();
                        } else {
                            // Keep single relationships as they are
                            return relationships.get(0);
                        }
                    })
                    .collect(Collectors.toList());

                // Build a new P2PVisualization instance with combined non-ownership relationships
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
                    .nonOwnershipRelationships(combinedNonOwnerships)
                    .build();
            })
            .orElse(Stream.of(party))) // If no nonOwnershipRelationships, keep the original party
        .collect(Collectors.toList());

    // Step 2: Partition and sort
    return Stream.concat(
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()), // Without non-ownership relationships
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) // With non-ownership relationships
                .sorted(Comparator.comparing(party -> {
                    List<P2PRelationship> nonOwnerships = party.getNonOwnershipRelationships();
                    boolean hasMultipleRelationships = nonOwnerships.stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values().stream()
                        .anyMatch(details -> details.size() > 1);

                    if (hasMultipleRelationships) {
                        return Integer.MAX_VALUE; // Keep entries with multiple relationships at the end
                    } else {
                        // Sort by relationshipTypeId of the single relationship detail
                        return nonOwnerships.get(0).getRelationshipDetails().get(0).getRelationshipTypeId();
                    }
                }, Comparator.nullsLast(Comparator.naturalOrder())))
        )
        .collect(Collectors.toList());
}







public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    // Step 1: Separate each nonOwnershipRelationship and its relationshipDetails into its own P2PVisualization instance
    List<P2PVisualization> separatedList = p2pVisualizationParties.stream()
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
            .orElse(Stream.of(party)) // If no nonOwnershipRelationships, keep the original party
        )
        .collect(Collectors.toList());

    // Step 2: Partition and sort - non-ownership relationships come at the end, sorted by relationshipTypeId
    return Stream.concat(
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()), // Without non-ownership
            separatedList.stream()
                .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) // With non-ownership
                .sorted(Comparator.comparing(party -> party.getNonOwnershipRelationships().stream()
                    .flatMap(nonOwnership -> nonOwnership.getRelationshipDetails().stream().findFirst().stream())
                    .map(RelationshipDetail::getRelationshipTypeId)
                    .findFirst().orElse(""))) // Sort by relationshipTypeId using findFirst()
        )
        .collect(Collectors.toList());
}






public List<P2PVisualization> processAndSortP2PVisualizations(List<P2PVisualization> p2pVisualizationParties) {
    // Step 1: Separate each nonOwnershipRelationship and its relationshipDetails into its own P2PVisualization instance
    List<P2PVisualization> separatedList = p2pVisualizationParties.stream()
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
