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
