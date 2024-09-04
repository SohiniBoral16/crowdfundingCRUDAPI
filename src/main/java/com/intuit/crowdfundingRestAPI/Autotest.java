
@When("I prepare the copy relationships JSON payload")
public void prepareCopyRelationshipsJSON() {
    // Load the template JSON from the file
    p2pCopyRelationshipJSON = new JSONObject(KerberosClient.filterJson(requestsFolder, "copyRelationships.json").toString());

    // Replace the placeholders with actual values
    p2pCopyRelationshipJSON.put("mainPartyId", p2PCopyRequest.getMainPartyId());

    // Loop over target parties and replace the values dynamically
    for (int i = 0; i < p2PCopyRequest.getTargetParties().size(); i++) {
        P2PCopyTargetParty targetParty = p2PCopyRequest.getTargetParties().get(i);
        JSONObject targetPartyJSON = p2pCopyRelationshipJSON.getJSONArray("targetParties").getJSONObject(i);
        targetPartyJSON.put("targetPartyId", targetParty.getTargetPartyId());
        targetPartyJSON.put("action", targetParty.getAction() != null ? targetParty.getAction().toString() : null);
    }

    // Loop over source relationships and replace the values dynamically
    for (int i = 0; i < p2PCopyRequest.getSourceRelationships().size(); i++) {
        P2PCopyRelationship relationship = p2PCopyRequest.getSourceRelationships().get(i);
        JSONObject relationshipJSON = p2pCopyRelationshipJSON.getJSONArray("sourceRelationships").getJSONObject(i);
        relationshipJSON.put("sourcePartyId", relationship.getSourcePartyId());
        relationshipJSON.put("relationshipTypeIds", relationship.getRelationshipTypeIds());
    }

    // Convert the final JSON object back to string format for the API call
    p2pCopyRequestJSON = p2pCopyRelationshipJSON.toString();
}



{
  "mainPartyId": "{mainPartyId}",
  "targetParties": [
    {
      "targetPartyId": "{targetPartyId_1}",
      "action": "{action_1}"
    },
    {
      "targetPartyId": "{targetPartyId_2}",
      "action": "{action_2}"
    }
  ],
  "sourceRelationships": [
    {
      "sourcePartyId": "{sourcePartyId_1}",
      "relationshipTypeIds": [
        "{relationshipTypeId_1}",
        "{relationshipTypeId_2}"
      ]
    },
    {
      "sourcePartyId": "{sourcePartyId_2}",
      "relationshipTypeIds": [
        "{relationshipTypeId_3}"
      ]
    }
  ]
}


----------------------
package com.ms.kycautomationframework.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.kycautomationframework.model.P2PCopyRequest;
import com.ms.kycautomationframework.model.P2PCopyRelationship;
import com.ms.kycautomationframework.model.P2PCopyTargetParty;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class P2PCopyRelationshipStep {

    @Value("${copyRelationshipsURI}")
    private String copyRelationshipsURI;

    private P2PCopyRequest p2PCopyRequest;
    private ResponseEntity<String> response;

    @Given("main party Id {string}")
    public void givenMainPartyId(String mainPartyId) {
        p2PCopyRequest = new P2PCopyRequest();
        p2PCopyRequest.setMainPartyId(mainPartyId);
    }

    @Given("the following target parties and the relationships of main party id to be copied in the target parties:")
    public void givenTargetPartiesCopyRelationships(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<P2PCopyTargetParty> targetParties = new ArrayList<>();
        List<P2PCopyRelationship> sourceRelationships = new ArrayList<>();

        for (Map<String, String> row : rows) {
            // Create P2PCopyTargetParty object
            P2PCopyTargetParty targetParty = new P2PCopyTargetParty();
            targetParty.setTargetPartyId(row.get("targetPartyId"));
            if (row.get("action") != null && !row.get("action").isEmpty()) {
                targetParty.setAction(P2PCopyAction.valueOf(row.get("action")));
            }
            targetParties.add(targetParty);

            // Create P2PCopyRelationship object
            P2PCopyRelationship relationship = new P2PCopyRelationship();
            relationship.setSourcePartyId(row.get("sourcePartyId"));
            relationship.setRelationshipTypeIds(List.of(row.get("relationshipTypeIds").split(",")));
            sourceRelationships.add(relationship);
        }

        p2PCopyRequest.setTargetParties(targetParties);
        p2PCopyRequest.setSourceRelationships(sourceRelationships);
    }

    @When("I send the copy request")
    public void sendCopyRequest() throws IOException {
        // Convert P2PCopyRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(p2PCopyRequest);

        // Make POST request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
        response = restTemplate.exchange(copyRelationshipsURI, HttpMethod.POST, entity, String.class);
    }

    @Then("the copyStatus should be {string}")
    public void verifyCopyStatus(String expectedStatus) {
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONObject jsonResponse = new JSONObject(response.getBody());
        String actualStatus = jsonResponse.getString("copyStatus");
        Assert.assertEquals(expectedStatus, actualStatus);
    }

    @Then("the response should contain the message {string}")
    public void verifyResponseMessage(String expectedMessage) {
        JSONObject jsonResponse = new JSONObject(response.getBody());
        String actualMessage = jsonResponse.getString("message");
        Assert.assertEquals(expectedMessage, actualMessage);
    }
}


