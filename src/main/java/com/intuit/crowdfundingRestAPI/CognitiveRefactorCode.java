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
