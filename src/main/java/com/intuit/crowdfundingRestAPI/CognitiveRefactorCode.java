buildscript {
    dependencies {
        classpath 'org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:1.1.0'
    }
}

plugins {
    id 'org.springframework.boot' version '2.5.13'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
    id 'com.ms.gradle.train-metadata'
    id 'com.ms.gradle.afs-install'
    id 'com.ms.gradle.runscripts'
    id 'jacoco'
    id 'org.barfuin.gradle.jacocolog' version '3.1.0'
    id 'com.google.cloud.tools.jib'
}

apply plugin: 'application'

//apply plugin: 'jsonschema2pojo'

group = 'com.ms.clientData'
version = '1.0'
sourceCompatibility = '11'

ext {
    set('springCloudVersion', "2020.0.2")
    imagePackageDir = "../packages/docker/$account/$application/$version/"
    trainDir = "$imagePackageDir/metadata"
}

sourceSets {
    integrationTest {
        java.srcDirs = ['src/integrationTest/java']
        resources.srcDirs = ['src/integrationTest/resources']
        compileClasspath += main.output + test.output
        runtimeClasspath += main.output + test.output
    }
}

configurations {
    compileOnly.extendsFrom annotationProcessor
    integrationTestImplementation.extendsFrom testImplementation
    integrationTestRuntime.extendsFrom testRuntime
}

dependencies {
    implementation 'com.ms.clientdata:objectmodel:2023.03.13-1'
    implementation project(':eventschemas')
    implementation project(':gluon')
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-test'
    implementation 'org.springframework.cloud:spring-cloud-config-client'
    implementation 'org.springframework.cloud:spring-cloud-starter'
    implementation 'org.springframework.cloud:spring-cloud-starter-bootstrap'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.12.0'
    implementation 'com.fasterxml.jackson.core:jackson-core:2.12.0'
    implementation 'com.fasterxml.jackson.core:jackson-annotations:2.12.0'
    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.0'
    implementation 'com.googlecode.json-simple:json-simple:1.1.1'
    implementation 'com.netflix.hystrix:hystrix-core:1.5.18'
    implementation 'org.apache.curator:curator-recipes:2.7.1'
    implementation 'org.apache.curator:curator-framework:2.7.1'
    implementation 'org.json:json:20210307'
    implementation 'org.jsonschema2pojo:jsonschema2pojo-core:1.1.0'
    implementation 'org.mapstruct:mapstruct:1.4.2.Final'
    implementation 'org.mapstruct:mapstruct-processor:1.4.2.Final'
    implementation 'io.swagger.core.v3:swagger-annotations:2.1.9'
    implementation 'io.swagger.core.v3:swagger-jaxrs2:2.1.9'
    implementation 'io.swagger.core.v3:swagger-core:2.1.9'
    implementation 'javax.xml.bind:jaxb-api:2.3.1'
    implementation 'javax.activation:activation:1.1.1'
    implementation 'com.sun.xml.bind:jaxb-impl:2.3.3'
    implementation 'com.sun.xml.bind:jaxb-core:2.3.0.1'
    implementation 'javax.annotation:javax.annotation-api:1.3.2'
    implementation 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.1'
    testImplementation 'org.junit.vintage:junit-vintage-engine:5.7.1'
    testRuntimeOnly 'org.junit.vintage:junit-vintage-engine:5.7.1'
    testImplementation 'org.mock-server:mockserver-client-java:5.11.2'
    testImplementation 'org.mock-server:mockserver-netty:5.11.2'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events "passed", "skipped", "failed", "standardOut", "standardError"
    }
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    dependsOn test // tests are required to run before generating the report
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect { fileTree(dir: it, exclude: ['**/generated/**', '**/config/**', '**/model/**', '**/module/**', '**/exception/**', '**/P2PServiceApplication.class']) }))
    }
    finalizedBy copyJacocoTests
}

task copyJUnitTests(type: Copy) {
    dependsOn test
    from "$buildDir/test-results/test"
    into "$trainDir/test-results/UNIT/junit"
}

task copyJacocoTests(type: Copy) {
    dependsOn jacocoTestReport
    from "$buildDir/reports/jacoco"
    into "$trainDir/coverage-results/jacoco"
    finalizedBy copyJUnitTests
}

bootJar {
    archiveBaseName = "pipex-${project.name}"
    destinationDirectory = project.layout.buildDirectory.dir('lib')
    manifest {
        attributes 'Main-Class': 'com.ms.clientData.p2pService.P2PServiceApplication'
    }
}

runscripts {
    runscriptDefaults {
        azuluZOpenJdk = "ms-java/azul/zulu-openjdk/11.0.19/11.0.19+11"
        javaModule = "ms-java/azul/zulu-openjdk/11.0.19/11.0.19+11"
    }
    runscriptGenerationParameters {
        PartyPersistenceServiceApplication {
            jarTaskCreatingApplication = tasks.bootJar
        }
    }
}

task copyArtifacts(type: Copy) {
    from "$
