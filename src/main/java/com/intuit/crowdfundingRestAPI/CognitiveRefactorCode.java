
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Initialize parties
        Party partyA = new Party("A");
        Party partyB = new Party("B");
        Party partyC = new Party("C");
        Party partyD = new Party("D");
        Party partyE = new Party("E");
        Party partyF = new Party("F");
        Party partyG = new Party("G");

        // Initialize relationships based on the provided diagram
        Relationship r1 = new Relationship(partyB, "r1");
        Relationship r2 = new Relationship(partyC, "r2");
        Relationship r3 = new Relationship(partyE, "r3");
        Relationship r4 = new Relationship(partyD, "r4");
        Relationship r5 = new Relationship(partyF, "r5");
        Relationship r6 = new Relationship(partyG, "r6");
        Relationship r7 = new Relationship(partyG, "r7"); // Additional relationship to make G multiparent

        // Create graph and add parties
        RelationshipGraphHierarchy graph = new RelationshipGraphHierarchy();
        graph.addParty(partyA);
        graph.addParty(partyB);
        graph.addParty(partyC);
        graph.addParty(partyD);
        graph.addParty(partyE);
        graph.addParty(partyF);
        graph.addParty(partyG);

        // Add relationships to the graph
        graph.addRelationship("A", "B", r1);
        graph.addRelationship("A", "C", r2);
        graph.addRelationship("B", "E", r3);
        graph.addRelationship("A", "D", r4);
        graph.addRelationship("C", "F", r5);
        graph.addRelationship("D", "G", r6);
        graph.addRelationship("C", "G", r7);

        // Calculate levels and the longest path parent
        List<String[]> multiparentChildren = graph.recalculateLevelsAndParents();

        // Print multiparent children and their longest path parents
        for (String[] childParent : multiparentChildren) {
            System.out.println("Multiparent child: " + childParent[0] + ", Longest path parent: " + childParent[1]);
        }

        // Print the tree structure
        System.out.println("\nTree Structure:");
        graph.printTree("A");
    }
}



import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class RelationshipGraphHierarchy {
    private final Map<String, Party> partyLookup = new HashMap<>();

    public void addParty(Party party) {
        partyLookup.put(party.getPartyId(), party);
    }

    public void addRelationship(String parentPartyId, String childPartyId, Relationship relationship) {
        Party parentParty = partyLookup.get(parentPartyId);
        Party childParty = partyLookup.get(childPartyId);
        relationship.setChildParty(childParty);
        parentParty.addRelationship(relationship);
        recalculateLevelsAndParents();
    }

    public void addRelationships(String sourcePartyId, List<Relationship> relationships) {
        Party sourceParty = partyLookup.get(sourcePartyId);
        for (Relationship relationship : relationships) {
            Party destinationParty = partyLookup.get(relationship.getChildParty().getPartyId());
            relationship.setChildParty(destinationParty);
            sourceParty.addRelationship(relationship);
        }
        recalculateLevelsAndParents();
    }

    private Map<String, Integer> levelLookup = new HashMap<>();
    private Map<String, String> longestPathParentLookup = new HashMap<>();

    private List<String[]> recalculateLevelsAndParents() {
        levelLookup.clear();
        longestPathParentLookup.clear();
        Map<String, List<String>> parentTracker = new HashMap<>();

        for (String root : partyLookup.keySet()) {
            if (!levelLookup.containsKey(root)) {
                dfsCalculateLevels(root, 0, null, parentTracker);
            }
        }

        List<String[]> multiparentChildren = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : parentTracker.entrySet()) {
            if (entry.getValue().size() > 1) {
                String child = entry.getKey();
                String longestPathParent = longestPathParentLookup.get(child);
                multiparentChildren.add(new String[]{child, longestPathParent});
            }
        }
        return multiparentChildren;
    }

    private void dfsCalculateLevels(String partyId, int level, String parent, Map<String, List<String>> parentTracker) {
        if (!levelLookup.containsKey(partyId) || level > levelLookup.get(partyId)) {
            levelLookup.put(partyId, level);
            if (parent != null) {
                longestPathParentLookup.put(partyId, parent);
                parentTracker.computeIfAbsent(partyId, k -> new ArrayList<>()).add(parent);
            }
        }

        Party party = partyLookup.get(partyId);
        if (party == null) return;

        for (Relationship relationship : party.getRelatedPartyHierarchy()) {
            String destinationPartyId = relationship.getChildParty().getPartyId();
            dfsCalculateLevels(destinationPartyId, level + 1, partyId, parentTracker);
        }
    }

    public int getPartyLevel(String partyId) {
        return levelLookup.getOrDefault(partyId, -1);
    }

    public String getLongestPathParent(String partyId) {
        return longestPathParentLookup.get(partyId);
    }
}
