plugins {
    id("java")
}

group = "org.example.my-app"
version = "0.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

dependencies.constraints {
    implementation("org.apache.commons:commons-lang3:3.6!!") // Remove !! (strict version) and this will be upgraded
    implementation("org.apache.commons:commons-text:1.5")
}

val applicationRuntimeClasspath = configurations.create("applicationRuntimeClasspath") {
    isCanBeResolved = true
    isCanBeConsumed = false
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }
}
dependencies {
    applicationRuntimeClasspath("org.example.my-app:app")
}
configurations {
    compileClasspath.get().shouldResolveConsistentlyWith(applicationRuntimeClasspath)
    runtimeClasspath.get().shouldResolveConsistentlyWith(applicationRuntimeClasspath)
    testCompileClasspath.get().shouldResolveConsistentlyWith(applicationRuntimeClasspath)
    testRuntimeClasspath.get().shouldResolveConsistentlyWith(applicationRuntimeClasspath)
}
