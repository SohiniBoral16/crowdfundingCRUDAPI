
private void processPartyHierarchy(Party rootParty, Party currentParty, String parentPartyId, List<PartyVisualization> partyVisualizationList) {
    if (currentParty == null) {
        return;
    }

    // Add the main party to the flat list
    PartyVisualization partyDetails = createPartyVisualization(currentParty);
    partyDetails.setParentId(parentPartyId);
    partyVisualizationList.add(partyDetails);

    List<RelationshipDetails> relationshipDetailList = new ArrayList<>();

    if (rootParty != null && rootParty.getRelatedPartyList() != null) {
        List<PartyToPartyRelationship> relatedPartyList = rootParty.getRelatedPartyList().stream()
            .filter(relatedParty -> relatedParty.getRoleInParty().getPartyID().equals(currentParty.getPartyID()))
            .collect(Collectors.toList());

        for (PartyToPartyRelationship partyRelationship : relatedPartyList) {
            if (checkOwnershipType(partyRelationship.getPartyRelationshipType().getName())) {
                RelationshipDetails relationshipDetail = createRelationshipDetails(partyRelationship);
                relationshipDetailList.add(relationshipDetail);
            }
        }
    }

    // Convert the relationships into flatList and add them to the PartyDTO
    if (currentParty.getRelatedPartyList() != null) {
        for (PartyToPartyRelationship partyRelationship : currentParty.getRelatedPartyList()) {
            Party relatedParty = partyRelationship.getRoleToParty();
            if (relatedParty != null) {
                List<String> existingParties = partyVisualizationList.stream()
                    .map(PartyVisualization::getPartyID)
                    .collect(Collectors.toList());

                if (!existingParties.contains(relatedParty.getPartyID())) {
                    processPartyHierarchy(rootParty, relatedParty, currentParty.getPartyID(), partyVisualizationList);
                }
            }
        }
    }

    List<PartyRelationship> partyRelationships = new ArrayList<>();
    PartyRelationship partyRelationship = new PartyRelationship();
    partyRelationship.setRelationships(relationshipDetailList);
    partyRelationships.add(partyRelationship);
    partyDetails.setPartyRelationships(partyRelationships);
}

// Helper methods for creating PartyVisualization and RelationshipDetails
private PartyVisualization createPartyVisualization(Party party) {
    // Your implementation for creating PartyVisualization
    // ...
}

private RelationshipDetails createRelationshipDetails(PartyToPartyRelationship partyRelationship) {
    // Your implementation for creating RelationshipDetails
    // ...
}



--------------

private static PartyVisualization createPartyVisualization(Party party) {
    if (party == null) {
        return null;
    }

    PartyVisualization partyDetails = new PartyVisualization();

    // Use Optional to handle potential null values
    partyDetails.setPartyType(Optional.ofNullable(party.getPartyType()).map(Enum::name).orElse(null));
    partyDetails.setPartyName(Optional.ofNullable(party.getPartyNameList())
                                      .filter(names -> !names.isEmpty())
                                      .map(names -> names.get(0).getName())
                                      .orElse(null));
    partyDetails.setAlias(Optional.ofNullable(party.getPartyAliasList())
                                  .filter(aliases -> !aliases.isEmpty())
                                  .map(aliases -> aliases.get(0).getAlias())
                                  .orElse(null));
    partyDetails.setCountryOfDomicile(Optional.ofNullable(party.getCountryOfDomicile()).map(Country::getName).orElse(null));
    partyDetails.setCountryOfOrganization(Optional.ofNullable(party.getCountryOfOrganization()).map(Country::getName).orElse(null));
    partyDetails.setDateOfBirth(Optional.ofNullable(party.getDateOfBirth()).map(LocalDate::toString).orElse(null));
    partyDetails.setIncorporationDate(Optional.ofNullable(party.getFormationDate()).map(LocalDate::toString).orElse(null));
    partyDetails.setLegalForm(Optional.ofNullable(party.getLegalForm()).map(Form::getName).orElse(null));
    partyDetails.setValidationStatus(Optional.ofNullable(party.getPartyValidationStatus()).map(Status::getName).orElse(null));

    // Populate other fields similarly
    // ...

    // Set country-specific identifiers
    partyDetails.setCountrySpecificIdentifiers(getCountrySpecificIdentifier(party));

    return partyDetails;
}

