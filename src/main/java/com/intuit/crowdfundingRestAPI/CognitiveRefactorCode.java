import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
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
    void testPercentageValueOfOwnershipForHSBALDirectOwner(Float inputValue, Float expectedValue, String expectedErrorMessage) {
        relatedParty.setPercentageValueOfOwnership(inputValue);

        if (expectedErrorMessage.isEmpty()) {
            try {
                method.invoke(transformer, control, relatedParty, p2pRelationship);
                assertEquals(expectedValue, p2pRelationship.getPercentageValueOfOwnership());
                if (inputValue == null) {
                    verify(control).addNullAttribute("percentageValueOfOwnership");
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    fail("Unexpected IllegalArgumentException: " + e.getCause().getMessage());
                } else {
                    fail("Unexpected exception: " + e.getMessage());
                }
            }
        } else {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                try {
                    method.invoke(transformer, control, relatedParty, p2pRelationship);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw e.getCause();
                }
            });
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

    // Additional test cases to cover toOOMPartyToPartyRelationship method
    @ParameterizedTest
    @MethodSource("providePartyRelationships")
    void testToOOMPartyToPartyRelationship(List<P2PPartyToPartyRelationship> relatedParties, int expectedSize) {
        List<PartyToPartyRelationship> result = transformer.toOOMPartyToPartyRelationship(relatedParties);
        assertEquals(expectedSize, result.size());
    }

    private Stream<Arguments> providePartyRelationships() {
        return Stream.of(
            Arguments.of(List.of(createPartyRelationship(45.0f)), 1),     // Normal case with valid percentage
            Arguments.of(List.of(createPartyRelationship(102.55f)), 0),    // Case with invalid percentage
            Arguments.of(List.of(createPartyRelationship(null)), 1),       // Case with null percentage
            Arguments.of(List.of(), 0)                                     // Case with empty list
        );
    }

    private P2PPartyToPartyRelationship createPartyRelationship(Float percentageValue) {
        P2PPartyToPartyRelationship party = new P2PPartyToPartyRelationship();
        party.setPercentageValueOfOwnership(percentageValue);
        party.setPartyRelationshipType(new PartyRelationshipType("HSBAL_DIRECT_BENEFICIAL_OWNER_OF_RELATIONSHIP_ID"));
        return party;
    }
}
