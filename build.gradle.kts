import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.loom)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.shadow)
  alias(libs.plugins.detekt)
  `maven-publish`
}

version = providers.gradleProperty("modVersion").get()
group = providers.gradleProperty("baseGroup").get()

base {
  archivesName = providers.gradleProperty("modName").get()
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
    }
  }
}

repositories {
  mavenCentral()
  maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val shadowImpl: Configuration by configurations.creating {
  configurations.implementation.get().extendsFrom(this)
}

loom {
  accessWidenerPath = rootProject.file("src/main/resources/cobalt.accesswidener")
}

dependencies {
  minecraft(libs.minecraft)
  implementation(libs.bundles.fabric)

  shadowImpl(libs.skija.shared)
  shadowImpl(libs.bundles.skija.natives)

  runtimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.2")
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

detekt {
  buildUponDefaultConfig = true
  config.setFrom(rootProject.file("config/detekt/detekt.yml"))
  allRules = false
  ignoredBuildTypes = listOf()
}
