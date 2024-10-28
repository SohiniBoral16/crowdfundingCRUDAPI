
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortByNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
        return p2pVisualizationParties.stream()
            .sorted(Comparator.comparing(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()))
            .collect(Collectors.toList());
    }
}





import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> processP2PVisualizationParties(List<P2PVisualization> p2pVisualizationParties) {
        return p2pVisualizationParties.stream()
            .flatMap(party -> {
                List<P2PRelationship> nonOwnershipRelationships = party.getNonOwnershipRelationships();

                // Step 1: Sort nonOwnershipRelationships by `parentPartyId`
                List<P2PRelationship> sortedNonOwnershipRelationships = (nonOwnershipRelationships != null)
                    ? nonOwnershipRelationships.stream()
                        .sorted(Comparator.comparing(P2PRelationship::getParentPartyId))
                        .collect(Collectors.toList())
                    : new ArrayList<>();

                // Step 2: Split each relationship based on `parentPartyId` to create distinct `P2PVisualization` entries
                return sortedNonOwnershipRelationships.stream()
                    .map(singleRelationship -> P2PVisualization.builder()
                        .partyId(party.getPartyId())
                        .parentId(party.getParentId())
                        .partyName(party.getPartyName())
                        .validationStatus(party.getValidationStatus())
                        .countryOfOrganization(party.getCountryOfOrganization())
                        .legalForm(party.getLegalForm())
                        .countryOfDomicile(party.getCountryOfDomicile())
                        .dateOfBirth(party.getDateOfBirth())
                        .dateOfIncorporation(party.getDateOfIncorporation())
                        .countrySpecificIdentifiers(party.getCountrySpecificIdentifiers())
                        .pepIndicator(party.getPepIndicator())
                        .effectivePercentageValueOfOwnership(party.getEffectivePercentageValueOfOwnership())
                        .ownershipRelationships(party.getOwnershipRelationships())
                        .nonOwnershipRelationships(List.of(singleRelationship))  // Only one relationship per entry
                        .build()
                    );
            })
            .collect(Collectors.toList());
    }
}




import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> processP2PVisualizationParties(List<P2PVisualization> p2pVisualizationParties) {
        return p2pVisualizationParties.stream()
            .flatMap(party -> {
                List<P2PRelationship> nonOwnershipRelationships = party.getNonOwnershipRelationships();

                // Step 1: Sort nonOwnershipRelationships by `parentPartyId` if the list is not empty
                List<P2PRelationship> sortedNonOwnershipRelationships = (nonOwnershipRelationships != null)
                    ? nonOwnershipRelationships.stream()
                        .sorted(Comparator.comparing(P2PRelationship::getParentPartyId))
                        .collect(Collectors.toList())
                    : new ArrayList<>();

                // Step 2: Check for different `parentPartyId`s and split into separate P2PVisualization entries if needed
                if (sortedNonOwnershipRelationships.stream().map(P2PRelationship::getParentPartyId).distinct().count() > 1) {
                    // Group by `parentPartyId` and create separate `P2PVisualization` entries for each group
                    return sortedNonOwnershipRelationships.stream()
                        .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                        .values()
                        .stream()
                        .map(groupedRelationships -> P2PVisualization.builder()
                            .partyId(party.getPartyId())
                            .parentId(party.getParentId())
                            .partyName(party.getPartyName())
                            .validationStatus(party.getValidationStatus())
                            .countryOfOrganization(party.getCountryOfOrganization())
                            .legalForm(party.getLegalForm())
                            .countryOfDomicile(party.getCountryOfDomicile())
                            .dateOfBirth(party.getDateOfBirth())
                            .dateOfIncorporation(party.getDateOfIncorporation())
                            .countrySpecificIdentifiers(party.getCountrySpecificIdentifiers())
                            .pepIndicator(party.getPepIndicator())
                            .effectivePercentageValueOfOwnership(party.getEffectivePercentageValueOfOwnership())
                            .ownershipRelationships(party.getOwnershipRelationships())
                            .nonOwnershipRelationships(groupedRelationships)  // Assign each group of relationships
                            .build()
                        );
                } else {
                    // If there are no different `parentPartyId`s, return the original party with sorted nonOwnershipRelationships
                    return List.of(P2PVisualization.builder()
                        .partyId(party.getPartyId())
                        .parentId(party.getParentId())
                        .partyName(party.getPartyName())
                        .validationStatus(party.getValidationStatus())
                        .countryOfOrganization(party.getCountryOfOrganization())
                        .legalForm(party.getLegalForm())
                        .countryOfDomicile(party.getCountryOfDomicile())
                        .dateOfBirth(party.getDateOfBirth())
                        .dateOfIncorporation(party.getDateOfIncorporation())
                        .countrySpecificIdentifiers(party.getCountrySpecificIdentifiers())
                        .pepIndicator(party.getPepIndicator())
                        .effectivePercentageValueOfOwnership(party.getEffectivePercentageValueOfOwnership())
                        .ownershipRelationships(party.getOwnershipRelationships())
                        .nonOwnershipRelationships(sortedNonOwnershipRelationships)  // Assign sorted relationships
                        .build()
                    ).stream();
                }
            })
            .collect(Collectors.toList());
    }
}




