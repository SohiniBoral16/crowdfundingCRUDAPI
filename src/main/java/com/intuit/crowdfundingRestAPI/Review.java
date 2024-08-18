
Certainly! Here’s how you can outline the scope of the Jira ticket with the scenarios based on the two test cases:

---

### **Jira Scope: Copy and Validate Related Party Relationships**

**Scenarios:**

1. **Scenario 1: Handle Duplicate Related Parties and Potential Errors During Copy**
   - **Given:** The related parties of the source facing client are provided.
   - **And:** The target facing client is specified.
   - **When:** The system validates the copy request payload.
   - **Then:** If any related party is duplicated in the target facing client, the status of those parties should be set to `"duplicate_relationship_exists"`.
   - **And:** The status of other related parties should be set to `"ready_to_copy"`.
   - **When:** The duplicate relationships are resolved (e.g., deleted or overridden).
   - **And:** A new request is sent with the updated related parties.
   - **Then:** The system revalidates the copy request payload.
   - **And:** If no duplicate parties are found after revalidation, the system proceeds to copy all related parties.
   - **And:** The status of each copied party should be set to `"copied"`.
   - **Then:** The system completes the copy process.

2. **Scenario 2: No Duplicate Related Parties and Successful Copy**
   - **Given:** The related parties of the source facing client are provided.
   - **And:** The target facing client is specified.
   - **When:** The system validates the copy request payload.
   - **And:** No duplicate related parties are found.
   - **Then:** The system proceeds with copying the related parties.
   - **And:** The status of each copied party should be set to `"copied"`.
   - **Then:** The system completes the copy process.

---

This concise summary captures both scenarios that should be included in the scope of the Jira ticket. It clearly outlines the conditions and expected outcomes for each scenario, ensuring that all necessary test cases are covered.

Let me know if you need further adjustments or additions!




Feature: Validate and copy related parties of the facing client to another facing client

  Background: Validate and Copy Related Parties from one Facing Client to Another
    Given the related parties of the source facing client are provided
    And the target facing client is specified

  @CopyRelatedParties
  Scenario: Handle duplicate related parties and potential errors during copy
    When the system validates the copy request payload
    Then if any related party is duplicated in the target facing client,
      the status of those parties should be set to "duplicate_relationship_exists"
    And the status of other related parties should be set to "ready_to_copy"
    
    When the duplicate relationships are resolved (e.g., deleted or overridden)
    And a new request is sent with the updated related parties
    Then the system revalidates the copy request payload
    And if no duplicate parties are found after revalidation
    Then the system proceeds to copy all related parties
    And the status of each copied party should be set to "copied"
    Then the system completes the copy process

  Scenario: No duplicate related parties and successful copy
    When the system validates the copy request payload
    And no duplicate related parties are found
    Then the system proceeds with copying the related parties
    And the status of each copied party should be set to "copied"
    Then the system completes the copy process


Feature: Copy and Validate Related Party Relationships

  Background: Validate and Copy Related Parties from one Facing Client to Another
    Given the related parties of the source facing client are provided
    And the target facing client is specified

  @CopyRelatedParties
  Scenario: Handle duplicate related parties and potential errors during copy
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

    When a server error occurs during the copy
    Then the status of the successfully copied parties should be set to "copied"
    And the status of the unprocessed related parties should be set to "copy_failure"

    Then the system completes the copy process


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
