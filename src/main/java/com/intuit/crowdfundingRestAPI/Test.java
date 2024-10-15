import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class PartyVisualizationServiceTest {

    private PartyVisualizationService service = new PartyVisualizationService(); // Assuming this is your class

    @Test
    void testGetPartyVisualizationByPartyId_NullPartyId() {
        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () -> {
            service.getPartyVisualizationByPartyId(null);
        });

        // Optionally, check the message of the exception
        String expectedMessage = "partyId is marked non-null but is null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
