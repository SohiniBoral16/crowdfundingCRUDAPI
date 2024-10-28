
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
