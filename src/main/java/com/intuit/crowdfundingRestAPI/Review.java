Feature: Copy and Validate Related Party Relationships

  Background: Validate and Copy Related Parties from one Facing Client to Another
    Given the related parties of the source facing client are provided
    And the target facing client is specified

  @CopyRelatedParties
  Scenario: Duplicate related parties exist and get resolved
    When the system validates the copy request payload
    Then if any related party is duplicated in the target facing client,
      the status of those parties should be set to "duplicate_relationship_exists"
    And the status of other related parties should be set to "ready_to_copy"
    When the duplicate relationships are resolved (e.g., deleted or overridden)
    And a new request is sent with the updated related parties
    Then the system revalidates the copy request payload
    And if no errors exist after revalidation
    Then the system proceeds to copy all related parties
    And the status of each copied party should be set to "copied"
    Then the system completes the copy process

  Scenario: No duplicate related parties and copy succeeds
    When no duplicate related parties are present
    Then the system proceeds with copying the related parties
    And the status of each copied party should be set to "copied"
    Then the system completes the copy process

  Scenario: Partial copy failure due to server error
    When the system attempts to copy the related parties
    And some related parties encounter a server error during the copy
    Then the status of the successfully copied parties should be set to "copied"
    And the status of the unprocessed related parties should be set to "copy_failure"
    Then the system completes the copy process
