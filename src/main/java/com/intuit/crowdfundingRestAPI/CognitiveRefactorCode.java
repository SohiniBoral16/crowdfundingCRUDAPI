import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class P2PTransformerTest {

    @Mock
    private DTOControl control;

    @InjectMocks
    private P2PTransformer transformer;

    private P2PPartyToPartyRelationship relatedParty;
    private PartyToPartyRelationship p2pRelationship;
    private Method method;

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        relatedParty = new P2PPartyToPartyRelationship();
        p2pRelationship = new PartyToPartyRelationship();
        p2pRelationship.setPartyRelationshipType(new PartyRelationshipType("HSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID"));

        // Use reflection to access the private method
        method = P2PTransformer.class.getDeclaredMethod("processHSBALDirectOwnershipDetails", DTOControl.class, P2PPartyToPartyRelationship.class, PartyToPartyRelationship.class);
        method.setAccessible(true);
    }

    @ParameterizedTest
    @MethodSource("providePercentageValues")
    void testPercentageValueOfOwnershipForHSBALDirectOwner(Float inputValue, Float expectedValue, String expectedErrorMessage) throws InvocationTargetException, IllegalAccessException {
        relatedParty.setPercentageValueOfOwnership(inputValue);

        if (expectedErrorMessage.isEmpty()) {
            method.invoke(transformer, control, relatedParty, p2pRelationship);
            assertEquals(expectedValue, p2pRelationship.getPercentageValueOfOwnership());
            if (inputValue == null) {
                verify(control).addNullAttribute("percentageValueOfOwnership");
            }
        } else {
            IllegalArgumentException exception = assertThrows(InvocationTargetException.class, () -> {
                method.invoke(transformer, control, relatedParty, p2pRelationship);
            }).getCause();
            assertEquals(expectedErrorMessage, exception.getMessage());
        }
    }

    private Stream<Arguments> providePercentageValues() {
        return Stream.of(
            Arguments.of(45.0f, 45.0f, ""),     // Normal case
            Arguments.of(102.55f, null, "Ownership or HSBAL Direct Beneficiary must not be more than 100%"),  // Exception case
            Arguments.of(null, null, "")         // Null case
        );
    }
}
--------------------
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class P2PTransformerTest {

    @Mock
    private DTOControl control;

    @InjectMocks
    private P2PTransformer transformer;

    private P2PPartyToPartyRelationship relatedParty;
    private PartyToPartyRelationship p2pRelationship;
    private Method method;

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        relatedParty = new P2PPartyToPartyRelationship();
        p2pRelationship = new PartyToPartyRelationship();
        p2pRelationship.setPartyRelationshipType(new PartyRelationshipType("HSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID"));

        // Use reflection to access the private method
        method = P2PTransformer.class.getDeclaredMethod("processHSBALDirectOwnershipDetails", DTOControl.class, P2PPartyToPartyRelationship.class, PartyToPartyRelationship.class);
        method.setAccessible(true);
    }

    @ParameterizedTest
    @MethodSource("providePercentageValues")
    void testPercentageValueOfOwnershipForHSBALDirectOwner(Float inputValue, Float expectedValue, String expectedErrorMessage) throws InvocationTargetException, IllegalAccessException {
        relatedParty.setPercentageValueOfOwnership(inputValue);

        if (expectedErrorMessage.isEmpty()) {
            method.invoke(transformer, control, relatedParty, p2pRelationship);
            assertEquals(expectedValue, p2pRelationship.getPercentageValueOfOwnership());
            if (inputValue == null) {
                verify(control).addNullAttribute("percentageValueOfOwnership");
            }
        } else {
            IllegalArgumentException exception = assertThrows(InvocationTargetException.class, () -> {
                method.invoke(transformer, control, relatedParty, p2pRelationship);
            }).getCause();
            assertEquals(expectedErrorMessage, exception.getMessage());
        }
    }

    private Stream<Arguments> providePercentageValues() {
        return Stream.of(
            Arguments.of(45.0f, 45.0f, ""),     // Normal case
            Arguments.of(102.55f, null, "Ownership or HSBAL Direct Beneficiary must not be more than 100%"),  // Exception case
            Arguments.of(null, null, "")         // Null case
        );
    }
}




≠============
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ParameterizedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class P2PTransformerTest {

    @Mock
    private DTOControl control;

    @InjectMocks
    private P2PTransformer transformer;

    private P2PPartyToPartyRelationship relatedParty;
    private PartyToPartyRelationship p2pRelationship;

    @BeforeEach
    public void setUp() {
        relatedParty = new P2PPartyToPartyRelationship();
        p2pRelationship = new PartyToPartyRelationship();
    }

    @ParameterizedTest
    @MethodSource("provideOwnershipDetails")
    public void testProcessHSBALDirectOwnershipDetails(Double ownershipValue, boolean expectException) {
        relatedParty.setPercentageValueOfOwnership(ownershipValue);
        p2pRelationship.setPartyRelationshipType(new PartyRelationshipType("HSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID"));

        if (expectException) {
            assertThrows(IllegalArgumentException.class, () -> {
                transformer.processHSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);
            });
        } else {
            transformer.processHSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);
            if (ownershipValue == null) {
                verify(control).addNullAttribute("percentageValueOfOwnership");
            } else {
                assertEquals(ownershipValue, p2pRelationship.getPercentageValueOfOwnership());
            }
        }
    }

    private static Stream<Arguments> provideOwnershipDetails() {
        return Stream.of(
                Arguments.of(50.0, false),    // Normal case
                Arguments.of(150.0, true),    // Exception case
                Arguments.of(null, false)     // Null case
        );
    }
}



private void processOwnershipDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, PartyToPartyRelationship p2pRelationship) {
    String relationshipTypeId = p2pRelationship.getPartyRelationshipType().getID();
    if (P2P_HSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID == relationshipTypeId || 
        P2P_HSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID.equals(relationshipTypeId)) {
        handlePercentageValueOfOwnership(control, relatedParty, p2pRelationship);
    }

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
