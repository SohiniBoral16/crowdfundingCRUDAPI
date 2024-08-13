
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