Reviewing the `getPartyDto` method:

### Observations and Recommendations:

1. **Null Checks**: Ensure null checks are concise and handle all potential null pointers. If `party` or any of its fields can be null, consider using `Optional` to handle null values gracefully.
2. **Method Naming**: The method name `getPartyDto` suggests it returns a DTO object, which it does. However, if the DTO class is `PartyVisualization`, consider renaming the method to `createPartyVisualization` for clarity.
3. **Magic Numbers**: Avoid magic numbers like `.get(0)`. If the list is expected to have only one element, consider verifying the list size or refactoring the method that constructs the list.
4. **Field Assignment**: The current approach is clear, but consider using a builder pattern if the `PartyVisualization` class supports it. This can make the code more readable and maintainable.

### Updated Code Example:
```java
private static PartyVisualization createPartyVisualization(Party party) {
    if (party == null) {
        return null;
    }

    PartyVisualization partyDetails = new PartyVisualization();

    // Use Optional to handle potential null values
    partyDetails.setPartyType(Optional.ofNullable(party.getPartyType()).map(Enum::name).orElse(null));
    partyDetails.setPartyName(Optional.ofNullable(party.getPartyNameList())
                                      .filter(names -> !names.isEmpty())
                                      .map(names -> names.get(0).getName())
                                      .orElse(null));
    partyDetails.setAlias(Optional.ofNullable(party.getPartyAliasList())
                                  .filter(aliases -> !aliases.isEmpty())
                                  .map(aliases -> aliases.get(0).getAlias())
                                  .orElse(null));
    partyDetails.setCountryOfDomicile(Optional.ofNullable(party.getCountryOfDomicile()).map(Country::getName).orElse(null));
    partyDetails.setCountryOfOrganization(Optional.ofNullable(party.getCountryOfOrganization()).map(Country::getName).orElse(null));
    partyDetails.setDateOfBirth(Optional.ofNullable(party.getDateOfBirth()).map(LocalDate::toString).orElse(null));
    partyDetails.setIncorporationDate(Optional.ofNullable(party.getFormationDate()).map(LocalDate::toString).orElse(null));
    partyDetails.setLegalForm(Optional.ofNullable(party.getLegalForm()).map(Form::getName).orElse(null));
    partyDetails.setValidationStatus(Optional.ofNullable(party.getPartyValidationStatus()).map(Status::getName).orElse(null));

    // Populate other fields similarly
    // ...

    // Set country-specific identifiers
    partyDetails.setCountrySpecificIdentifiers(getCountrySpecificIdentifier(party));

    return partyDetails;
}
```

### Summary of Changes:

1. **Use of `Optional`**: Wrapped potential null values in `Optional` to streamline null checks and default values.
2. **Improved Method Naming**: Renamed the method to `createPartyVisualization` for clarity.
3. **Validation**: Ensured list accesses are guarded against potential null pointers or empty lists.