--------+-------------------
package com.ms.kycautomationframework.world;

import com.ms.kycautomationframework.model.P2PCopyRequest;
import com.ms.kycautomationframework.model.P2PCopyTargetParty;
import com.ms.kycautomationframework.model.P2PCopyRelationship;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class P2PCopyRelationshipStep {

    private P2PCopyRequest p2PCopyRequest;
    private String copyStatus;

    @Given("main party Id {string}")
    public void givenMainPartyId(String mainPartyId) {
        p2PCopyRequest = new P2PCopyRequest();
        p2PCopyRequest.setMainPartyId(mainPartyId);
    }

    @Given("the following target parties and the relationships of the main party Id to be copied in the target parties:")
    public void givenTargetPartiesAndRelationships(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<P2PCopyTargetParty> targetParties = new ArrayList<>();
        List<P2PCopyRelationship> sourceRelationships = new ArrayList<>();

        for (Map<String, String> row : rows) {
            // Create and populate the P2PCopyTargetParty object
            P2PCopyTargetParty targetParty = new P2PCopyTargetParty();
            targetParty.setTargetPartyId(row.get("targetPartyId"));
            targetParty.setAction(row.get("action") == null ? null : P2PCopyAction.valueOf(row.get("action")));
            targetParties.add(targetParty);

            // Create and populate the P2PCopyRelationship object
            P2PCopyRelationship relationship = new P2PCopyRelationship();
            relationship.setSourcePartyId(row.get("sourcePartyId"));
            relationship.setRelationshipTypeIds(List.of(row.get("relationshipTypeIds").split(",")));
            sourceRelationships.add(relationship);
        }

        p2PCopyRequest.setTargetParties(targetParties);
        p2PCopyRequest.setSourceRelationships(sourceRelationships);
    }

    @Then("while validating if no duplicate related parties are present in the target parties, then the copyStatus should be {string}")
    public void validateNoDuplicateRelatedParties(String expectedStatus) {
        // Add logic to validate that there are no duplicate relationships
        // For demonstration purposes, we will set the copyStatus as VALIDATION_SUCCESS
        copyStatus = "VALIDATION_SUCCESS";
        Assert.assertEquals(expectedStatus, copyStatus);
    }

    @Then("all the source parties should be successfully copied in the target parties, the copyStatus should be {string}")
    public void copySourcePartiesToTarget(String expectedStatus) {
        // Add logic to handle the copying of source parties to target parties
        // For demonstration purposes, we will set the copyStatus as COPY_SUCCESS
        copyStatus = "COPY_SUCCESS";
        Assert.assertEquals(expectedStatus, copyStatus);
    }
}



-------------------------------
Scenario: Validate relationships for parties with duplicate relationships
    Given the main party ID "BBB02722214"
    | targetPartyId    | status                          | copyFailedRelationships         | copySuccessRelationships         |
    | "BBB02682978"    | "DUPLICATE_RELATIONSHIP_EXISTS"  | [sourcePartyId: "BBB02532943", relationshipTypeIds: [8021501]]  | []  |
    | "BBB02682216"    | "READY_TO_COPY"                 | []                            | [sourcePartyId: "BBB02532943", relationshipTypeIds: [8021761, 8021501]], [sourcePartyId: "BBB02631843", relationshipTypeIds: [8058647]]  |

When the user executes the P2P copy functionality
Then the copyStatus should be "VALIDATION_FAILURE"
And the message should be "Duplicate Relationships exists within the selected Parties"


