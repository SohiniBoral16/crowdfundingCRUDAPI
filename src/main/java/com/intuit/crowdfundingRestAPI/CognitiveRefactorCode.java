public RelationshipGraphHierarchy buildRelationshipGraphHierarchy(List<String> partyIds) {
    Set<String> processedParties = new HashSet<>();
    RelationshipGraphHierarchy hierarchy = new RelationshipGraphHierarchy();

    for (String partyId : partyIds) {
        if (!processedParties.contains(partyId)) {
            Party rootParty = getPartiesFromCoda(Collections.singletonList(partyId)).get(0);
            PartyForTreeStructure rootPartyDetails = buildHierarchyForParty(rootParty, processedParties, hierarchy);
            hierarchy.addParty(rootPartyDetails);
        }
    }

    return hierarchy;
}

private PartyForTreeStructure buildHierarchyForParty(Party party, Set<String> processedParties, RelationshipGraphHierarchy hierarchy) {
    String partyId = party.getPartyID();

    // If the party is already processed, return the existing PartyForTreeStructure
    if (processedParties.contains(partyId)) {
        return hierarchy.getPartyLookup().get(partyId); // Reuse the existing structure
    }

    // Mark this party as processed
    processedParties.add(partyId);

    // Build the base DTO for the current party
    PartyForTreeStructure partyDetails = getPartyDTO(party);
    hierarchy.addParty(partyDetails); // Add to the hierarchy

    // Fetch related parties and recursively build their hierarchies
    List<PartyToPartyRelationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());

    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        Relationship relationship = getRelationshipDTO(partyRelationship);
        String childPartyId = partyRelationship.getRelatedPartyId();

        if (childPartyId != null) {
            if (!processedParties.contains(childPartyId)) {
                List<Party> childParties = getPartiesFromCoda(Collections.singletonList(childPartyId));

                if (!childParties.isEmpty()) {
                    Party childParty = childParties.get(0);
                    PartyForTreeStructure childPartyDetails = buildHierarchyForParty(childParty, processedParties, hierarchy);

                    if (childPartyDetails != null) {
                        relationship.setChildParty(childPartyDetails);
                        partyDetails.addRelationship(relationship);
                    }
                }
            } else {
                // If the child party is already processed, link the existing structure
                PartyForTreeStructure existingChildPartyDetails = hierarchy.getPartyLookup().get(childPartyId);
                relationship.setChildParty(existingChildPartyDetails);
                partyDetails.addRelationship(relationship);
            }
        }
    }

    return partyDetails;
}



public RelationshipGraphHierarchy buildRelationshipGraphHierarchy(List<String> partyIds) {
    Set<String> processedParties = new HashSet<>();
    RelationshipGraphHierarchy hierarchy = new RelationshipGraphHierarchy();

    for (String partyId : partyIds) {
        Party rootParty = getPartiesFromCoda(Collections.singletonList(partyId)).get(0);
        PartyForTreeStructure rootPartyDetails = buildHierarchyForParty(rootParty, processedParties);
        hierarchy.addParty(rootPartyDetails);
    }

    return hierarchy;
}

private PartyForTreeStructure buildHierarchyForParty(Party party, Set<String> processedParties) {
    String partyId = party.getPartyID();

    if (processedParties.contains(partyId)) {
        return null;
    }

    processedParties.add(partyId);

    PartyForTreeStructure partyDetails = getPartyDTO(party);

    List<PartyToPartyRelationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());

    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        Relationship relationship = getRelationshipDTO(partyRelationship);
        String childPartyId = partyRelationship.getRelatedPartyId();

        if (childPartyId != null) {
            List<Party> childParties = getPartiesFromCoda(Collections.singletonList(childPartyId));

            if (!childParties.isEmpty()) {
                Party childParty = childParties.get(0);
                PartyForTreeStructure childPartyDetails = buildHierarchyForParty(childParty, processedParties);

                if (childPartyDetails != null) {
                    hierarchy.addParty(childPartyDetails);
                    hierarchy.addRelationship(partyId, childPartyId, relationship);
                }
            }
        }
    }

    return partyDetails;
}
------------------------------------


