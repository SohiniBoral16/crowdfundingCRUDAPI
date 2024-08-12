
import java.util.List;
import java.util.stream.Collectors;

public class RelationshipGraphHierarchyBuilder {

    private final CodaQueryClient codaQueryClient;

    public RelationshipGraphHierarchyBuilder(CodaQueryClient codaQueryClient) {
        this.codaQueryClient = codaQueryClient;
    }

    private static final List<String> VISUALIZATION_DOM_ATTRIBUTES = List.of(
            "partyId", "partyNameList", "partyValidationStatus", "countryOfOrganization", "countryOfDomicile",
            "legalForm", "formationDate", "nationalIDList", "passportNumberList", "registrationIDList", "dateOfBirth",
            "relatedPartyList", "partyAlias", "globalKYCCollection", "relatedPartyList.roleParty.partyId",
            "relatedPartyList.roleParty.partyNameList", "relatedPartyList.roleParty.partyAlias",
            "relatedPartyList.roleParty.countryOfOrganization", "relatedPartyList.roleParty.legalForm"
    );

    public RelationshipGraphHierarchy buildRelationshipGraphHierarchy(List<String> partyIds) {
        List<Party> codaParties = getPartiesFromCoda(partyIds);
        return mapToRelationshipGraphHierarchy(codaParties);
    }

    private List<Party> getPartiesFromCoda(List<String> partyIds) {
        long start = System.currentTimeMillis();
        var parties = codaQueryClient.getPartiesWithAttributesPOST(partyIds, Stream.of(VISUALIZATION_DOM_ATTRIBUTES)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        log.info("getPartyVisualizationById returned in {} ms.", System.currentTimeMillis() - start);
        return parties;
    }

    private RelationshipGraphHierarchy mapToRelationshipGraphHierarchy(List<Party> codaParties) {
        RelationshipGraphHierarchy relationshipGraph = RelationshipGraphHierarchy.builder().build();

        // Step 1: Convert each CODA Party to the internal Party model and add to the graph
        for (Party codaParty : codaParties) {
            Party internalParty = convertToInternalPartyModel(codaParty);
            relationshipGraph.addParty(internalParty);
        }

        // Step 2: Iterate over each Party and establish relationships
        for (Party codaParty : codaParties) {
            String parentPartyId = codaParty.getPartyId();
            for (CodaRelatedParty relatedParty : codaParty.getRelatedParties()) { // Assuming a related party model
                String childPartyId = relatedParty.getPartyId();
                Relationship relationship = new Relationship(convertToInternalPartyModel(relatedParty), relatedParty.getRole());
                relationshipGraph.addRelationship(parentPartyId, childPartyId, relationship);
            }
        }

        return relationshipGraph;
    }

    private Party convertToInternalPartyModel(Party codaParty) {
        return Party.builder()
                .partyId(codaParty.getPartyId())
                .partyName(codaParty.getPartyName())
                .validationStatus(codaParty.getValidationStatus())
                .countryOfOrganization(codaParty.getCountryOfOrganization())
                .legalForm(codaParty.getLegalForm())
                .partyAlias(codaParty.getPartyAlias())
                .dateOfBirth(codaParty.getDateOfBirth())
                .dateOfIncorporation(codaParty.getDateOfIncorporation())
                .countrySpecificIdentifiers(codaParty.getCountrySpecificIdentifiers())
                .pepIndicator(codaParty.getPepIndicator())
                .build();
    }
}

-------------------
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collection;

private List<Party> getPartiesFromCoda(List<String> partyIds) {
    log.info("getPartyVisualizationById for party={}", partyIds);

    var parties = codaQueryClient.getPartiesWithAttributesPOST(partyIds, Stream.of(VISUALIZATION_DOM_ATTRIBUTES)
            .flatMap(Collection::stream) // Use Collection::stream to flatten the list
            .collect(Collectors.toList()));

    return parties;
}


package com.ms.clientData.p2pservice.model.visualization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Party {
    private String partyId;
    private String partyName;
    private String validationStatus;
    private String countryOfOrganization;
    private String legalForm;
    private String partyAlias;
    private String legalName;
    private String countryOfDomicile;
    private String dateOfBirth;
    private String dateOfIncorporation;
    private List<String> countrySpecificIdentifiers = new ArrayList<>();
    private String pepIndicator;
    private List<Relationship> relationships = new ArrayList<>();

    public Party(String partyId) {
        this.partyId = partyId;
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public void addRelationships(List<Relationship> relationships) {
        this.relationships.addAll(relationships);
    }
}

package com.ms.clientData.p2pservice.model.visualization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    private String relationshipType;
    private Float directOwnershipPercentageValue;
    private Float indirectOwnershipPercentageValue;
    private Float ownershipRange;
    private Boolean significantInfluenceOverIndicator;
    private String economicDependenceFactor;
    private Boolean revocableTrustIndicator;
    private String businessTitle;
    private Party childParty;

    public Relationship(Party childParty, String relationshipType) {
        this.relationshipType = relationshipType;
        this.childParty = childParty;
    }
}


package com.ms.clientData.p2pservice.model.visualization;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RelationshipTreeHierarchy {
    private String partyId;
    private Map<String, List<RelationshipTreeHierarchy>> childrenByType = new HashMap<>();

    public RelationshipTreeHierarchy(String partyId) {
        this.partyId = partyId;
        this.childrenByType = new HashMap<>();
    }
}


package com.ms.clientData.p2pservice.model.visualization;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class RelationshipGraphHierarchy {
    private final Map<String, Party> partyLookup = new HashMap<>();

    public void addParty(Party party) {
        partyLookup.put(party.getPartyId(), party);
    }

    public RelationshipTreeHierarchy addRelationship(String parentPartyId, String childPartyId, Relationship relationship) {
        var parentParty = partyLookup.get(parentPartyId);
        var childParty = partyLookup.get(childPartyId);
        
        relationship.setChildParty(childParty);
        parentParty.addRelationship(relationship);

        RelationshipTreeBuilder treeBuilder = new RelationshipTreeBuilder(partyLookup);
        return treeBuilder.buildRelationshipTreeHierarchy(parentPartyId);
    }
}


package com.ms.clientData.p2pservice.model.visualization;

import java.util.ArrayList;
import java.util.Map;

public class RelationshipTreeBuilder {
    private final Map<String, Party> partyLookup;