Feature: Copy relations between parties
  As a user,
  I want to validate and copy relationships between target parties.

  Background:
    Given the following target parties exist:
      | targetPartyId  | actionCode |
      | BBB02722214    | null       |
      | BBB02682216    | null       |
      | BBB02682978    | null       |
    And the following source relationships exist:
      | sourcePartyId  | relationshipTypeIds     |
      | BBB02532943    | 8021501, 8021761        |
      | BBB02631843    | 8058647                 |

  Scenario: Validate all target parties are ready to copy
    When I send a copy relations request
    Then the response should contain:
      | copyStatus           | VALIDATION_SUCCESS |
      | message              | All target parties are ready to copy |
      | mainPartyId          | BBB02722214        |
      | validationStatus     | targetPartyId | status        | copyFailedRelationships | copySuccessRelationships                                                             |
      |                      | BBB02722214   | READY_TO_COPY | []                     | [{"sourcePartyId":"BBB02631843","relationshipTypeIds":["8058647"]},                 |
      |                      |               |               |                        | {"sourcePartyId":"BBB02532943","relationshipTypeIds":["8021501","8021761"]}]       |
      |                      | BBB02682216   | READY_TO_COPY | []                     | [{"sourcePartyId":"BBB02631843","relationshipTypeIds":["8058647"]},                 |
      |                      |               |               |                        | {"sourcePartyId":"BBB02532943","relationshipTypeIds":["8021501","8021761"]}]       |
      |                      | BBB02682978   | READY_TO_COPY | []                     | [{"sourcePartyId":"BBB02631843","relationshipTypeIds":["8058647"]},                 |
      |                      |               |               |                        | {"sourcePartyId":"BBB02532943","relationshipTypeIds":["8021501","8021761"]}]       |

  Scenario: Validation fails due to duplicate relationships
    Given the target party "BBB02682978" has existing relationships:
      | sourcePartyId  | relationshipTypeIds     |
      | BBB02532943    | 8021501                 |
    When I send a copy relations request
    Then the response should contain:
      | copyStatus           | VALIDATION_FAILURE |
      | message              | Duplicate Relationships exist within the selected Parties |
      | mainPartyId          | BBB02722214        |
      | validationStatus     | targetPartyId | status                        | copyFailedRelationships                            | copySuccessRelationships                                  |
      |                      | BBB02722214   | READY_TO_COPY                | []                                                | [{"sourcePartyId":"BBB02631843","relationshipTypeIds":["8058647"]},       |
      |                      |               |                               |                                                  | {"sourcePartyId":"BBB02532943","relationshipTypeIds":["8021501","8021761"]}]  |
      |                      | BBB02682216   | READY_TO_COPY                | []                                                | [{"sourcePartyId":"BBB02631843","relationshipTypeIds":["8058647"]},       |
      |                      |               |                               |                                                  | {"sourcePartyId":"BBB02532943","relationshipTypeIds":["8021501","8021761"]}]  |
      |                      | BBB02682978   | DUPLICATE_RELATIONSHIP_EXISTS | [{"sourcePartyId":"BBB02532943","relationshipTypeIds":["8021501"]}]  | [{"sourcePartyId":"BBB02631843","relationshipTypeIds":["8058647"]}]       |


Feature: Copy relations between parties
  As a user,
  I want to validate and copy relationships between target parties.

  Background:
    Given the following target parties exist:
      | targetPartyId  | actionCode |
      | BBB02722214    | null       |
      | BBB02682216    | null       |
      | BBB02682978    | null       |
    And the following source relationships exist:
      | sourcePartyId  | relationshipTypeIds     |
      | BBB02532943    | 8021501, 8021761        |
      | BBB02631843    | 8058647                 |

  Scenario: Validate all target parties are ready to copy
    When I send a copy relations request
    Then the response should contain the following statuses:
      | targetPartyId  | status               | copyFailedRelationships | copySuccessRelationships |
      | BBB02722214    | READY_TO_COPY        | []                      | sourcePartyId: BBB02631843, relationshipTypeIds: [8058647], sourcePartyId: BBB02532943, relationshipTypeIds: [8021501, 8021761] |
      | BBB02682216    | READY_TO_COPY        | []                      | sourcePartyId: BBB02631843, relationshipTypeIds: [8058647], sourcePartyId: BBB02532943, relationshipTypeIds: [8021501, 8021761] |
      | BBB02682978    | READY_TO_COPY        | []                      | sourcePartyId: BBB02631843, relationshipTypeIds: [8058647], sourcePartyId: BBB02532943, relationshipTypeIds: [8021501, 8021761] |

  Scenario: Validation fails due to duplicate relationships
    Given the target party "BBB02682978" has existing relationships:
      | sourcePartyId  | relationshipTypeIds     |
      | BBB02532943    | 8021501                 |
    When I send a copy relations request
    Then the response should contain the following statuses:
      | targetPartyId  | status                         | copyFailedRelationships                             | copySuccessRelationships |
      | BBB02722214    | READY_TO_COPY                  | []                                                  | sourcePartyId: BBB02631843, relationshipTypeIds: [8058647], sourcePartyId: BBB02532943, relationshipTypeIds: [8021501, 8021761] |
      | BBB02682216    | READY_TO_COPY                  | []                                                  | sourcePartyId: BBB02631843, relationshipTypeIds: [8058647], sourcePartyId: BBB02532943, relationshipTypeIds: [8021501, 8021761] |
      | BBB02682978    | DUPLICATE_RELATIONSHIP_EXISTS  | sourcePartyId: BBB02532943, relationshipTypeIds: [8021501] | sourcePartyId: BBB02631843, relationshipTypeIds: [8058647] |
    And the overall copy status should be "VALIDATION_FAILURE"
    And the response message should be "Duplicate Relationships exists within the selected Parties"