public RelationshipGraphHierarchy buildRelationshipGraphHierarchy(List<String> partyIds) {
    // Use a set to track processed parties to avoid infinite loops
    Set<String> processedParties = new HashSet<>();
    RelationshipGraphHierarchy hierarchy = new RelationshipGraphHierarchy();

    // Process each top-level party
    for (String partyId : partyIds) {
        Party rootParty = getPartiesFromCoda(Collections.singletonList(partyId)).get(0);
        PartyForTreeStructure rootPartyDetails = buildHierarchyForParty(rootParty, processedParties);
        hierarchy.addParty(rootPartyDetails); // Assuming RelationshipGraphHierarchy has this method
    }

    return hierarchy;
}

private PartyForTreeStructure buildHierarchyForParty(Party party, Set<String> processedParties) {
    String partyId = party.getPartyID();

    // If the party is already processed, return null to avoid infinite loops
    if (processedParties.contains(partyId)) {
        return null;
    }

    // Mark this party as processed
    processedParties.add(partyId);

    // Build the base DTO for the current party
    PartyForTreeStructure partyDetails = getPartyDTO(party);

    // Fetch related parties and recursively build their hierarchies
    List<PartyToPartyRelationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());
    log.info("Related parties for {}: {}", partyId, relatedPartyList);

    List<Relationship> relationships = new ArrayList<>();
    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        // Map relationship attributes
        Relationship relationship = getRelationshipDTO(partyRelationship);

        // Get the child party ID from the relationship
        String childPartyId = partyRelationship.getRelatedPartyId(); // Assuming this method exists

        if (childPartyId != null) {
            // Fetch the child party from Coda
            List<Party> childParties = getPartiesFromCoda(Collections.singletonList(childPartyId));

            if (!childParties.isEmpty()) {
                Party childParty = childParties.get(0);

                // Recursively build the hierarchy for the child party
                PartyForTreeStructure childPartyDetails = buildHierarchyForParty(childParty, processedParties);
                
                // Only add the relationship if the child party was not already processed
                if (childPartyDetails != null) {
                    relationship.setChildParty(childPartyDetails);
                    relationships.add(relationship); // Add the relationship to the list
                }
            }
        }
    }

    partyDetails.setRelationships(relationships);
    return partyDetails;
}



----++++++++++++++++++++++++++++++++++
public PartyForTreeStructure buildRelationshipGraphHierarchy(List<String> partyIds) {
    // Fetch top-level parties
    List<Party> parties = getPartiesFromCoda(partyIds);
    log.info("Top-level parties: {}", parties);

    PartyForTreeStructure rootPartyDetails = null;

    // Iterate over top-level parties and build their hierarchy
    for (Party codaParty : parties) {
        rootPartyDetails = buildHierarchyForParty(codaParty);
    }

    return rootPartyDetails;
}

private PartyForTreeStructure buildHierarchyForParty(Party party) {
    // Build the base DTO for the current party
    PartyForTreeStructure partyDetails = getPartyDTO(party);

    // Fetch related parties and recursively build their hierarchies
    List<PartyToPartyRelationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());
    log.info("Related parties for {}: {}", party.getPartyID(), relatedPartyList);

    List<Relationship> relationships = new ArrayList<>();
    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        // Map relationship attributes
        Relationship relationship = getRelationshipDTO(partyRelationship);

        // Get the child party ID from the relationship
        String childPartyId = partyRelationship.getRelatedPartyId(); // Assuming this method exists

        if (childPartyId != null) {
            // Fetch the child party from Coda
            List<String> childPartyIds = Collections.singletonList(childPartyId);
            List<Party> childParties = getPartiesFromCoda(childPartyIds);

            if (!childParties.isEmpty()) {
                Party childParty = childParties.get(0);

                // Recursively build the hierarchy for the child party
                PartyForTreeStructure childPartyDetails = buildHierarchyForParty(childParty);
                relationship.setChildParty(childPartyDetails);  // Link the child details
            }
        }

        relationships.add(relationship); // Add the relationship to the list
    }

    partyDetails.setRelationships(relationships);
    return partyDetails;
}