----------------------------
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortAndSplitNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
        List<P2PVisualization> updatedParties = new ArrayList<>();

        for (P2PVisualization party : p2pVisualizationParties) {
            List<P2PRelationship> nonOwnershipRelationships = party.getNonOwnershipRelationships();

            // Step 1: Check for different `parentId`s in `nonOwnershipRelationships`
            List<P2PRelationship> splitRelationships = new ArrayList<>();
            if (nonOwnershipRelationships != null && !nonOwnershipRelationships.isEmpty()) {
                // Group relationships by `parentId` and add each group to `splitRelationships`
                splitRelationships = nonOwnershipRelationships.stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
                
                // Only update if there are different `parentId`s
                if (splitRelationships.size() > nonOwnershipRelationships.size()) {
                    // Create a new `P2PVisualization` instance using the builder with updated `nonOwnershipRelationships`
                    party = P2PVisualization.builder()
                            .partyId(party.getPartyId())
                            .parentId(party.getParentId())
                            .partyName(party.getPartyName())
                            .validationStatus(party.getValidationStatus())
                            .countryOfOrganization(party.getCountryOfOrganization())
                            .legalForm(party.getLegalForm())
                            .countryOfDomicile(party.getCountryOfDomicile())
                            .dateOfBirth(party.getDateOfBirth())
                            .dateOfIncorporation(party.getDateOfIncorporation())
                            .countrySpecificIdentifiers(party.getCountrySpecificIdentifiers())
                            .pepIndicator(party.getPepIndicator())
                            .effectivePercentageValueOfOwnership(party.getEffectivePercentageValueOfOwnership())
                            .ownershipRelationships(party.getOwnershipRelationships())
                            .nonOwnershipRelationships(splitRelationships)  // Setting the split relationships
                            .build();
                }
            }

            // Step 2: Sort `nonOwnershipRelationships` by `parentId + relationshipTypeId`
            party.getNonOwnershipRelationships().sort(
                Comparator.comparing((P2PRelationship rel) -> rel.getParentPartyId())
                          .thenComparing(rel -> {
                              List<RelationshipDetail> details = rel.getRelationshipDetails();
                              return (details != null && !details.isEmpty()) ? details.get(0).getRelationshipTypeId() : "";
                          })
            );

            // Add the updated `party` to the result list
            updatedParties.add(party);
        }

        return updatedParties;
    }
}




