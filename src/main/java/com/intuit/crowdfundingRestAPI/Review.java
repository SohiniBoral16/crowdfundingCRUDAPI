Feature: Copy Related Parties

  Background: Validate and Copy Related Parties from one Facing Client to Another
    Given the related parties of the source facing client are provided
    And the target facing client is specified

  Scenario: Duplicate related parties exist
    When the system validates the copy request payload
    Then if any related party is duplicated in the target facing client,
      the status of those parties should be set to "duplicate_relationship_exists"
    And the status of other related parties should be set to "ready_to_copy"

  Scenario: No duplicate related parties are present
    When no duplicate related parties are present
    Then the system proceeds with copying the related parties
    And the status of each copied party should be set to "copied"

  Scenario: Partial copy failure due to server error
    When the system attempts to copy the related parties
    And some related parties encounter a server error during the copy
    Then the status of the successfully copied parties should be set to "copied"
    And the status of the unprocessed related parties should be set to "copy_failure"


Feature: Copy Related Parties

  Scenario: Validate and Copy Related Parties from one Facing Client to Another
    Given the related parties of the source facing client are provided
    And the target facing client is specified
    When the system validates the copy request payload
    Then if any related party is duplicated in the target facing client, 
      the status of those parties should be set to "duplicate_relationship_exists"
    And the status of other related parties should be set to "ready_to_copy"
    When no duplicate related parties are present
    Then the system proceeds with copying the related parties
    And the status of each copied party should be set to "copied"
    When a server error occurs during the copy
    Then the status of the related parties should be set to "copy_failure"
