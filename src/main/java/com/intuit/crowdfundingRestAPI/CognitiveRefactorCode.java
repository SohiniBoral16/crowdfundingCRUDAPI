plugins {
    id 'org.springframework.boot' version '2.5.13'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
    id 'jacoco'
    id 'org.sonarqube' version '4.0.0.2929' // Add this line to apply the SonarQube plugin
}


sonarqube {
    properties {
        property "sonar.projectKey", "your.project.key"
        property "sonar.projectName", "Your Project Name"
        property "sonar.projectVersion", "1.0"
        property "sonar.sourceEncoding", "UTF-8"
        property "sonar.sources", "src/main/java"
        property "sonar.tests", "src/test/java"
        property "sonar.java.binaries", "build/classes/java/main"
        property "sonar.junit.reportPaths", "build/test-results/test"
        property "sonar.jacoco.reportPaths", "build/jacoco/test.exec"
        // Add any other necessary properties here
    }
}