---------------------
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortAndSplitNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
        List<P2PVisualization> updatedParties = new ArrayList<>();

        for (P2PVisualization party : p2pVisualizationParties) {
            List<P2PRelationship> nonOwnershipRelationships = party.getNonOwnershipRelationships();

            // Step 1: Check for different `parentId`s in `nonOwnershipRelationships`
            List<P2PRelationship> splitRelationships = nonOwnershipRelationships;
            if (nonOwnershipRelationships != null && !nonOwnershipRelationships.isEmpty()) {
                // Group relationships by `parentId`
                splitRelationships = new ArrayList<>();
                
                nonOwnershipRelationships.stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                    .forEach((parentId, groupedRelationships) -> splitRelationships.addAll(groupedRelationships));

                // Only update if there are different `parentId`s
                if (splitRelationships.size() > nonOwnershipRelationships.size()) {
                    // Create a new `P2PVisualization` instance using the builder with updated `nonOwnershipRelationships`
                    party = P2PVisualization.builder()
                            .partyId(party.getPartyId())
                            .parentId(party.getParentId())
                            .partyName(party.getPartyName())
                            .validationStatus(party.getValidationStatus())
                            .countryOfOrganization(party.getCountryOfOrganization())
                            .legalForm(party.getLegalForm())
                            .countryOfDomicile(party.getCountryOfDomicile())
                            .dateOfBirth(party.getDateOfBirth())
                            .dateOfIncorporation(party.getDateOfIncorporation())
                            .countrySpecificIdentifiers(party.getCountrySpecificIdentifiers())
                            .pepIndicator(party.getPepIndicator())
                            .effectivePercentageValueOfOwnership(party.getEffectivePercentageValueOfOwnership())
                            .ownershipRelationships(party.getOwnershipRelationships())
                            .nonOwnershipRelationships(splitRelationships)  // Setting the split relationships
                            .build();
                }
            }

            // Step 2: Sort `nonOwnershipRelationships` by `parentId + relationshipTypeId`
            party.getNonOwnershipRelationships().sort(
                Comparator.comparing((P2PRelationship rel) -> rel.getParentPartyId())
                          .thenComparing(rel -> {
                              List<RelationshipDetail> details = rel.getRelationshipDetails();
                              return (details != null && !details.isEmpty()) ? details.get(0).getRelationshipTypeId() : "";
                          })
            );

            // Add the updated `party` to the result list
            updatedParties.add(party);
        }

        return updatedParties;
    }
}








import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortAndSplitNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
        List<P2PVisualization> updatedParties = new ArrayList<>();

        for (P2PVisualization party : p2pVisualizationParties) {
            List<P2PRelationship> nonOwnershipRelationships = party.getNonOwnershipRelationships();

            // Step 1: Check for different `parentId`s in `nonOwnershipRelationships`
            List<P2PRelationship> splitRelationships = nonOwnershipRelationships;
            if (nonOwnershipRelationships != null && !nonOwnershipRelationships.isEmpty()) {
                // Group relationships by `parentId`
                splitRelationships = new ArrayList<>();
                
                nonOwnershipRelationships.stream()
                    .collect(Collectors.groupingBy(P2PRelationship::getParentPartyId))
                    .forEach((parentId, groupedRelationships) -> splitRelationships.addAll(groupedRelationships));

                // Only update if there are different `parentId`s
                if (splitRelationships.size() > nonOwnershipRelationships.size()) {
                    // Create a new `P2PVisualization` instance using the builder with updated `nonOwnershipRelationships`
                    party = P2PVisualization.builder()
                            .partyId(party.getPartyId())
                            .parentId(party.getParentId())
                            .partyName(party.getPartyName())
                            .validationStatus(party.getValidationStatus())
                            .countryOfOrganization(party.getCountryOfOrganization())
                            .legalForm(party.getLegalForm())
                            .countryOfDomicile(party.getCountryOfDomicile())
                            .dateOfBirth(party.getDateOfBirth())
                            .dateOfIncorporation(party.getDateOfIncorporation())
                            .countrySpecificIdentifiers(party.getCountrySpecificIdentifiers())
                            .pepIndicator(party.getPepIndicator())
                            .effectivePercentageValueOfOwnership(party.getEffectivePercentageValueOfOwnership())
                            .ownershipRelationships(party.getOwnershipRelationships())
                            .nonOwnershipRelationships(splitRelationships)  // Setting the split relationships
                            .build();
                }
            }

            // Step 2: Sort `nonOwnershipRelationships` by `parentId + relationshipTypeId`
            party.getNonOwnershipRelationships().sort(
                Comparator.comparing(rel -> rel.getParentPartyId() + rel.getRelationshipTypeId())
            );

            // Add the updated `party` to the result list
            updatedParties.add(party);
        }

        return updatedParties;
    }
}





