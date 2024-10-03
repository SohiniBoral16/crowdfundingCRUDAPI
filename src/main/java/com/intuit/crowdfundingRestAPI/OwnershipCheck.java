
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(P2PController.class) // Change to your actual controller class
public class P2PControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CodaQueryClient codaQueryClient;  // Mock the service that fetches the main party

    @Test
    public void testGetMainParty() throws Exception {
        // Mock data for main party
        P2PParty mainParty = new P2PParty();
        mainParty.setId("B8802455240");
        
        // Mock related parties
        P2PPartyRelationshipType ownershipRelationship = new P2PPartyRelationshipType();
        ownershipRelationship.setID("OWNERSHIP");
        
        P2PPartyToPartyRelationship relatedParty1 = new P2PPartyToPartyRelationship();
        relatedParty1.setPartyRelationshipType(ownershipRelationship);
        
        P2PPartyToPartyRelationship relatedParty2 = new P2PPartyToPartyRelationship();
        relatedParty2.setPartyRelationshipType(new P2PPartyRelationshipType());  // non-ownership relationship
        
        List<P2PPartyToPartyRelationship> relatedParties = Arrays.asList(relatedParty1, relatedParty2);
        mainParty.setRelatedPartyList(relatedParties);
        
        // Mock the service call to return main party
        when(codaQueryClient.getPartyWithAttributesPOST(any(), any())).thenReturn(mainParty);
        
        // Perform GET request to /p2p/mainParty/B8802455240
        mockMvc.perform(get("/p2p/mainParty/B8802455240")
                .contentType("application/json"))
                .andExpect(status().isOk())
                // Validate JSON structure and data filtering
                .andExpect(jsonPath("$.relatedPartyList", hasSize(1)))  // Should have filtered out non-ownership related parties
                .andExpect(jsonPath("$.relatedPartyList[0].partyRelationshipType.id", is("OWNERSHIP")));
    }
}
Optional.ofNullable(mainParty.getRelatedPartyList())
    .ifPresent(relatedParties -> {
        var toRemove = relatedParties.stream()
            .filter(rp -> P2PHierarchyRelationship.isOwnershipType(rp.getPartyRelationshipType().getID()))
            .collect(Collectors.toList());
        
        // Remove all filtered items from the original list
        relatedParties.removeAll(toRemove);
    });



mainParty.getRelatedPartyList().forEach(rp -> {
    String relationshipTypeId = rp.getPartyRelationshipType().getID();

    // Skip if the relationship type is an ownership type
    if (P2PHierarchyRelationshipType.isOwnershipType(relationshipTypeId)) {
        return;
    }

    // Process other relationships
    Optional.of(relationshipTypeId)
            .filter(id -> P2P_CORS_EXCLUDED_RELATIONSHIPS.contains(id))
            .ifPresentOrElse(
                id -> p2PParty.getRelatedPartyList().add(convertRelatedParty(rp)),
                () -> {
                    if (P2PFinancialRegulationRelationship.equals(relationshipTypeId)) {
                        p2PParty.getFinancialRegulatorList().add(convert(rp.getRole2party()));
                    }
                }
            );
});



public static boolean isOwnershipType(String relationshipTypeId) {
    return Arrays.stream(P2PHierarchyRelationshipType.values())
                 .anyMatch(type -> type.ownershipTypeFlag && type.relationshipTypeId.equals(relationshipTypeId));
}


public static boolean isOwnershipType(String relationshipTypeId) {
    return Arrays.stream(P2PHierarchyRelationshipType.values())
                 .anyMatch(type -> type.isOwnershipTypeFlag() && type.getRelationshipTypeId().equals(relationshipTypeId));
}
