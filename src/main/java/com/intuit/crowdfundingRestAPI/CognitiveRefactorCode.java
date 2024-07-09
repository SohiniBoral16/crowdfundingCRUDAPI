import java.util.List;

public class Party {
    private String partyId;
    private String parentId;
    private String partyName;
    private String validationStatus;
    private String countryOfOrganization;
    private String legalForm;
    private String partyAlias;
    private String legalName;
    private String countryOfDomicile;
    private String dateOfBirth;
    private String dateOfIncorporation;
    private List<Identifier> countrySpecificIdentifiers;
    private List<Relationship> relationships;

    // Getters and setters
    // Constructors
}

class Identifier {
    private String identifierId;
    private String identifierName;

    // Getters and setters
    // Constructors
}

class Relationship {
    private String parentId;
    private List<RelationshipDetail> relationshipDetails;

    // Getters and setters
    // Constructors
}

class RelationshipDetail {
    private String relationshipTypeName;
    private RelationshipAttribute relationshipAttributes;

    // Getters and setters
    // Constructors
}

class RelationshipAttribute {
    private boolean significantInfluenceOverIndicator;
    private String indirectOwnershipValue;
    private String percentageValue;

    // Getters and setters
    // Constructors
}
