I see the issue. It looks like we need to adjust the `getDeepNestedValue` method to handle nested values correctly and return the appropriate type. Let's rewrite the method to properly handle nested null checks.

We'll change the approach to use a list of suppliers, checking each level in sequence:

### Step 1: Define the Utility Method for Deep Nested Checks

```java
import java.util.Optional;
import java.util.function.Supplier;

public class RelationshipAttributeService {

    public RelationshipAttributeDetails getRelationshipAttribute(PartyToPartyRelationship partyRelationship) {
        RelationshipAttributeDetails relationshipAttribute = new RelationshipAttributeDetails();

        // Setting attributes using the common getValue method
        relationshipAttribute.setBusinessTitle(getValue(() -> partyRelationship.getBusinessTitle()));
        relationshipAttribute.setJapanUltimateBeneficialOwnerApplicability(getValue(() -> partyRelationship.getJapanUltimateBeneficialOwnerApplicability()));
        relationshipAttribute.setPercentOfAnnualOperatingRevenueFromOwner(getValue(() -> partyRelationship.getPercentOfAnnualOperatingRevenueFromOwner()));
        relationshipAttribute.setPercentOfAnnualOperatingRevenueFromOwner(getDeepNestedValue(
            () -> partyRelationship.getPercentOfAnnualOperatingRevenueFromOwner(),
            () -> partyRelationship.getPercentOfAnnualOperatingRevenueFromOwner().getName()
        ));
        relationshipAttribute.setPercentOfBeneficialOwnership(getValue(() -> partyRelationship.getPercentOfBeneficialOwnership()));
        relationshipAttribute.setPercentOfDirectBeneficialOwnership(getDeepNestedValue(
            () -> partyRelationship.getPercentOfDirectBeneficialOwnership(),
            () -> partyRelationship.getPercentOfDirectBeneficialOwnership().getName()
        ));
        // Continue this pattern for the rest of the fields...

        return relationshipAttribute;
    }

    private <T> T getValue(Supplier<T> supplier) {
        return Optional.ofNullable(supplier.get()).orElse(null);
    }

    @SafeVarargs
    private final <T> T getDeepNestedValue(Supplier<?>... suppliers) {
        Object value = null;
        for (Supplier<?> supplier : suppliers) {
            value = supplier.get();
            if (value == null) {
                return null;
            }
        }
        return (T) value;
    }

    // Additional methods if necessary...
}
```

### Explanation

1. **getValue Method**: A simple utility to handle null checks for a single supplier.

2. **getDeepNestedValue Method**: This method takes a varargs of suppliers. It iterates over each supplier and returns null if any supplier in the sequence provides a null value. If all suppliers are non-null, it returns the value from the last supplier cast to the appropriate type.

### Usage

In the `getRelationshipAttribute` method, you use `getDeepNestedValue` for fields that require multiple levels of null checks. The suppliers are passed in sequence, and the method ensures that if any level is null, it returns null.

This implementation keeps the code clean and flexible for deep nested null checks, ensuring that any intermediate null results in a null return.


import java.util.Optional;
import java.util.function.Supplier;

public class RelationshipAttributeService {

    public RelationshipAttributeDetails getRelationshipAttribute(PartyToPartyRelationship partyRelationship) {
        RelationshipAttributeDetails relationshipAttribute = new RelationshipAttributeDetails();

        // Setting attributes using the common getValue method
        relationshipAttribute.setBusinessTitle(getValue(() -> partyRelationship.getBusinessTitle()));
        relationshipAttribute.setJapanUltimateBeneficialOwnerApplicability(getValue(() -> partyRelationship.getJapanUltimateBeneficialOwnerApplicability()));
        relationshipAttribute.setPercentOfAnnualOperatingRevenueFromOwner(getValue(() -> partyRelationship.getPercentOfAnnualOperatingRevenueFromOwner()));
        relationshipAttribute.setPercentOfAnnualOperatingRevenueFromOwner(getDeepNestedValue(
            () -> partyRelationship.getPercentOfAnnualOperatingRevenueFromOwner(),
            () -> partyRelationship.getPercentOfAnnualOperatingRevenueFromOwner().getName()
        ));
        relationshipAttribute.setPercentOfBeneficialOwnership(getValue(() -> partyRelationship.getPercentOfBeneficialOwnership()));
        relationshipAttribute.setPercentOfDirectBeneficialOwnership(getDeepNestedValue(
            () -> partyRelationship.getPercentOfDirectBeneficialOwnership(),
            () -> partyRelationship.getPercentOfDirectBeneficialOwnership().getName()
        ));
        // Continue this pattern for the rest of the fields...

        return relationshipAttribute;
    }

    private <T> T getValue(Supplier<T> supplier) {
        return Optional.ofNullable(supplier.get()).orElse(null);
    }

    @SafeVarargs
    private <T> T getDeepNestedValue(Supplier<T>... suppliers) {
        for (Supplier<T> supplier : suppliers) {
            T value = supplier.get();
            if (value == null) {
                return null;
            }
        }
        return suppliers[suppliers.length - 1].get();
    }

    // Additional methods if necessary...
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
