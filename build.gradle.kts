import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.loom)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.detekt)
  `maven-publish`
}

version = providers.gradleProperty("modVersion").get()
group = providers.gradleProperty("baseGroup").get()

base {
  archivesName = providers.gradleProperty("modName").get()
}

repositories {
  mavenCentral()
}

dependencies {
  minecraft(libs.minecraft)
  implementation(libs.bundles.fabric)

  implementation(libs.skija.shared) { include(this) }
  runtimeOnly(libs.bundles.skija.natives) { include(this) }
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
}
