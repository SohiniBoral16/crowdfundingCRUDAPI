public List<PartyToPartyRelationship> toOOMPartyToPartyRelationship(List<P2PPartyToPartyRelationship> relatedParties) {
    List<PartyToPartyRelationship> relatedPartyList = new ArrayList<>();
    DTOControl control = new DTOControl();

    if (Objects.nonNull(relatedParties) && !relatedParties.isEmpty()) {
        for (P2PPartyToPartyRelationship relatedParty : relatedParties) {
            PartyToPartyRelationship p2pRelationship = new MutablePartyToPartyRelationship();
            processBasicDetails(control, relatedParty, p2pRelationship);
            processAttributes(control, relatedParty, p2pRelationship);
            processEmployerDetails(control, relatedParty, p2pRelationship);
            processOwnershipDetails(control, relatedParty, p2pRelationship);
            relatedPartyList.add(p2pRelationship);
        }
    }

    return relatedPartyList;
}

private void processBasicDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    p2pRelationship.setBusinessRole(relatedParty.getBusinessRole());
    p2pRelationship.setLegalEntity(relatedParty.getLegalEntity());
    p2pRelationship.setBeneficiary(relatedParty.getBeneficiary());
    p2pRelationship.setRoleName(relatedParty.getRoleName());
    p2pRelationship.setPersonName(relatedParty.getPersonName());
    p2pRelationship.setRelationType(relatedParty.getRelationType());
    p2pRelationship.setEffectiveDate(relatedParty.getEffectiveDate());
    p2pRelationship.setEndDate(relatedParty.getEndDate());
    p2pRelationship.setDocument(relatedParty.getDocument());
}

private void processAttributes(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    if (relatedParty.getInfluenceOver() != null) {
        control.addNullAttribute("influenceOver");
        p2pRelationship.setInfluenceOver(relatedParty.getInfluenceOver());
    } else {
        p2pRelationship.setInfluenceOver(control.getInfluenceOverIndicator());
    }

    if (relatedParty.getRevocableTrustIndication() != null) {
        control.addNullAttribute("revocableTrustIndication");
        p2pRelationship.setRevocableTrustIndication(relatedParty.getRevocableTrustIndication());
    } else {
        p2pRelationship.setRevocableTrustIndication(control.getRevocableTrustIndication());
    }

    if (relatedParty.getPercentageValueOfOwnership() != null) {
        control.addNullAttribute("percentageValueOfOwnership");
        p2pRelationship.setPercentageValueOfOwnership(relatedParty.getPercentageValueOfOwnership());
    } else {
        p2pRelationship.setPercentageValueOfOwnership(control.getPercentageValueOfOwnership());
    }
}

private void processEmployerDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    if (relatedParty.getRolePartyEmployeeTitle() != null) {
        if (Objects.isNull(relatedParty.getRolePartyEmployeeTitle().getId())) {
            control.addNullAttribute("rolePartyEmployeeTitle");
            EmployeeTitle employeeTitle = new MutableEmployeeTitle();
            employeeTitle.setCode(control.getRolePartyEmployeeTitle().getCode());
            p2pRelationship.setRolePartyEmployeeTitle(employeeTitle);
        } else {
            p2pRelationship.setRolePartyEmployeeTitle(relatedParty.getRolePartyEmployeeTitle());
        }
    }
}

private void processOwnershipDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    if (relatedParty.getPercentBeneficialOwner() != null) {
        PercentBeneficialOwner percentBeneficialOwner = new LazyPercentBeneficialOwner();
        percentBeneficialOwner.setTo(relatedParty.getPercentBeneficialOwner().getId());
        p2pRelationship.setPercentBeneficialOwner(percentBeneficialOwner);
    }

    if (relatedParty.getPercentDirectInvestorBeneficialOwner() != null) {
        PercentDirectInvestorBeneficialOwner percentDirectInvestorBeneficialOwner = new LazyPercentDirectInvestorBeneficialOwner();
        percentDirectInvestorBeneficialOwner.setTo(relatedParty.getPercentDirectInvestorBeneficialOwner().getId());
        p2pRelationship.setPercentDirectInvestorBeneficialOwner(percentDirectInvestorBeneficialOwner);
    }

    if (relatedParty.getPercentIntermediaryBeneficialOwner() != null) {
        PercentIntermediaryBeneficialOwner percentIntermediaryBeneficialOwner = new LazyPercentIntermediaryBeneficialOwner();
        percentIntermediaryBeneficialOwner.setTo(relatedParty.getPercentIntermediaryBeneficialOwner().getId());
        p2pRelationship.setPercentIntermediaryBeneficialOwner(percentIntermediaryBeneficialOwner);
    }

    if (relatedParty.getVerificationMethodJapanUltimateBeneficialOwner() != null) {
        VerificationMethodJapanUltimateBeneficialOwner verificationMethod = new LazyVerificationMethodJapanUltimateBeneficialOwner();
        verificationMethod.setTo(relatedParty.getVerificationMethodJapanUltimateBeneficialOwner().getId());
        p2pRelationship.setVerificationMethodJapanUltimateBeneficialOwner(verificationMethod);
    }
}
