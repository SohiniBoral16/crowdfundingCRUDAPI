

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
