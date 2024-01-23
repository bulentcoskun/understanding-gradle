import java.nio.file.Files
import org.gradle.api.plugins.jvm.internal.JvmLanguageUtilities
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("java")
    id("groovy")
}

group = "org.example.my-app"
version = "1.0"

sourceSets.main {
    // java.setSrcDirs(listOf(project.layout.projectDirectory.dir("sources")))
    // resources.setSrcDirs(listOf(project.layout.projectDirectory.dir("resources")))
}

sourceSets.test {

}

val extraFeature = sourceSets.create("extraFeature")
tasks.register<Jar>(extraFeature.jarTaskName) {
    from(extraFeature.output)
    archiveClassifier.set("extra-feature")
}

val jvm = serviceOf<JvmLanguageUtilities>() // if this does not compile, use 'serviceOf<JvmEcosystemUtilities>()'
jvm.registerJvmLanguageSourceDirectory(sourceSets.main.get(), "java17") {
    compiledWithJava {
        javaCompiler.set(javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        })
        classpath = sourceSets.main.get().compileClasspath
        doLast {
            val destRoot = destinationDirectory.get().asFile
            val destVersions = File(destRoot, "META-INF/versions/17").also { it.mkdirs() }
            destVersions.deleteRecursively()
            destVersions.mkdirs()
            destRoot.listFiles()?.forEach {
                if (it.name != "META-INF") {
                    Files.move(it.toPath(), File(destVersions, it.name).toPath())
                }
            }
        }
    }
}

tasks.jar {
    manifest {
        attributes("Multi-Release" to true)
    }
}

dependencies.constraints {
    implementation("org.apache.commons:commons-lang3:3.9")
    add("extraFeatureImplementation", "org.apache.commons:commons-lang3:3.9")
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

