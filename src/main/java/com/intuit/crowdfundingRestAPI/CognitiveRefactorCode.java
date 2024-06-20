To add the JaCoCo plugin in an IntelliJ IDEA project, you'll need to update your `build.gradle` file and configure IntelliJ IDEA to use it. Here's how you can do it:

### Step 1: Update `build.gradle`

Add the JaCoCo plugin and configure it in your `build.gradle` file:

```groovy
plugins {
    id 'org.springframework.boot' version '3.0.0'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
    id 'java'
    id 'jacoco'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    runtimeOnly 'com.h2database:h2'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}

bootJar {
    mainClassName = 'com.example.demo.DemoApplication'
}

jacoco {
    toolVersion = "0.8.8" // Use the appropriate version of JaCoCo
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}
```

### Step 2: Sync Gradle Project

After updating `build.gradle`, you need to refresh your Gradle project in IntelliJ IDEA:

1. Open your project in IntelliJ IDEA.
2. In the right-hand side panel, find the "Gradle" tool window.
3. Click the "Refresh" button to sync the project with the updated `build.gradle` file.

### Step 3: Run Tests with JaCoCo

To generate the JaCoCo test coverage report:

1. Run your tests using Gradle tasks. You can do this by opening the "Gradle" tool window, navigating to `Tasks > verification`, and double-clicking `test`.
2. After running the tests, generate the JaCoCo report by double-clicking `jacocoTestReport` in the same "Gradle" tool window.

### Step 4: View JaCoCo Reports

The generated JaCoCo reports will be available in the `build/reports/jacoco/test/html` directory. You can open the `index.html` file in a web browser to view the test coverage report.

By following these steps, you will have integrated the JaCoCo plugin into your Spring Boot project and configured it in IntelliJ IDEA.