These changes enhance the code's readability, maintainability, and robustness against null pointer exceptions.



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationshipVisualizationService {
    private static final Logger logger = LoggerFactory.getLogger(RelationshipVisualizationService.class);
    private final CodaQueryClient codaQueryClient;

    public RelationshipVisualizationService(CodaQueryClient codaQueryClient) {
        this.codaQueryClient = codaQueryClient;
    }

    /**
     * Retrieves a list of PartyVisualization objects by the given facing client ID.
     *
     * @param facingClient the ID of the facing client
     * @return a list of PartyVisualization objects
     */
    public List<PartyVisualization> getPartyVisualizationById(String facingClient) {
        List<Party> relatedParty;
        try {
            relatedParty = codaQueryClient.getPartiesWithAttributesPOS(facingClient,
                Stream.of(MAIN_PARTY_ATTRIBUTES_FOR_MAIN_PTY, RELATED_PARTY1_ATTRIBUTES_FOR_MAIN_PTY, 
                          RELATED_PARTY2_ATTRIBUTES_FOR_MAIN_PTY, COUNTRY_SPECIFIC_IDENTIFIER)
                      .flatMap(Collection::stream)
                      .collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("Error retrieving parties for facing client: {}", facingClient, e);
            return Collections.emptyList();
        }

        // Use stream to find the first element if present
        Party party = relatedParty.stream().findFirst().orElse(null);
        List<PartyVisualization> flatList = new ArrayList<>();

        if (party == null) {
            return flatList;
        }

        // Convert the main party into a flat list including nested related parties
        processParty(party, party, null, flatList);
        Collections.reverse(flatList);

        return flatList;
    }

    private void processParty(Party mainParty, Party parentParty, String parentPartyId, List<PartyVisualization> flatList) {
        if (mainParty == null) {
            return;
        }
        // Add the main party to the flat list
        PartyVisualization partyDetails = new PartyVisualization();
        partyDetails.setParentId(parentPartyId);
        flatList.add(partyDetails);

        List<RelationshipDetails> relationshipDetailsList = mainParty.getRelatedParties();
        if (relationshipDetailsList == null || relationshipDetailsList.isEmpty()) {
            return;
        }

        for (RelationshipDetails details : relationshipDetailsList) {
            Party relatedParty = details.getRelatedParty();
            if (relatedParty != null) {
                processParty(relatedParty, mainParty, mainParty.getId(), flatList);
            }
        }
    }
}



public List<PartyVisualization> getPartyVisualizationById(String facingClient) {
    List<Party> relatedParty = codaQueryClient.getPartiesWithAttributesPOS(facingClient,
        Stream.of(MAIN_PARTY_ATTRIBUTES_FOR_MAIN_PTY, RELATED_PARTY1_ATTRIBUTES_FOR_MAIN_PTY, 
                  RELATED_PARTY2_ATTRIBUTES_FOR_MAIN_PTY, COUNTRY_SPECIFIC_IDENTIFIER)
              .flatMap(Collection::stream)
              .collect(Collectors.toList()));

    // Use stream to find the first element if present
    Party party = relatedParty.stream().findFirst().orElse(null);
    List<PartyVisualization> flatList = new ArrayList<>();

    if (party == null) {
        return flatList;
    }

    // Convert the main party into a flat list including nested related parties
    processParty(party, party, null, flatList);
    Collections.reverse(flatList);

    return flatList;
}

Understood. To avoid directly using `get(0)`, we can use a more functional approach with the `stream` API, which provides a more elegant and readable way to handle the retrieval of the first element.

Here's how you can rewrite the code:

```java
public List<PartyVisualization> getPartyVisualizationById(String facingClient) {
    List<Party> relatedParty = codaQueryClient.getPartiesWithAttributesPOS(facingClient,
        Stream.of(MAIN_PARTY_ATTRIBUTES_FOR_MAIN_PTY, RELATED_PARTY1_ATTRIBUTES_FOR_MAIN_PTY, 
                  RELATED_PARTY2_ATTRIBUTES_FOR_MAIN_PTY, COUNTRY_SPECIFIC_IDENTIFIER)
              .flatMap(Collection::stream)
              .collect(Collectors.toList()));

    // Use stream to find the first element if present
    Party party = relatedParty.stream().findFirst().orElse(null);
    List<PartyVisualization> flatList = new ArrayList<>();

    if (party == null) {
        return flatList;
    }

    // Convert the main party into a flat list including nested related parties
    processParty(party, party, null, flatList);
    Collections.reverse(flatList);

    return flatList;
}
```

Changes made:
1. Replaced `relatedParty.get(0)` with `relatedParty.stream().findFirst().orElse(null)` which avoids directly accessing the list index and provides a more functional programming approach.
2. This approach also handles the case where the list might be empty gracefully.

This way, the code becomes more readable and avoids the direct use of hardcoded indices.
