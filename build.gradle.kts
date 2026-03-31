import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.loom)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.shadow)
  `maven-publish`
}

version = providers.gradleProperty("modVersion").get()
group = providers.gradleProperty("baseGroup").get()

base {
  archivesName = providers.gradleProperty("modName").get()
}

repositories {
  mavenCentral()
  maven("https://api.modrinth.com/maven")
}

val shadowImpl: Configuration by configurations.creating {
  configurations.implementation.get().extendsFrom(this)
}

dependencies {
  minecraft(libs.minecraft)
  implementation(libs.bundles.fabric)

  shadowImpl(libs.skija.shared)
  shadowImpl(libs.bundles.skija.natives)

  // Additional Mods
  runtimeOnly("maven.modrinth:sodium:mc26.1-0.8.7-fabric")
}

tasks {
  processResources {
    val fabricLoaderVersion = libs.versions.fabric.loader.get()
    val minecraftVersion = libs.versions.minecraft.version.get()

    inputs.property("version", project.version)
    inputs.property("fabricLoaderVersion", fabricLoaderVersion)
    inputs.property("minecraftVersion", minecraftVersion)

    filesMatching("fabric.mod.json") {
      expand(
        "version" to project.version,
        "fabricLoaderVersion" to fabricLoaderVersion,
        "minecraftVersion" to minecraftVersion,
      )
    }
  }

  shadowJar {
    archiveClassifier.set("")
    configurations = listOf(shadowImpl)
  }

  withType(RemapJarTask::class) {
    dependsOn(shadowJar)
    inputFile = shadowJar.get().archiveFile
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.release = 25
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_25
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_25
  targetCompatibility = JavaVersion.VERSION_25
}
