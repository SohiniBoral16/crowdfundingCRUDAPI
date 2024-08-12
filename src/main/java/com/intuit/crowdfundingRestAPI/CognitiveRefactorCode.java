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
