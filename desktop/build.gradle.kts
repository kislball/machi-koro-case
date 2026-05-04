import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
  id("buildlogic.kotlin-common-conventions")
  id("com.gradleup.shadow") version "9.4.1"
  id("org.jetbrains.compose") version "1.10.0"
  id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
  implementation(project(":core"))
  implementation(compose.desktop.currentOs)
  implementation(compose.material3)
  implementation(compose.materialIconsExtended)
  testImplementation(kotlin("test"))
}

extensions.configure<DetektExtension> {
  config.setFrom(
      files(
          "${rootProject.projectDir}/config/detekt/detekt.yml",
          "${projectDir}/detekt.yml",
      ))
}

compose.desktop { application { mainClass = "ru.kislball.machikoro.gui.MainKt" } }
