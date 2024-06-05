public class P2POMTransformer {

    private DTOControl control = new DTOControl();

    public List<PartyToPartyRelationship> toP2PPartyToPartyRelationship(List<P2PPartyToPartyRelationship> relatedParties) {
        List<PartyToPartyRelationship> relatedPartyList = new ArrayList<>();

        if (Objects.isNull(relatedParties) || relatedParties.isEmpty()) {
            return relatedPartyList;
        }

        for (P2PPartyToPartyRelationship relatedParty : relatedParties) {
            MutablePartyToPartyRelationship p2pRelationship = new MutablePartyToPartyRelationship();
            setBasicAttributes(p2pRelationship, relatedParty);
            setOwnershipPercentage(p2pRelationship, relatedParty);
            setEmployeeTitle(p2pRelationship, relatedParty);
            setRevocableTrustIndicator(p2pRelationship, relatedParty);
            setEmployeeTitleRole2(p2pRelationship, relatedParty);
            setDependenceFactor(p2pRelationship, relatedParty);
            setAnnualOperatingCost(p2pRelationship, relatedParty);
            setBeneficialOwnership(p2pRelationship, relatedParty);
            setConfirmationMethod(p2pRelationship, relatedParty);
            relatedPartyList.add(p2pRelationship);
        }

        return relatedPartyList;
    }

    private void setBasicAttributes(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        p2pRelationship.setBusinessInitiative(relatedParty.getBusinessInitiative().getID());
        p2pRelationship.setPersonInChargeVerificationLetterReceivedDate(relatedParty.getPersonInChargeVerificationLetterReceivedDate());
        p2pRelationship.setRoleType(relatedParty.getRoleType().getRoleType().getID());
        p2pRelationship.setRoleParty(relatedParty.getRoleParty().getParty().getPartyID());
    }

    private void setOwnershipPercentage(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (!isDirectBeneficiaryOwnerOfRelationship(p2pRelationship, relatedParty)) {
            return;
        }
        if (relatedParty.getPercentageValueOfOwnership() == null) {
            control.addNullAttribute("percentageValueOfOwnership");
        } else if (relatedParty.getPercentageValueOfOwnership() > 100) {
            throw new IllegalArgumentException("Ownership of MSBAL Direct Beneficiary must not be more than 100%");
        } else {
            p2pRelationship.setPercentageValueOfOwnership(relatedParty.getPercentageValueOfOwnership());
        }
    }

    private boolean isDirectBeneficiaryOwnerOfRelationship(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        return "PP2MSBALDirectBeneficiaryOwnerOfRelationship".equals(p2pRelationship.getPartyRelationshipType().getID());
    }

    private void setEmployeeTitle(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (relatedParty.getRolePartyEmployeeTitle() == null) {
            control.addNullAttribute("rolePartyEmployeeTitle");
            return;
        }

        JobTitleList jobTitleList = relatedParty.getRolePartyEmployeeTitle();
        if (Objects.isNull(jobTitleList.getEmployeeTitle().getID())) {
            control.addNullAttribute("rolePartyEmployeeTitle");
            return;
        }

        MutableEmployeeTitle employeeTitle = new MutableEmployeeTitle();
        employeeTitle.setCode(jobTitleList.getEmployeeTitle().getCode());
        employeeTitle.setGroupCode(jobTitleList.getEmployeeTitle().getGroupCode().getCode());
        employeeTitle.setDescription(jobTitleList.getEmployeeTitle().getDescription().getText());
        p2pRelationship.setRolePartyEmployeeTitle(employeeTitle);
    }

    private void setRevocableTrustIndicator(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (relatedParty.getRevocableTrustIndicator() != null) {
            p2pRelationship.setRevocableTrustIndicator(relatedParty.getRevocableTrustIndicator());
        }
    }

    private void setEmployeeTitleRole2(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (relatedParty.getRole2PartyEmployeeTitle() == null) {
            control.addNullAttribute("role2PartyEmployeeTitle");
            return;
        }

        JobTitleList jobTitleList = relatedParty.getRole2PartyEmployeeTitle();
        if (Objects.isNull(jobTitleList.getEmployeeTitle().getID())) {
            control.addNullAttribute("role2PartyEmployeeTitle");
            return;
        }

        MutableEmployeeTitle employeeTitle = new MutableEmployeeTitle();
        employeeTitle.setCode(jobTitleList.getEmployeeTitle().getCode());
        employeeTitle.setGroupCode(jobTitleList.getEmployeeTitle().getGroupCode().getCode());
        employeeTitle.setDescription(jobTitleList.getEmployeeTitle().getDescription().getText());
        p2pRelationship.setRole2PartyEmployeeTitle(employeeTitle);
    }

    private void setDependenceFactor(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (relatedParty.getEconomicDependenceFactor() != null) {
            p2pRelationship.setEconomicDependenceFactor(relatedParty.getEconomicDependenceFactor().getID());
        }
    }

    private void setAnnualOperatingCost(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (relatedParty.getPercentOfAnnualOperatingCostFundedByDonor() != null) {
            p2pRelationship.setPercentOfAnnualOperatingCostFundedByDonor(relatedParty.getPercentOfAnnualOperatingCostFundedByDonor().getID());
        }
    }

    private void setBeneficialOwnership(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (relatedParty.getPercentOfBeneficialOwnership() != null) {
            p2pRelationship.setPercentOfBeneficialOwnership(relatedParty.getPercentOfBeneficialOwnership().getID());
        }
    }

    private void setConfirmationMethod(MutablePartyToPartyRelationship p2pRelationship, P2PPartyToPartyRelationship relatedParty) {
        if (relatedParty.getPersonInChargeAuthorityConfirmationMethod() != null) {
            p2pRelationship.setPersonInChargeAuthorityConfirmationMethod(relatedParty.getPersonInChargeAuthorityConfirmationMethod().getID());
        }
    }
}

private void validateAndSetOwnershipPercentage(P2PRelationship p2pRelationship, RelatedParty relatedParty) {
    if (!"PP2MSBALDirectBeneficiaryOwnerOfRelationship".equals(p2pRelationship.getPartyRelationshipType().getID())) {
        return;
    }

    if (relatedParty.getPercentageValueOfOwnership() == null) {
        control.addNullAttribute("percentageValueOfOwnership");
        return;
    }

    double percentage = relatedParty.getPercentageValueOfOwnership();
    if (percentage > 100) {
        throw new IllegalArgumentException("Ownership of MSBAL Direct Beneficiary must not be more than 100%");
    }

    p2pRelationship.setPercentageValueOfOwnership(percentage);
}

validateAndSetOwnershipPercentage(p2pRelationship, relatedParty);