import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortAndSplitNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
        for (P2PVisualization party : p2pVisualizationParties) {
            List<RelationshipDetail> nonOwnershipRelationships = party.getNonOwnershipRelationships();

            // Step 1: Check for different `parentId`s in `nonOwnershipRelationships`
            if (nonOwnershipRelationships != null && !nonOwnershipRelationships.isEmpty()) {
                // Group relationships by `parentId`
                List<RelationshipDetail> splitRelationships = new ArrayList<>();
                
                nonOwnershipRelationships.stream()
                    .collect(Collectors.groupingBy(RelationshipDetail::getParentPartyId))
                    .forEach((parentId, groupedRelationships) -> splitRelationships.addAll(groupedRelationships));

                // Replace the original list with the split relationships if they have different parentIds
                if (splitRelationships.size() > nonOwnershipRelationships.size()) {
                    party.setNonOwnershipRelationships(splitRelationships);
                }
            }

            // Step 2: Sort `nonOwnershipRelationships` by `parentId + relationshipTypeId`
            if (party.getNonOwnershipRelationships() != null) {
                party.getNonOwnershipRelationships().sort(
                    Comparator.comparing(rel -> rel.getParentPartyId() + rel.getRelationshipTypeId())
                );
            }
        }

        return p2pVisualizationParties;
    }
}





import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortAndSplitNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties) {
        for (P2PVisualization party : p2pVisualizationParties) {
            // Step 1: Split `nonOwnershipRelationships` if multiple relationships are present
            List<RelationshipDetail> nonOwnershipRelationships = party.getNonOwnershipRelationships();
            if (nonOwnershipRelationships != null && nonOwnershipRelationships.size() > 1) {
                List<RelationshipDetail> splitRelationships = new ArrayList<>(nonOwnershipRelationships);
                party.setNonOwnershipRelationships(splitRelationships);  // Assuming you have a setter
            }

            // Step 2: Sort `nonOwnershipRelationships` by `parentId + relationshipTypeId`
            if (party.getNonOwnershipRelationships() != null) {
                party.getNonOwnershipRelationships().sort(Comparator.comparing(
                        rel -> (party.getParentId() + rel.getRelationshipTypeId())));
            }
        }

        return p2pVisualizationParties;
    }
}










import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RelationshipDetail implements Comparator<RelationshipDetail> {
    private String relationshipTypeId;
    private String relationshipTypeName;
    private RelationshipAttributeDetails relationshipAttributeDetails;

    @Override
    public int compare(RelationshipDetail o1, RelationshipDetail o2) {
        return o1.getRelationshipTypeId().compareTo(o2.getRelationshipTypeId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipDetail that = (RelationshipDetail) o;
        return Objects.equals(relationshipTypeId, that.relationshipTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationshipTypeId);
    }
}






import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortP2PVisualizationParties(List<P2PVisualization> p2pVisualizationParties) {
        // Step 1: Sort `p2pVisualizationParties` to move entries with non-empty `nonOwnershipRelationships` to the end
        List<P2PVisualization> sortedParties = p2pVisualizationParties.stream()
                .sorted(Comparator.comparing(party -> !(party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty())))
                .collect(Collectors.toList());

        // Step 2: For each `P2PVisualization` entry, sort `ownershipRelationships` by `relationshipTypeId`
        for (P2PVisualization party : sortedParties) {
            if (party.getOwnershipRelationships() != null && !party.getOwnershipRelationships().isEmpty()) {
                party.getOwnershipRelationships().sort(Comparator.comparing(RelationshipDetail::getRelationshipTypeId));
            }
        }

        return sortedParties;
    }
}





