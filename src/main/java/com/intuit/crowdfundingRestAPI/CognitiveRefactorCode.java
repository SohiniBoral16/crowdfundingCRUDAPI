@Test
void testProcessMSBALDirectOwnershipDetails_withOtherRelationshipType() {
    P2PPartyToPartyRelationship relatedParty = mock(P2PPartyToPartyRelationship.class);
    PartyToPartyRelationship p2pRelationship = new PartyToPartyRelationship();
    p2pRelationship.setPartyRelationshipType(new PartyRelationshipType("some_other_relationship_id"));
    
    DTOControl control = mock(DTOControl.class);
    
    when(relatedParty.getPercentageValueOfOwnership()).thenReturn(30.0f);
    
    yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);
    
    assertEquals(30.0f, p2pRelationship.getPercentageValueOfOwnership());
}
------------------
private void processMSBALDirectOwnershipDetails(DTOControl control, P2PPartyToPartyRelationship relatedParty, 
    PartyToPartyRelationship p2pRelationship) {

    String relationshipIdOfMSBALDirectBeneficialOwnerOf = P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID;
    
    if (isDirectBeneficialOwnerRelationship(p2pRelationship.getPartyRelationshipType().getID(), relationshipIdOfMSBALDirectBeneficialOwnerOf)) {
        handleDirectBeneficialOwnerRelationship(control, relatedParty, p2pRelationship);
    }
}

private boolean isDirectBeneficialOwnerRelationship(String relationshipTypeId, String directBeneficialOwnerId) {
    return directBeneficialOwnerId.equals(relationshipTypeId);
}

private void handleDirectBeneficialOwnerRelationship(DTOControl control, P2PPartyToPartyRelationship relatedParty, 
    PartyToPartyRelationship p2pRelationship) {

    Optional.ofNullable(relatedParty.getPercentageValueOfOwnership()).ifPresentOrElse(percentageValueOfOwnership -> {
        if (percentageValueOfOwnership > 100) {
            throw new IllegalArgumentException("Ownership of MSBAL Direct Beneficiary must not be more than 100%");
        }
        p2pRelationship.setPercentageValueOfOwnership(percentageValueOfOwnership);
    }, () -> control.addNullAttribute("percentageValueOfOwnership"));
}
-----------------------------
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
---------------------------------------
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class YourClassTest {

    private YourClass yourClass;
    private DTOControl control;
    private P2PPartyToPartyRelationship relatedParty;
    private PartyToPartyRelationship p2pRelationship;

    @Before
    public void setUp() {
        yourClass = new YourClass();
        control = Mockito.mock(DTOControl.class);
        relatedParty = Mockito.mock(P2PPartyToPartyRelationship.class);
        p2pRelationship = Mockito.mock(PartyToPartyRelationship.class);
    }

    @Test
    public void testProcessMSBALDirectOwnershipDetails_withDirectBeneficialOwner() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(50.0);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);

        verify(p2pRelationship).setPercentageValueOfOwnership(50.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessMSBALDirectOwnershipDetails_withOwnershipGreaterThan100() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(150.0);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);
    }

    @Test
    public void testProcessMSBALDirectOwnershipDetails_withOtherRelationshipType() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("some_other_relationship_id");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(30.0);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);

        verify(p2pRelationship).setPercentageValueOfOwnership(30.0);
    }

    @Test
    public void testProcessMSBALDirectOwnershipDetails_nullPercentageValue() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(null);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);

        verify(control).addNullAttribute("percentageValueOfOwnership");
    }
}
To test the private methods indirectly by testing the public method that calls them, you can structure your unit tests to ensure that the public method correctly invokes the private methods and handles the scenarios appropriately. You do not need to mock private methods directly. Instead, you will validate the behavior through the public interface.

Let's write the test cases focusing on the public method `processMSBALDirectOwnershipDetails`, which calls the private methods indirectly.

### JUnit Test Class for Public Method

Here is an example of how to write unit tests for the `processMSBALDirectOwnershipDetails` method:

```java
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class YourClassTest {

    private YourClass yourClass;
    private DTOControl control;
    private P2PPartyToPartyRelationship relatedParty;
    private PartyToPartyRelationship p2pRelationship;

    @Before
    public void setUp() {
        yourClass = new YourClass();
        control = Mockito.mock(DTOControl.class);
        relatedParty = Mockito.mock(P2PPartyToPartyRelationship.class);
        p2pRelationship = Mockito.mock(PartyToPartyRelationship.class);
    }

    @Test
    public void testProcessMSBALDirectOwnershipDetails_withDirectBeneficialOwner() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(50.0);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);

        verify(p2pRelationship).setPercentageValueOfOwnership(50.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessMSBALDirectOwnershipDetails_withOwnershipGreaterThan100() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(150.0);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);
    }

    @Test
    public void testProcessMSBALDirectOwnershipDetails_withOtherRelationshipType() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("some_other_relationship_id");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(30.0);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);

        verify(p2pRelationship).setPercentageValueOfOwnership(30.0);
    }

    @Test
    public void testProcessMSBALDirectOwnershipDetails_nullPercentageValue() {
        when(p2pRelationship.getPartyRelationshipType().getID())
            .thenReturn("P2P_MSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID");
        when(relatedParty.getPercentageValueOfOwnership())
            .thenReturn(null);

        yourClass.processMSBALDirectOwnershipDetails(control, relatedParty, p2pRelationship);

        verify(control).addNullAttribute("percentageValueOfOwnership");
    }
}
```

### Explanation

1. **Setup Mocks**: Initialize the objects using Mockito.
2. **Test Cases**:
    - **Direct Beneficial Owner**: Checks if the `setPercentageValueOfOwnership` method is called with the correct value when the relationship ID matches the expected constant.
    - **Ownership Greater Than 100%**: Ensures an `IllegalArgumentException` is thrown for invalid ownership values.
    - **Other Relationship Type**: Verifies handling of other relationship types and ensures the percentage value is set correctly.
    - **Null Percentage Value**: Checks if the `addNullAttribute` method is called when the percentage value is `null`.

### Next Steps

**a.** Run the tests to verify they pass and ensure the correctness of your code.
**b.** Add additional test cases as needed to cover more scenarios and edge cases.

Would you like further assistance with setting up your testing environment or writing more specific tests?