private PartyForTreeStructure buildHierarchyForParty(Party party) {
    PartyForTreeStructure partyDetails = getPartyDTO(party);

    // Fetch related parties and recursively build their hierarchies
    List<PartyToPartyRelationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());
    log.info("relatedPartyList: {}", relatedPartyList);

    List<Relationship> relationships = new ArrayList<>();
    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        // Here we assume that the related party also has its own hierarchy
        Relationship relationship = getRelationshipDTO(partyRelationship);

        // Get the child party ID from the relationship
        String childPartyId = partyRelationship.getRelatedPartyId(); // Assuming this method exists

        if (childPartyId != null) {
            // Fetch the child party from Coda
            List<String> childPartyIds = Collections.singletonList(childPartyId);
            List<Party> childParties = getPartiesFromCoda(childPartyIds);

            if (!childParties.isEmpty()) {
                Party childParty = childParties.get(0);
                
                // Recursively build the hierarchy for the child party
                PartyForTreeStructure childPartyDetails = buildHierarchyForParty(childParty);
                relationship.setChildParty(childPartyDetails);  // Assuming you want to link the child details
            }
        }

        relationships.add(relationship);
    }

    partyDetails.setRelationships(relationships);
    return partyDetails;
}




private PartyForTreeStructure buildHierarchyForParty(Party party) {
    PartyForTreeStructure partyDetails = getPartyDTO(party);

    // Fetch related parties and recursively build their hierarchies
    List<PartyToPartyRelationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());
    log.info("relatedPartyList: {}", relatedPartyList);

    List<Relationship> relationships = new ArrayList<>();
    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        // Here we assume that the related party also has its own hierarchy
        Relationship relationship = getRelationshipDTO(partyRelationship);

        // Get the child party ID from the relationship
        String childPartyId = partyRelationship.getRelatedPartyId(); // Assuming this method exists

        if (childPartyId != null) {
            // Fetch the child party from Coda
            Party childParty = fetchPartyById(childPartyId);

            // Recursively build the hierarchy for the child party
            PartyForTreeStructure childPartyDetails = buildHierarchyForParty(childParty);
            relationship.setChildParty(childPartyDetails);  // Assuming you want to link the child details
        }

        relationships.add(relationship);
    }

    partyDetails.setRelationships(relationships);
    return partyDetails;
}




private Party getRelatedParty(PartyToPartyRelationship partyRelationship) {
    // Assuming there is a method or field in PartyToPartyRelationship that gives the related party ID
    String relatedPartyId = partyRelationship.getRelatedPartyId(); // Replace with actual method/field
    
    // Now use this ID to fetch the related Party object
    return fetchPartyById(relatedPartyId);
}

// You might have a method like this to fetch the Party by its ID
private Party fetchPartyById(String partyId) {
    // Logic to fetch the Party object by its ID, possibly querying a database or calling a service
    return partyService.getPartyById(partyId); // Replace with your actual data fetching logic
}



public PartyForTreeStructure buildRelationshipGraphHierarchy(List<String> partyIds) {
    List<Party> parties = getPartiesFromCoda(partyIds);
    log.info("code Parties: {}", parties);

    PartyForTreeStructure rootPartyDetails = null;

    for (Party codaParty : parties) {
        rootPartyDetails = buildHierarchyForParty(codaParty);
    }

    return rootPartyDetails;
}

