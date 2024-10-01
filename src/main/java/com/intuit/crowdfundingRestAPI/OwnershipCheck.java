
mainParty.getRelatedPartyList().forEach(rp -> {
    String relationshipTypeId = rp.getPartyRelationshipType().getID();

    // Skip if the relationship type is an ownership type
    if (P2PHierarchyRelationshipType.isOwnershipType(relationshipTypeId)) {
        return;
    }

    // Process other relationships
    Optional.of(relationshipTypeId)
            .filter(id -> P2P_CORS_EXCLUDED_RELATIONSHIPS.contains(id))
            .ifPresentOrElse(
                id -> p2PParty.getRelatedPartyList().add(convertRelatedParty(rp)),
                () -> {
                    if (P2PFinancialRegulationRelationship.equals(relationshipTypeId)) {
                        p2PParty.getFinancialRegulatorList().add(convert(rp.getRole2party()));
                    }
                }
            );
});



public static boolean isOwnershipType(String relationshipTypeId) {
    return Arrays.stream(P2PHierarchyRelationshipType.values())
                 .anyMatch(type -> type.ownershipTypeFlag && type.relationshipTypeId.equals(relationshipTypeId));
}


public static boolean isOwnershipType(String relationshipTypeId) {
    return Arrays.stream(P2PHierarchyRelationshipType.values())
                 .anyMatch(type -> type.isOwnershipTypeFlag() && type.getRelationshipTypeId().equals(relationshipTypeId));
}
