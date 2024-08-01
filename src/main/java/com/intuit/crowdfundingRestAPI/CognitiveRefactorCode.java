import java.util.Optional;
import java.util.function.Supplier;

public class PartyRelationshipVisualizationService {

    public PartyRelationshipVisualization getPartyDetails(Party party) {
        PartyRelationshipVisualization partyDetails = new PartyRelationshipVisualization();

        // Set other attributes...

        // Using Supplier for getCountryOfDomicile logic
        partyDetails.setCountryOfDomicile(getValue(() -> {
            if (party.getCountryOfDomicile() != null) {
                if (party.getCountryOfDomicile().getName() != null) {
                    return party.getCountryOfDomicile().getName();
                } else {
                    return party.getCountryOfDomicile().getId().toString();
                }
            }
            return null;
        }));

        // Set other attributes...

        return partyDetails;
    }

    private <T> T getValue(Supplier<T> supplier) {
        return Optional.ofNullable(supplier.get()).orElse(null);
    }

    // Other methods...
}
------------

import java.util.List;

class Party {
    String partyId;
    List<Relationship> relatedParties;

    public Party(String partyId, List<Relationship> relatedParties) {
        this.partyId = partyId;
        this.relatedParties = relatedParties;
    }
}

class Relationship {
    String relationshipType;
    double directOwnership;
    double indirectOwnership;
    double ownershipRange;
    boolean significantInfluence;
    Party childParty;

    public Relationship(String relationshipType, double directOwnership, double indirectOwnership, double ownershipRange, boolean significantInfluence, Party childParty) {
        this.relationshipType = relationshipType;
        this.directOwnership = directOwnership;
        this.indirectOwnership = indirectOwnership;
        this.ownershipRange = ownershipRange;
        this.significantInfluence = significantInfluence;
        this.childParty = childParty;
    }
}


import java.util.HashMap;
import java.util.Map;

class Graph {
    Map<String, Party> partyLookup;

    public Graph() {
        this.partyLookup = new HashMap<>();
    }

    public void addParty(Party party) {
        partyLookup.put(party.partyId, party);
    }

    public void addRelationship(String parentPartyId, String childPartyId, Relationship relationship) {
        Party parentParty = partyLookup.get(parentPartyId);
        Party childParty = partyLookup.get(childPartyId);
        relationship.childParty = childParty;
        parentParty.relatedParties.add(relationship);
    }
}

import java.util.*;

class RelationshipTree {
    String partyId;
    Map<String, List<RelationshipTree>> childrenByType;

    public RelationshipTree(String partyId) {
        this.partyId = partyId;
        this.childrenByType = new HashMap<>();
    }

    public void addChild(String relationshipType, RelationshipTree childTree) {
        this.childrenByType.computeIfAbsent(relationshipType, k -> new ArrayList<>()).add(childTree);
    }

    public void printTree(String indent) {
        System.out.println(indent + partyId);
        for (Map.Entry<String, List<RelationshipTree>> entry : childrenByType.entrySet()) {
            String type = entry.getKey();
            List<RelationshipTree> children = entry.getValue();
            System.out.println(indent + "  Type: " + type);
            for (RelationshipTree child : children) {
                child.printTree(indent + "    ");
            }
        }
    }

    public void calculateIndirectOwnershipAndPepStatus(Graph graph, double parentOwnership, Set<String> pepParties) {
        Party party = graph.partyLookup.get(partyId);
        boolean isPep = pepParties.contains(partyId);
        for (Relationship relationship : party.relatedParties) {
            RelationshipTree childTree = new RelationshipTree(relationship.childParty.partyId);
            addChild(relationship.relationshipType, childTree);
            double childIndirectOwnership = parentOwnership * relationship.directOwnership;
            childTree.calculateIndirectOwnershipAndPepStatus(graph, childIndirectOwnership, pepParties);
            isPep = isPep || pepParties.contains(relationship.childParty.partyId);
        }
        if (isPep) {
            pepParties.add(partyId);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Initialize parties and relationships (this would normally come from your database)
        Party partyA = new Party("A", new ArrayList<>());
        Party partyB = new Party("B", new ArrayList<>());
        Party partyC = new Party("C", new ArrayList<>());
        Party partyD = new Party("D", new ArrayList<>());
        Party partyE = new Party("E", new ArrayList<>());
        Party partyF = new Party("F", new ArrayList<>());

        Relationship r1 = new Relationship("r1", 0.5, 0, 0, false, partyB);
        Relationship r2 = new Relationship("r2", 0.3, 0, 0, false, partyC);
        Relationship r3 = new Relationship("r3", 0.4, 0, 0, false, partyD);
        Relationship r4 = new Relationship("r4", 0.2, 0, 0, false, partyE);
        Relationship r5 = new Relationship("r5", 0.1, 0, 0, false, partyF);

        // Create graph and add parties and relationships
        Graph graph = new Graph();
        graph.addParty(partyA);
        graph.addParty(partyB);
        graph.addParty(partyC);
        graph.addParty(partyD);
        graph.addParty(partyE);
        graph.addParty(partyF);

        graph.addRelationship("A", "B", r1);
        graph.addRelationship("A", "C", r2);
        graph.addRelationship("B", "D", r3);
        graph.addRelationship("C", "E", r4);
        graph.addRelationship("E", "F", r5);

        // Initialize RelationshipTree
        RelationshipTree root = new RelationshipTree("A");

        // Set of parties that are "pep"
        Set<String> pepParties = new HashSet<>(Arrays.asList("F"));

        // Calculate indirect ownership and "pep" status
        root.calculateIndirectOwnershipAndPepStatus(graph, 1.0, pepParties);

        // Print the tree structure
        root.printTree("");
    }
}
