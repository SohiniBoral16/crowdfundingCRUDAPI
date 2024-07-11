// Apply the SonarQube plugin
plugins {
    id 'org.sonarqube' version '4.3.0.3225'
}

// SonarQube configuration
sonarqube {
    properties {
        property "sonar.projectKey", "yourProjectKey"
        property "sonar.organization", "yourOrganization"
        property "sonar.host.url", "https://your-remote-sonarqube-server-url"
        property "sonar.login", "yourAuthenticationToken" // Use an authentication token if required
        property "sonar.sources", "src/main/java"
        property "sonar.tests", "src/test/java"
        property "sonar.java.binaries", "build/classes"
    }
}

// Ensure the analysis task runs before the build task
tasks.build {
    dependsOn "sonarqube"
}


----_---------------

@RestController
@RequestMapping("/api")
public class RelationshipVisualizationController {

    private final RelationshipVisualizationService relationshipVisualizationService;

    public RelationshipVisualizationController(RelationshipVisualizationService relationshipVisualizationService) {
        this.relationshipVisualizationService = relationshipVisualizationService;
    }

    @GetMapping("/relationship-visualization/{id}")
    public ResponseEntity<RelationshipVisualization> getRelationshipVisualization(
            @PathVariable("id") String id, 
            HttpServletRequest request) throws ServiceException {

        try {
            LOG.info("getRelationshipVisualization called with ID={}", id);
            AuthUtils.getAuthUser(request);

            RelationshipVisualization relationshipVisualization = relationshipVisualizationService.getRelationshipVisualizationById(id);

            return ResponseEntity.ok(relationshipVisualization);
        } catch (Exception ex) {
            LOG.error("Error fetching relationship visualization for ID={}", id, ex);
            throw new ServiceException("Failed to get relationship visualization", ex);
        }
    }
}
