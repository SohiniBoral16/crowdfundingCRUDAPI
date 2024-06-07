private void processMSBALDirectOwnershipDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, 
    PartyToPartyRelationship p2pRelationship) {

    String relationshipIdOfMSBALDirectBeneficialOwnerOf = P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID;
    
    if (isDirectBeneficialOwnerRelationship(p2pRelationship.getPartyRelationshipType().getID(), relationshipIdOfMSBALDirectBeneficialOwnerOf)) {
        handleOwnershipPercentage(control, relatedParty, p2pRelationship);
    } else {
        p2pRelationship.setPercentageValueOfOwnership(relatedParty.getPercentageValueOfOwnership());
    }

    control.addNullAttribute("percentageValueOfOwnership");
}

private boolean isDirectBeneficialOwnerRelationship(String relationshipTypeId, String directBeneficialOwnerId) {
    return directBeneficialOwnerId.equals(relationshipTypeId);
}

private void handleOwnershipPercentage(DTOControl control, P2PPartyToPartyRelationship relatedParty, 
    PartyToPartyRelationship p2pRelationship) {

    Optional.ofNullable(relatedParty.getPercentageValueOfOwnership()).ifPresentOrElse(percentageValueOfOwnership -> {
        if (percentageValueOfOwnership > 100) {
            throw new IllegalArgumentException("Ownership of MSBAL Direct Beneficiary must not be more than 100%");
        }
        p2pRelationship.setPercentageValueOfOwnership(percentageValueOfOwnership);
    }, () -> control.addNullAttribute("percentageValueOfOwnership"));
}
