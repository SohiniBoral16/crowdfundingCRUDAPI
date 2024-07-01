buildscript {
    repositories {
        mavenCentral()
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
}

apply plugin: 'jsonschema2pojo'

jacoco {
    toolVersion = "${jacocoVersion}"
}

group = 'com.ms.clientData'
version = '1.0'
sourceCompatibility = '11'

ext {
    set('springCloudVersion', "${springCloudVersion}")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

apply from: file("${rootProject.projectDir}/docker.gradle")

jsonSchema2Pojo {
    source = files('src/main/resources/schemas') // Path to your JSON Schema files
    targetPackage = 'com.example.generated'      // Package for the generated classes
}
