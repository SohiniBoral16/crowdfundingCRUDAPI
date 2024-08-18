
Feature: Copy Related Parties

  Background: Validate and Copy Related Parties from one Facing Client to Another
    Given the related parties of the source facing client are provided
    And the target facing client is specified

  @CopyRelatedParties
  Scenario: Duplicate related parties exist
    When the system validates the copy request payload
    Then if any related party is duplicated in the target facing client,
      the status of those parties should be set to "duplicate_relationship_exists"
    And the status of other related parties should be set to "ready_to_copy"
    Then the system completes the validation process

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


Certainly! Based on the discussion and your requirements, here's how you can write the scenarios in a Jira-compatible format for inclusion in the scope:

### Jira Scope Format

#### **Feature: Copy Related Parties**

**Description:**  
As a user, I want to copy related parties of the facing client to another facing client.

**Scenarios:**

---

**Scenario 1: Duplicate related parties exist**

- **Given:** The related parties of the source facing client are provided.
- **And:** The target facing client is specified.
- **When:** The system validates the copy request payload.
- **Then:** If any related party is duplicated in the target facing client, the status of those parties should be set to `"duplicate_relationship_exists"`.
- **And:** The status of other related parties should be set to `"ready_to_copy"`.

---

**Scenario 2: No duplicate related parties are present**

- **Given:** The related parties of the source facing client are provided.
- **And:** The target facing client is specified.
- **When:** No duplicate related parties are present.
- **Then:** The system proceeds with copying the related parties.
- **And:** The status of each copied party should be set to `"copied"`.

---

**Scenario 3: Partial copy failure due to server error**

- **Given:** The related parties of the source facing client are provided.
- **And:** The target facing client is specified.
- **When:** The system attempts to copy the related parties.
- **And:** Some related parties encounter a server error during the copy.
- **Then:** The status of the successfully copied parties should be set to `"copied"`.
- **And:** The status of the unprocessed related parties should be set to `"copy_failure"`.

---

**Acceptance Criteria:**
1. **Validation of Request Payload:** 
   - The system should correctly identify and set statuses for duplicate related parties and those ready to copy.
   - Any duplicates should be marked with the `"duplicate_relationship_exists"` status.

2. **Successful Copy:** 
   - If no duplicates exist, all related parties should be copied and marked with the `"copied"` status.

3. **Error Handling:** 
   - If a server error occurs, the system should differentiate between successfully copied and failed parties, assigning `"copied"` and `"copy_failure"` statuses accordingly.

### How to Use in Jira:
- **Feature Title:** Use the feature description as the title or summary.
- **Feature Description:** Include the scenarios and acceptance criteria in the description.
- **Scope:** Mention that the scenarios cover duplicate handling, successful copy operations, and error management.

Your manager can review these scenarios directly within Jira, ensuring that the BDD approach is clear and aligns with the scope.

Feature: As a user
  I want to copy related parties of the facing client to another facing client

  Background: Validate and Copy Related Parties from one Facing Client to Another
    Given the related parties of the source facing client are provided
    And the target facing client is specified

  @CopyRelatedParties
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
