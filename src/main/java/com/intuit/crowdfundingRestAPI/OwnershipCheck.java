public static boolean isOwnershipType(String relationshipTypeId) {
    return Arrays.stream(P2PHierarchyRelationshipType.values())
                 .anyMatch(type -> type.ownershipTypeFlag && type.relationshipTypeId.equals(relationshipTypeId));
}


public static boolean isOwnershipType(String relationshipTypeId) {
    return Arrays.stream(P2PHierarchyRelationshipType.values())
                 .anyMatch(type -> type.isOwnershipTypeFlag() && type.getRelationshipTypeId().equals(relationshipTypeId));
}
