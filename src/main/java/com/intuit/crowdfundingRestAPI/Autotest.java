
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
