public void pollAll(Queue<P2PHierarchyParty> p2pHierarchyPartyQueue) {
    while (!p2pHierarchyPartyQueue.isEmpty()) {
        // Poll the first element in the queue
        P2PHierarchyParty currentParty = p2pHierarchyPartyQueue.poll();

        // Process the polled party (this is where you would implement your logic)
        // For example, print party details or handle relationships
        System.out.println("Processing party: " + currentParty.getPartyId());

        // Assuming you have relationships or other actions to perform:
        List<P2PHierarchyRelationship> relationships = currentParty.getP2PHierarchyRelationship().values()
                .stream().flatMap(Collection::stream).collect(Collectors.toList());

        for (P2PHierarchyRelationship relationship : relationships) {
            // Process each relationship if needed
            System.out.println("Processing relationship: " + relationship.getChildParty().getPartyId());
        }

        // Add more logic if necessary for further processing of the hierarchy
    }
}
