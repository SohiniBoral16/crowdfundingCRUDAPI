
import java.util.*;
import java.util.stream.Collectors;

// Generic method to process relationships in Java 11 standards
private TreeSet<P2PRelationship> processRelationships(
        List<P2PRelationship> oldRelationships, 
        List<P2PRelationship> newRelationships) {

    // Create a TreeSet with a comparator to sort relationships based on multiple relationship details
    return oldRelationships.stream()
        .filter(relationship -> !newRelationships.contains(relationship)) // Filter out duplicates
        .collect(Collectors.toCollection(() -> 
            new TreeSet<>((rel1, rel2) -> {
                // Compare relationship details dynamically, traverse all available details
                List<RelationshipDetail> rel1Details = rel1.getRelationshipDetails();
                List<RelationshipDetail> rel2Details = rel2.getRelationshipDetails();
                
                // Traverse through all the available details for comparison
                for (int i = 0; i < Math.min(rel1Details.size(), rel2Details.size()); i++) {
                    int result = rel1Details.get(i).getRelationshipTypeId()
                            .compareTo(rel2Details.get(i).getRelationshipTypeId());
                    
                    // If the current comparison yields a result, return it
                    if (result != 0) {
                        return result;
                    }
                }

                // If all compared details are equal, relationships are considered equal
                return 0;
            })
        ));
}

// Example usage within the main logic to combine ownership and non-ownership relationships
public List<P2PVisualization> getP2PVisualizationObj(List<P2PVisualization> visualizationParties) {
    return new ArrayList<>(visualizationParties.stream()
        .collect(Collectors.toMap(P2PVisualization::getPartyId,
            Function.identity(),
            (oldRel, newRel) -> {
                // Process ownership relationships
                TreeSet<P2PRelationship> ownerRelToAdd = processRelationships(
                    oldRel.getOwnershipRelationships(), 
                    newRel.getOwnershipRelationships());

                // Add sorted ownership relationships to newRel
                newRel.getOwnershipRelationships().addAll(ownerRelToAdd);

                // Process non-ownership relationships
                TreeSet<P2PRelationship> nonOwnerRelToAdd = processRelationships(
                    oldRel.getNonOwnershipRelationships(),
                    newRel.getNonOwnershipRelationships());

                // Add sorted non-ownership relationships to newRel
                newRel.getNonOwnershipRelationships().addAll(nonOwnerRelToAdd);

                return newRel;
            },
            LinkedHashMap::new)) // Maintain insertion order
        .values());
}



---------------------------
// Reduce P2PVisualization objects into single object for a given party by adding different P2PRelationships
var p2pVisualizationObj = new ArrayList<>(visualizationParties.stream()
    .collect(Collectors.toMap(P2PVisualization::getPartyId,
        Function.identity(),
        (oldRel, newRel) -> {
            // For ownership relationships, keeping the original list-based logic
            List<P2PRelationship> ownerRelToAdd = oldRel.getOwnershipRelationships().stream()
                .filter(relationship -> !newRel.getOwnershipRelationships().contains(relationship))
                .collect(Collectors.toList());

            newRel.getOwnershipRelationships().addAll(ownerRelToAdd);

            // For non-ownership relationships, switching to TreeSet to maintain sorted order
            TreeSet<P2PRelationship> nonOwnerRelToAdd = new TreeSet<>(Comparator.comparing(P2PRelationship::getSomeSortingField)); // Replace with the field to sort by
            nonOwnerRelToAdd.addAll(oldRel.getNonOwnershipRelationships().stream()
                .filter(relationship -> !newRel.getNonOwnershipRelationships().contains(relationship))
                .collect(Collectors.toList()));

            // Add non-ownership relationships to the newRel using TreeSet to ensure sorting
            newRel.getNonOwnershipRelationships().addAll(nonOwnerRelToAdd);

            return newRel;
        },
        LinkedHashMap::new)) // Maintain insertion order
    .values());

// Return the reduced p2pVisualizationObj
return p2pVisualizationObj;





// Reduce P2PVisualization objects into a single object for a given party, adding different P2PRelationships
var p2pVisualizationObj = new ArrayList<>(visualizationParties.stream()
    .collect(Collectors.toMap(P2PVisualization::getPartyId,
        Function.identity(),
        (oldRel, newRel) -> {
            // Use TreeSet instead of List to maintain sorted order
            TreeSet<P2PRelationship> ownerRelToAdd = new TreeSet<>(Comparator.comparing(P2PRelationship::getSomeSortingField)); // Replace with the field to sort by
            ownerRelToAdd.addAll(oldRel.getOwnershipRelationships());
            ownerRelToAdd.addAll(newRel.getOwnershipRelationships());
            
            // For non-ownership relationships
            TreeSet<P2PRelationship> nonOwnerRelToAdd = new TreeSet<>(Comparator.comparing(P2PRelationship::getSomeSortingField)); // Replace with the field to sort by
            nonOwnerRelToAdd.addAll(oldRel.getNonOwnershipRelationships());
            nonOwnerRelToAdd.addAll(newRel.getNonOwnershipRelationships());

            // Update relationships in newRel
            newRel.getOwnershipRelationships().clear();
            newRel.getOwnershipRelationships().addAll(ownerRelToAdd);

            newRel.getNonOwnershipRelationships().clear();
            newRel.getNonOwnershipRelationships().addAll(nonOwnerRelToAdd);

            return newRel;
        },
        LinkedHashMap::new)) // Maintain insertion order
    .values());

// Return the reduced p2pVisualizationObj
return p2pVisualizationObj;