    public RelationshipTreeBuilder(Map<String, Party> partyLookup) {
        this.partyLookup = partyLookup;
    }

    public RelationshipTreeHierarchy buildRelationshipTreeHierarchy(String rootPartyId) {
        var rootParty = partyLookup.get(rootPartyId);
        var relationshipTreeHierarchy = new RelationshipTreeHierarchy(rootParty.getPartyId());
        
        buildTreeRecursively(rootParty, relationshipTreeHierarchy);
        
        return relationshipTreeHierarchy;
    }

    private void buildTreeRecursively(Party party, RelationshipTreeHierarchy treeHierarchy) {
        for (var relationship : party.getRelationships()) {
            var childTreeHierarchy = new RelationshipTreeHierarchy(relationship.getChildParty().getPartyId());
            
            treeHierarchy.getChildrenByType().computeIfAbsent(
                relationship.getRelationshipType(),
                k -> new ArrayList<>()
            ).add(childTreeHierarchy);
            
            buildTreeRecursively(relationship.getChildParty(), childTreeHierarchy);
        }
    }
}

package com.ms.clientData.p2pservice;

import com.ms.clientData.p2pservice.model.visualization.*;

public class Main {
    public static void main(String[] args) {
        // Initialize parties
        Party partyA = new Party("A");
        Party partyB = new Party("B");
        Party partyC = new Party("C");

        // Create graph and add parties
        RelationshipGraphHierarchy graph = RelationshipGraphHierarchy.builder().build();
        graph.addParty(partyA);
        graph.addParty(partyB);
        graph.addParty(partyC);

        // Add a relationship and get the RelationshipTreeHierarchy
        Relationship r1 = new Relationship(partyB, "Parent-Child");
        RelationshipTreeHierarchy treeHierarchy = graph.addRelationship("A", "B", r1);

        // Print the tree hierarchy (or process it further)
        System.out.println(treeHierarchy);
    }
}



----------------------------------------
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
