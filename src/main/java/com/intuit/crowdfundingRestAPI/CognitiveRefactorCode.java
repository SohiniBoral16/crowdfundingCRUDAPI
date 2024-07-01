buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath "org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}"
        classpath "org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:${jsonSchema2PojoVersion}"
    }
}

plugins {
    id 'application'
    id 'org.springframework.boot' version "${springBootVersion}"
    id 'io.spring.dependency-management' version "${dependencyManagementVersion}"
    id 'java'
    id 'com.ms.gradle.train-metadata' version "${trainMetadataVersion}"
    id 'com.ms.gradle.afs-install' version "${afsInstallVersion}"
    id 'com.ms.gradle.runscripts' version "${runScriptsVersion}"
    id 'jacoco'
    id 'org.barfuin.gradle.jacocolog' version "${jacocoLogVersion}"
    id 'com.google.cloud.tools.jib' version "${jibVersion}"
}

jacoco {
    toolVersion = "${jacocoVersion}"
}

group = 'com.ms.clientData'
version = '1.0'
sourceCompatibility = "${javaVersion}"

ext {
    set('springCloudVersion', "${springCloudVersion}")
}

repositories {
    mavenCentral()
}

dependencies {
    // Runtime environment
    implementation "org.glassfish.jaxb:jaxb-runtime:${glassfishJaxbVersion}"

    // Sub Modules
    implementation project(':eventschema')
    implementation project(':json')
    implementation "com.ms.clientdata:objectmodel:2025.05.21"

    // MS
    implementation "com.ms.infra.pz:kerberos:2024.05.03"
    implementation "com.ms.infra.pz:saml:2024.05.03"

    // Spring dependencies
    implementation "org.springframework.boot:spring-boot-starter-web:${springBootStarterWebVersion}"
    implementation "org.springframework.boot:spring-boot-starter-security:${springBootStarterSecurityVersion}"
    implementation "org.springframework.boot:spring-boot-starter-actuator:${springBootStarterActuatorVersion}"
    implementation "org.springframework.boot:spring-boot-starter-validation:${springBootStarterValidationVersion}"
    implementation "org.springframework.boot:spring-boot-starter-swagger:${springBootStarterSwaggerVersion}"

    // Swagger dependencies
    implementation "io.springfox:springfox-swagger2:${swagger2Version}"
    implementation "io.springfox:springfox-swagger-ui:${swaggerUiVersion}"

    // Curator dependencies
    implementation("org.apache.curator:curator-recipes:${curatorRecipesVersion}") {
        exclude group: 'org.apache.zookeeper', module: 'zookeeper'
    }
    implementation("org.apache.curator:curator-framework:${curatorFrameworkVersion}") {
        exclude group: 'org.apache.zookeeper', module: 'zookeeper'
    }

    // Spring Cloud dependencies
    implementation "org.springframework.cloud:spring-cloud-starter-config:${springCloudStarterConfigVersion}"
    implementation "org.springframework.cloud:spring-cloud-starter-eureka:${springCloudStarterEurekaVersion}"
    implementation "org.springframework.cloud:spring-cloud-starter-bootstrap:${springCloudStarterBootstrapVersion}"

    // Micrometer dependencies
    implementation "io.micrometer:micrometer-registry-prometheus:${micrometerRegistryPrometheusVersion}"

    // JSON processing dependencies
    implementation "com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:${jacksonJaxbJsonProviderVersion}"

    // Testing dependencies
    testImplementation "org.springframework.boot:spring-boot-starter-test:${springBootStarterTestVersion}"
    testImplementation "org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}"
    testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}"
    testImplementation "org.mockito:mockito-junit-jupiter:${mockitoJUnitJupiterVersion}"
    testImplementation "org.hamcrest:hamcrest:${hamcrestVersion}"
    testImplementation "org.mock-server:mockserver-netty:${mockServerVersion}"
}

apply from: file("${rootProject.projectDir}/docker.gradle")

jsonSchema2Pojo {
    source = files('src/main/resources/schemas') // Path to your JSON Schema files
    targetPackage = 'com.example.generated'      // Package for the generated classes
}




springBootVersion=2.5.13
dependencyManagementVersion=1.0.11.RELEASE
jsonSchema2PojoVersion=1.1.0
jacocoVersion=0.8.11
jacocoLogVersion=3.1.0
springCloudVersion=2020.0.2
trainMetadataVersion=1.0.0
afsInstallVersion=1.0.0
runScriptsVersion=1.0.0
jibVersion=1.0.0
javaVersion=11

# Runtime environment
glassfishJaxbVersion=2.3.2

# Spring dependencies
springBootStarterWebVersion=2.5.13
springBootStarterSecurityVersion=2.5.13
springBootStarterActuatorVersion=2.5.13
springBootStarterValidationVersion=2.5.13
springBootStarterSwaggerVersion=2.5.13

# Swagger dependencies
swaggerUiVersion=2.9.2
swagger2Version=2.9.2

# Curator dependencies
curatorRecipesVersion=5.0.0
curatorFrameworkVersion=5.0.0

# Spring Cloud dependencies
springCloudStarterConfigVersion=2020.0.2
springCloudStarterEurekaVersion=2020.0.2
springCloudStarterBootstrapVersion=2020.0.2

# Micrometer dependencies
micrometerRegistryPrometheusVersion=1.7.2

# JSON processing dependencies
jacksonJaxbJsonProviderVersion=2.9.0

# Testing dependencies
springBootStarterTestVersion=2.5.13
junitJupiterVersion=5.7.1
mockitoJUnitJupiterVersion=3.9.0
hamcrestVersion=2.2
mockServerVersion=5.11.2
