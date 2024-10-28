
public List<P2PVisualization> filterNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties, String requiredRelationshipTypeId) {
    return p2pVisualizationParties.stream()
        // Filter out parties that only have non-ownership relationships
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()
            && (party.getOwnershipRelationships() == null || party.getOwnershipRelationships().isEmpty()))
        // Further filter non-ownership relationships by relationshipTypeId
        .map(party -> {
            List<P2PRelationship> filteredNonOwnerships = party.getNonOwnershipRelationships().stream()
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails() != null)
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .anyMatch(detail -> requiredRelationshipTypeId.equals(detail.getRelationshipTypeId())))
                .collect(Collectors.toList());

            // Build a new P2PVisualization instance with the filtered non-ownership relationships
            return P2PVisualization.builder()
                .partyId(party.getPartyId())
                .parentId(party.getParentId())
                .partyName(party.getPartyName())
                .validationStatus(party.getValidationStatus())
                .countryOfOrganization(party.getCountryOfOrganization())
                .legalForm(party.getLegalForm())
                .legalName(party.getLegalName())
                .pepIndicator(party.getPepIndicator())
                .ownershipRelationships(party.getOwnershipRelationships())
                .nonOwnershipRelationships(filteredNonOwnerships) // Set the filtered non-ownership relationships
                .build();
        })
        // Ensure we return only parties with at least one matching relationship in nonOwnershipRelationships
        .filter(party -> !party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());
}




public List<P2PVisualization> filterNonOwnershipRelationships(List<P2PVisualization> p2pVisualizationParties, String requiredRelationshipTypeId) {
    return p2pVisualizationParties.stream()
        // Filter out parties that only have non-ownership relationships
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty()
            && (party.getOwnershipRelationships() == null || party.getOwnershipRelationships().isEmpty()))
        // Further filter non-ownership relationships by relationshipTypeId
        .map(party -> {
            List<P2PRelationship> filteredNonOwnerships = party.getNonOwnershipRelationships().stream()
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails() != null)
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .anyMatch(detail -> requiredRelationshipTypeId.equals(detail.getRelationshipTypeId())))
                .collect(Collectors.toList());

            // Set the filtered non-ownership relationships back to the party
            party.setNonOwnershipRelationships(filteredNonOwnerships);
            return party;
        })
        // Ensure we return only parties with at least one matching relationship in nonOwnershipRelationships
        .filter(party -> !party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());
}


-------------------

import java.util.List;
import java.util.stream.Collectors;

public List<P2PVisualization> filterNonOwnershipPartiesByRelationshipTypeId(List<P2PVisualization> p2pVisualizationParties, String relationshipTypeId) {
    return p2pVisualizationParties.stream()
        // Filter out parties with nonOwnershipRelationships only
        .filter(party -> party.getNonOwnershipRelationships() != null && !party.getNonOwnershipRelationships().isEmpty())
        .map(party -> {
            // Filter nonOwnershipRelationships based on relationshipTypeId
            List<P2PRelationship> filteredNonOwnershipRelationships = party.getNonOwnershipRelationships().stream()
                .filter(nonOwnership -> nonOwnership.getRelationshipDetails().stream()
                    .anyMatch(detail -> relationshipTypeId.equals(detail.getRelationshipTypeId())))
                .collect(Collectors.toList());
            
            // Create a new P2PVisualization object with filtered nonOwnershipRelationships
            P2PVisualization filteredParty = new P2PVisualization(party);
            filteredParty.setNonOwnershipRelationships(filteredNonOwnershipRelationships);
            return filteredParty;
        })
        // Ensure only parties with the specified relationshipTypeId remain
        .filter(party -> !party.getNonOwnershipRelationships().isEmpty())
        .collect(Collectors.toList());
}