import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortP2PVisualizationParties(List<P2PVisualization> p2pVisualizationParties) {
        // Step 1: Sort each `nonOwnershipRelationships` list by `relationshipTypeId` if present
        for (P2PVisualization party : p2pVisualizationParties) {
            if (party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) {
                party.getNonOwnershipRelationships().sort(Comparator.comparing(RelationshipDetail::getRelationshipTypeId));
            }
        }

        // Step 2: Sort the `p2pVisualizationParties` list based on the presence of `nonOwnershipRelationships`
        List<P2PVisualization> sortedParties = p2pVisualizationParties.stream()
                .sorted(Comparator.comparing(party -> party.getNonOwnershipRelationships() == null || party.getNonOwnershipRelationships().isEmpty()))
                .collect(Collectors.toList());

        return sortedParties;
    }
}






import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class P2PVisualizationService {

    public List<P2PVisualization> sortP2PVisualizationParties(List<P2PVisualization> p2pVisualizationParties) {
        for (P2PVisualization party : p2pVisualizationParties) {
            // Sort nonOwnershipRelationships in place if present
            if (party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) {
                SortedSet<RelationshipDetail> sortedNonOwnership = new TreeSet<>(party.getNonOwnershipRelationships());
                
                // Clear the list and add back the sorted elements to maintain final reference
                party.getNonOwnershipRelationships().clear();
                party.getNonOwnershipRelationships().addAll(sortedNonOwnership);
            }
        }
        return p2pVisualizationParties;
    }
}


SortedSet<RelationshipDetail> sortedNonOwnership = new TreeSet<>(Comparator.comparing(RelationshipDetail::getRelationshipTypeId));
sortedNonOwnership.addAll(party.getNonOwnershipRelationships());




import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class P2PVisualizationService {

    public List<P2PVisualization> sortP2PVisualizationParties(List<P2PVisualization> p2pVisualizationParties) {
        for (P2PVisualization party : p2pVisualizationParties) {
            // Sort nonOwnershipRelationships in place if present
            if (party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) {
                Collections.sort(party.getNonOwnershipRelationships(), Comparator.comparing(RelationshipDetail::getRelationshipTypeId));
            }
        }
        return p2pVisualizationParties;
    }
}





import java.util.*;
import java.util.stream.Collectors;

public class P2PVisualizationService {

    public List<P2PVisualization> sortP2PVisualizationParties(List<P2PVisualization> p2pVisualizationParties) {
        List<P2PVisualization> sortedParties = new ArrayList<>();

        for (P2PVisualization party : p2pVisualizationParties) {
            // Sort nonOwnershipRelationships and move it to the end if present
            if (party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()) {
                SortedSet<P2PRelationship> sortedNonOwnership = new TreeSet<>(Comparator.comparing(P2PRelationship::getRelationshipTypeId));
                sortedNonOwnership.addAll(party.getNonOwnershipRelationships());
                party.setNonOwnershipRelationships(new ArrayList<>(sortedNonOwnership));
            }

            // Sort ownershipRelationships by relationshipTypeId
            if (party.getOwnershipRelationships() != null && !party.getOwnershipRelationships().isEmpty()) {
                party.setOwnershipRelationships(party.getOwnershipRelationships().stream()
                        .sorted(Comparator.comparing(P2PRelationship::getRelationshipTypeId))
                        .collect(Collectors.toList()));
            }

            sortedParties.add(party);
        }

        return sortedParties;
    }
}
