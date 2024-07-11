
plugins {
    id 'org.springframework.boot' version "${springBootVersion}"
    id 'io.spring.dependency-management' version "${dependencyManagementVersion}"
    id 'java'
    id 'jacoco'
    id 'com.gradle.plugin-publish' version '0.10.1'
    id 'com.bmuschko.docker-java-application' version '6.6.1'
    id 'org.sonarqube' version '4.3.0.3225' // Adding SonarQube plugin
    id 'com.google.cloud.tools.jib' version '3.1.4'
}

sonarqube {
    properties {
        property "sonar.projectKey", "clientData/pipex"
        property "sonar.projectName", "Pipex"
        property "sonar.language", "java"
        property "sonar.scm.provider", "git"
        property "sonar.instance.name", "sonar-prod:sonarprod"
        property "sonar.host.url", "https://sonarqube.example.com" // Updated to remote server URL
        property "sonar.login", "yourAuthenticationToken" // Ensure you have the correct authentication token if required
        property "sonar.java.libraries", "/ms/dist/oss/java/PROJ/lombok/1.18.10/lib/*.jar"
        
        property "sonar.sources", "src/main/java"
        property "sonar.tests", "src/test/java"
        property "sonar.java.libraries", "build/lib/p2pservice*.jar"
        property "sonar.java.binaries", "build/classes"
        property "sonar.junit.reportPaths", "build/test-results/test"
        property "sonar.jacoco.reportPaths", "build/jacoco/test.exec"
        property "sonar.coveragePlugin", "jacoco"
        property "sonar.exclusions", "**/generated/**,**/config/**,**/enum/**,**/repository/**,**/util/**,**/security/**"
        property "sonar.inclusions", "**/constants/**,**/P2PServiceApplication.java"
        property "sonar.scm.provider", "git"
    }
}

// Ensure the analysis task runs before the build task
tasks.build {
    dependsOn "sonarqube"
}


-----------------------------------
     
     plugins {
    id 'org.springframework.boot' version "${springBootVersion}"
    id 'io.spring.dependency-management' version "${dependencyManagementVersion}"
    id 'java'
    id 'jacoco'
    id 'com.gradle.plugin-publish' version '0.10.1'
    id 'com.bmuschko.docker-java-application' version '6.6.1'
    id 'org.sonarqube' version '4.3.0.3225' // Adding SonarQube plugin
    id 'com.google.cloud.tools.jib' version '3.1.4'
}

sonarqube {
    properties {
        property "sonar.projectKey", "clientData/pipex"
        property "sonar.projectName", "Pipex"
        property "sonar.language", "java"
        property "sonar.scm.provider", "git"
        property "sonar.instance.name", "sonar-prod:sonarprod"
        property "sonar.java.libraries", "/ms/dist/oss/java/PROJ/lombok/1.18.10/lib/*.jar"
        property "sonar.sources", "src/main/java"
        property "sonar.tests", "src/test/java"
        property "sonar.java.libraries", "build/lib/p2pservice*.jar"
        property "sonar.java.binaries", "build/classes"
        property "sonar.junit.reportPaths", "build/test-results/test"
        property "sonar.jacoco.reportPaths", "build/jacoco/test.exec"
        property "sonar.coveragePlugin", "jacoco"
        property "sonar.exclusions", "**/generated/**,**/config/**,**/enum/**,**/repository/**,**/util/**,**/security/**"
        property "sonar.inclusions", "**/constants/**,**/P2PServiceApplication.java"
        property "sonar.scm.provider", "git"
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
