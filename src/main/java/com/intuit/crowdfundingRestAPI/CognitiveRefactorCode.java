
ext.versions = new Properties()
file('versions.properties').withInputStream { stream ->
    ext.versions.load(stream)
}


import java.util.Properties

// Load versions from the properties file
def loadProperties(fileName) {
    def properties = new Properties()
    file(fileName).withInputStream { stream ->
        properties.load(stream)
    }
    return properties
}

def versions = loadProperties('versions.properties')

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath "org.springframework.boot:spring-boot-gradle-plugin:${versions['springBootVersion']}"
        classpath "org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:${versions['jsonSchema2PojoVersion']}"
    }
}

plugins {
    id 'application'
    id 'org.springframework.boot' version "${versions['springBootVersion']}"
    id 'io.spring.dependency-management' version "${versions['dependencyManagementVersion']}"
    id 'java'
}

apply plugin: 'jsonschema2pojo'

jacoco {
    toolVersion = "0.8.7"
}

group = 'com.ms.clientData'
version = '1.0'
sourceCompatibility = '11'

ext {
    set('springCloudVersion', '2020.0.2')
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






// Load versions from the properties file
def versions = new Properties()
file('versions.properties').withInputStream { stream ->
    versions.load(stream)
}

buildscript {
    dependencies {
        classpath "org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:1.1.0"
    }
}

plugins {
    id 'application'
    id 'org.springframework.boot' version "${versions['springBootVersion']}"
    id 'io.spring.dependency-management' version "${versions['dependencyManagementVersion']}"
    id 'java'
    id 'com.ms.gradle.train-metadata' version "${versions['trainMetadataVersion']}"
    id 'com.ms.gradle.afs-install' version "${versions['afsInstallVersion']}"
    id 'com.ms.gradle.runscripts' version "${versions['runScriptsVersion']}"
    id 'jacoco'
    id 'org.barfuin.gradle.jacocolog' version "${versions['jacocoLogVersion']}"
    id 'com.google.cloud.tools.jib' version "${versions['jibVersion']}"
}

jacoco {
    toolVersion = "${versions['jacocoVersion']}"
}

group = 'com.ms.clientData'
version = '1.0'
sourceCompatibility = "${versions['javaVersion']}"
apply from: file("${rootProject.projectDir}/docker.gradle")

ext {
    set('springCloudVersion', "${versions['springCloudVersion']}")
    imagePackageDir = "./packages/docker/${account}/${application}/${version}/"
    trainDir = "${imagePackageDir}/metadata/"
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
    compileOnly {
        extendsFrom annotationProcessor
    }
}


springBootVersion=2.5.13
dependencyManagementVersion=1.0.11.RELEASE
javaVersion=11
trainMetadataVersion=1.0.0
afsInstallVersion=1.0.0
runScriptsVersion=1.0.0
jacocoVersion=0.8.11
jacocoLogVersion=3.1.0
jibVersion=1.0.0
springCloudVersion=2020.0.2

// Load versions from the properties file
def versions = new Properties()
file('versions.properties').withInputStream { versions.load(it) }

buildscript {
    dependencies {
        classpath "org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:1.1.0"
    }
}

plugins {
    id 'application'
    id 'org.springframework.boot' version "${versions.springBootVersion}"
    id 'io.spring.dependency-management' version "${versions.dependencyManagementVersion}"
    id 'java'
    id 'com.ms.gradle.train-metadata' version "${versions.trainMetadataVersion}"
    id 'com.ms.gradle.afs-install' version "${versions.afsInstallVersion}"
    id 'com.ms.gradle.runscripts' version "${versions.runScriptsVersion}"
    id 'jacoco'
    id 'org.barfuin.gradle.jacocolog' version "${versions.jacocoLogVersion}"
    id 'com.google.cloud.tools.jib' version "${versions.jibVersion}"
}

jacoco {
    toolVersion = "${versions.jacocoVersion}"
}

group = 'com.ms.clientData'
version = '1.0'
sourceCompatibility = "${versions.javaVersion}"
apply from: file("${rootProject.projectDir}/docker.gradle")

ext {
    set('springCloudVersion', "${versions.springCloudVersion}")
    imagePackageDir = "./packages/docker/${account}/${application}/${version}/"
    trainDir = "${imagePackageDir}/metadata/"
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
    compileOnly {
        extendsFrom annotationProcessor
    }
}
