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
private void processOwnershipDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    Optional.ofNullable(relatedParty.getPercentageValueOfOwnership())
            .ifPresentOrElse(
                    value -> {
                        if (relatedParty.getPercentageValueOfBeneficialOwnership() == null) {
                            control.addNullAttribute("percentageValueOfOwnership");
                        } else if (relatedParty.getPercentageValueOfOwnership() > 100) {
                            throw new IllegalArgumentException("Ownership or HSBAL Direct Beneficiary must not be more than 100%");
                        } else {
                            p2pRelationship.setPercentageValueOfOwnership(value);
                        }
                    },
                    () -> p2pRelationship.setPercentageValueOfOwnership(
                            Optional.ofNullable(relatedParty.getPercentageValueOfBeneficialOwnership())
                                    .orElse(0.0)  // Default to 0.0 or handle as needed
                    )
            );

    Optional.ofNullable(relatedParty.getJapanUltimateBeneficialOwnerApplicability())
            .ifPresent(applicability -> {
                JapanUltimateBeneficialOwnerApplicabilityReason reason = new LazyJapanUltimateBeneficialOwnerApplicabilityReason();
                reason.setTo(applicability.getId());
                p2pRelationship.setJapanUltimateBeneficialOwnerApplicabilityReason(reason);
            });
}

private void processOwnershipDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    handlePercentageValueOfOwnership(control, relatedParty, p2pRelationship);
    handleHSBALDirectBeneficiaryOwner(control, relatedParty, p2pRelationship);
    handleRolePartyEmployeeTitle(control, relatedParty, p2pRelationship);
}

private void handlePercentageValueOfOwnership(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    Optional.ofNullable(relatedParty.getPercentageValueOfOwnership())
            .ifPresentOrElse(
                    value -> {
                        if (relatedParty.getPercentageValueOfOwnership() > 100) {
                            throw new IllegalArgumentException("Ownership or HSBAL Direct Beneficiary must not be more than 100%");
                        } else {
                            p2pRelationship.setPercentageValueOfOwnership(value);
                        }
                    },
                    () -> control.addNullAttribute("percentageValueOfOwnership")
            );
}

private void handleHSBALDirectBeneficiaryOwner(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    if (P2P_HSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID.equals(p2pRelationship.getPartyRelationshipType().getID())) {
        handlePercentageValueOfOwnership(control, relatedParty, p2pRelationship);
    }
}

private void handleRolePartyEmployeeTitle(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    if (relatedParty.getRolePartyEmployeeTitle() != null) {
        if (ObjectUtils.isEmpty(relatedParty.getRolePartyEmployeeTitle().getId())) {
            control.addNullAttribute("rolePartyEmployeeTitle");
        } else {
            EmployeeTitle employeeTitle = new MutableEmployeeTitle();
            if (ObjectUtils.isEmpty(relatedParty.getRolePartyEmployeeTitle().getEmployeeTitleGroup().getId())) {
                EmployeeTitleGroupCode employeeTitleGroupCode = new LazyEmployeeTitleGroupCode();
                employeeTitleGroupCode.setCode(relatedParty.getRolePartyEmployeeTitle().getEmployeeTitleGroup().getCode());
                employeeTitle.setEmployeeTitleGroupCode(employeeTitleGroupCode);
            }
            employeeTitle.setCode(relatedParty.getRolePartyEmployeeTitle().getCode());
            p2pRelationship.setRolePartyEmployeeTitle(employeeTitle);
        }
    }
}
