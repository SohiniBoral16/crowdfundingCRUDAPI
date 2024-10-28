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
