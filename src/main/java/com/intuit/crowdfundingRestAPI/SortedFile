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