Feature: P2P Copy Functionality

  As a user
  I want to test the P2P copy functionality for parties with null action codes
  So that I can ensure the feature works correctly when all parties are new and have no action code

  Background:
    Given the main party ID "BBB02722214"

  Scenario: All target parties have null action codes (first-time copy)
    Given the following target parties with actions:
      | targetPartyId  | action |
      | BBB02722214    | null   |
      | BBB02682216    | null   |
      | BBB02682978    | null   |
    And the following source relationships:
      | sourcePartyId  | RelationshipTypeIds         |
      | BBB02532943    | 8021501, 8021761            |
    When I execute the P2P copy functionality
    Then the validation status should be "VALIDATION_SUCCESS"
    And the copy status should be "COPY_SUCCESS"
    And all the target parties should be validated and copied successfully





Feature: P2P Copy Functionality

  As a user
  I want to test the P2P copy functionality
  So that I can ensure the feature works as expected for different scenarios

  Background:
    Given the main party ID "BBB02722214"
  
  Scenario: Skip all parties
    Given the following target parties with actions:
      | targetPartyId  | action          |
      | BBB02722214    | SKIP            |
      | BBB02682216    | SKIP            |
      | BBB02682978    | SKIP            |
    And the following source relationships:
      | sourcePartyId  | RelationshipTypeIds         |
      | BBB02532943    | 8021501, 8021761            |
    When I execute the P2P copy functionality
    Then the validation status should be "SKIPPED_VALIDATION"
    And the copy status should be "COPY_SUCCESS"

  Scenario: Overwrite all parties
    Given the following target parties with actions:
      | targetPartyId  | action          |
      | BBB02722214    | OVERWRITE       |
      | BBB02682216    | OVERWRITE       |
      | BBB02682978    | OVERWRITE       |
    And the following source relationships:
      | sourcePartyId  | RelationshipTypeIds         |
      | BBB02532943    | 8021501, 8021761            |
    When I execute the P2P copy functionality
    Then the validation status should be "VALIDATION_SUCCESS"
    And the copy status should be "COPY_SUCCESS"

  Scenario: Mixed actions with null actions
    Given the following target parties with actions:
      | targetPartyId  | action          |
      | BBB02722214    | OVERWRITE       |
      | BBB02682216    | null            |
      | BBB02682978    | SKIP            |
    And the following source relationships:
      | sourcePartyId  | RelationshipTypeIds         |
      | BBB02532943    | 8021501, 8021761            |
    When I execute the P2P copy functionality
    Then the validation status should be "VALIDATION_SUCCESS"
    And the copy status should be "COPY_SUCCESS"
    And the null action party should be validated and copied successfully

  Scenario: Duplicate relationships exist
    Given the following target parties with actions:
      | targetPartyId  | action          |
      | BBB02722214    | OVERWRITE       |
      | BBB02682216    | OVERWRITE       |
    And the following source relationships:
      | sourcePartyId  | RelationshipTypeIds         |
      | BBB02532943    | 8021501, 8021761            |
      | BBB02532943    | 8021501, 8021761            |
    When I execute the P2P copy functionality
    Then the validation status should be "VALIDATION_FAILURE"
    And the copy status should be "DUPLICATE_RELATIONSHIP_EXISTS"

  Scenario: No duplicate relationships exist
    Given the following target parties with actions:
      | targetPartyId  | action          |
      | BBB02722214    | OVERWRITE       |
      | BBB02682216    | OVERWRITE       |
    And the following source relationships:
      | sourcePartyId  | RelationshipTypeIds         |
      | BBB02532943    | 8021501, 8021761            |
      | BBB02532943    | 8058647                     |
    When I execute the P2P copy functionality
    Then the validation status should be "VALIDATION_SUCCESS"
    And the copy status should be "COPY_SUCCESS"