private PartyForTreeStructure buildHierarchyForParty(Party party) {
    PartyForTreeStructure partyDetails = getPartyDTO(party);

    // Fetch related parties and recursively build their hierarchies
    List<PartyToPartyRelationship> relatedPartyList = party.getRelatedPartyList().stream().collect(Collectors.toList());
    log.info("relatedPartyList: {}", relatedPartyList);

    List<Relationship> relationships = new ArrayList<>();
    for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
        // Here we assume that the related party also has its own hierarchy
        Relationship relationship = getRelationshipDTO(partyRelationship);

        // Assuming that partyRelationship can give you the child Party ID or object
        Party relatedParty = getRelatedParty(partyRelationship);
        if (relatedParty != null) {
            PartyForTreeStructure childPartyDetails = buildHierarchyForParty(relatedParty);
            relationship.setChildParty(childPartyDetails);  // Assuming you want to link the child details
        }

        relationships.add(relationship);
    }

    partyDetails.setRelationships(relationships);
    return partyDetails;
}


-------------++------------
package com.ms.clientData.p2pservice.service;

import com.ms.clientData.p2pservice.model.coda.Party as CodaParty;
import com.ms.clientData.p2pservice.model.visualization.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collection;

public class RelationshipGraphHierarchyBuilder {

    private final CodaQueryClient codaQueryClient;

    public RelationshipGraphHierarchyBuilder(CodaQueryClient codaQueryClient) {
        this.codaQueryClient = codaQueryClient;
    }

    public RelationshipGraphHierarchy buildRelationshipGraphHierarchy(List<String> partyIds) {
        List<CodaParty> codaParties = getPartiesFromCoda(partyIds);
        return mapToRelationshipGraphHierarchy(codaParties);
    }

    private List<CodaParty> getPartiesFromCoda(List<String> partyIds) {
        long start = System.currentTimeMillis();
        var parties = codaQueryClient.getPartiesWithAttributesPOST(partyIds, Stream.of(VISUALIZATION_DOM_ATTRIBUTES)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        log.info("getPartyVisualizationById returned in {} ms.", System.currentTimeMillis() - start);
        return parties;
    }

    private RelationshipGraphHierarchy mapToRelationshipGraphHierarchy(List<CodaParty> codaParties) {
        RelationshipGraphHierarchy relationshipGraph = RelationshipGraphHierarchy.builder().build();

        // Add Parties to the graph
        for (CodaParty codaParty : codaParties) {
            Party internalParty = convertToInternalPartyModel(codaParty);
            relationshipGraph.addParty(internalParty);
        }

        // Establish Relationships
        for (CodaParty codaParty : codaParties) {
            String parentPartyId = codaParty.getPartyId();
            for (CodaParty.Relationship codaRelationship : codaParty.getRelatedPartyList()) {
                String childPartyId = codaRelationship.getPartyId();
                Relationship relationship = new Relationship(convertToInternalPartyModel(codaRelationship), codaRelationship.getRole());
                relationshipGraph.addRelationship(parentPartyId, childPartyId, relationship);
            }
        }

        return relationshipGraph;
    }

    private Party convertToInternalPartyModel(CodaParty codaParty) {
        return Party.builder()
                .partyId(codaParty.getPartyId())
                .partyName(codaParty.getPartyNameList().isEmpty() ? null : codaParty.getPartyNameList().get(0))
                .validationStatus(codaParty.getPartyValidationStatus().name())
                .countryOfOrganization(codaParty.getCountryOfOrganization() != null ? codaParty.getCountryOfOrganization().name() : null)
                .legalForm(codaParty.getLegalForm() != null ? codaParty.getLegalForm().name() : null)
                .partyAlias(codaParty.getPartyAlias())
                .dateOfBirth(codaParty.getDateOfBirth())
                .dateOfIncorporation(codaParty.getFormationDate())
                .countrySpecificIdentifiers(codaParty.getNationalIDList().stream()
                        .map(nationalId -> new CountrySpecificIdentifier(nationalId.getId(), nationalId.getCountryCode()))
                        .collect(Collectors.toList()))
                .pepIndicator(codaParty.getPartyType().isPepIndicator() ? "Yes" : "No")
                .build();
    }
}
