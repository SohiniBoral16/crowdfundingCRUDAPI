Here is a rewritten version of the email:

---

**To:** Sarathe, Nishant (Ops Tech); Oliveira, Silvia (SHARED SERVICES & BANKING OPS); Kaup, Krithika (SHARED SERVICES & BANKING OPS)  
**Cc:** Giunga, Maria (SHARED SERVICES & BANKING OPS); Hemmer, Claas-Henning (SHARED SERVICES & BANKING OPS); cors_squad_transformers; Mitra, Sawswati (SHARED SERVICES & BANKING OPS); Shivaprasad, Abhay (SHARED SERVICES & BANKING OPS); Vohra, Saurabh (Ops Tech); Nappi, Danielle (SHARED SERVICES & BANKING OPS); Manasa, Devi (SHARED SERVICES & BANKING OPS); Archana, Desai (SHARED SERVICES & BANKING OPS); Yisa, Phoebe (SHARED SERVICES & BANKING OPS)

**Subject:** RE: [offline program / prod matter] Abu Dhabi KYC – p2p negative affirmation

Hi Nishant,

I have reviewed both the QA and UAT environments and noted the following relationships in negative affiliation across both settings:

[Insert observed relationships and details here]

Please confirm if anything further needs to be done in PipeX to address new P2P and negative affirmations.

All these details are available in A2.

Regards,  
[Your Name]

---

Is there any specific content you'd like to include in the email or any other modifications needed?




public List<Party> getPartyWithAttributes(String path, String partyId, List<String> attributes, List<String> attributeKeys) {
    long start = System.currentTimeMillis();
    CodaPixPartyRequest codaPixPartyRequest = new CodaPixPartyRequest();
    codaPixPartyRequest.setPartyID(partyId);
    codaPixPartyRequest.setAttributes(attributes != null ? attributes : attributeKeys);

    String url = codaEndpoint + path + "?app-id=" + appId + "&user-id=" + appUser;

    logger.info("coda_getPartyByID for partyId: {} called, URL: {}", partyId, url);
    CodaPartyRes response = restTemplate.postForObject(url, codaPixPartyRequest, CodaPartyRes.class);
    logger.info("coda_getPartyByID returned in {} ms", (System.currentTimeMillis() - start));

    return response.getDataList();
}

// Original methods using the new combined method
public List<Party> getPartyWithAttributesPOST(String partyId, List<String> attributes) {
    return getPartyWithAttributes("/parties", partyId, attributes, null);
}

public List<Party> getPartyWithAttributesBatchPOST(List<String> partyIds, List<String> attributeKeys) {
    return getPartyWithAttributes("/parties/batch", partyIds, null, attributeKeys);
}
----------------------------------------
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ms.clientdata.p2pservice.controller.RelationshipVisualizationController;
import com.ms.clientdata.p2pservice.exception.P2PServiceException;
import com.ms.clientdata.p2pservice.service.RelationshipVisualizationService;
import com.ms.clientdata.p2pservice.util.RelationshipVisualization;

@WebMvcTest(RelationshipVisualizationController.class)
public class RelationshipVisualizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelationshipVisualizationService relationshipVisualizationService;

    @Autowired
    private RelationshipVisualizationController relationshipVisualizationController;

    @BeforeEach
    public void setUp() {
        // Setup mock data if needed
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testGetPrincipleOfHierarchy(String mainPartyId, String feature, List<RelationshipVisualization> mockResponse, HttpStatus expectedStatus) throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);

        if (expectedStatus == HttpStatus.OK) {
            when(relationshipVisualizationService.getRelationshipVisualizationById(mainPartyId)).thenReturn(mockResponse);
        } else {
            when(relationshipVisualizationService.getRelationshipVisualizationById(mainPartyId)).thenThrow(new P2PServiceException("Error occurred"));
        }

        ResponseEntity<List<RelationshipVisualization>> response = relationshipVisualizationController.getPrincipleOfHierarchy(request, mainPartyId, feature);
        assertEquals(expectedStatus, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    private static Stream<Arguments> provideParameters() {
        List<RelationshipVisualization> successfulResponseBody = List.of(new RelationshipVisualization());
        HttpStatus successStatus = HttpStatus.OK;
        
        List<RelationshipVisualization> failureResponseBody = null;
        HttpStatus failureStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return Stream.of(
            Arguments.of("validMainPartyId", "validFeature", successfulResponseBody, successStatus),
            Arguments.of("invalidMainPartyId", "invalidFeature", failureResponseBody, failureStatus)
        );
    }
}
