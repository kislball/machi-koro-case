import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
  id("com.diffplug.spotless") version "7.0.4" apply false
  id("io.gitlab.arturbosch.detekt") version "1.23.7" apply false
}

subprojects {
  pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    pluginManager.apply("com.diffplug.spotless")
    pluginManager.apply("io.gitlab.arturbosch.detekt")

    extensions.configure<SpotlessExtension> {
      kotlin {
        target("src/**/*.kt")
        ktfmt()
      }
      kotlinGradle {
        target("*.gradle.kts")
        ktfmt()
      }
    }

    extensions.configure<DetektExtension> {
      toolVersion = "1.23.7"
      buildUponDefaultConfig = true
      config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
    }

    tasks.named("check") {
      dependsOn("spotlessCheck", "detekt")
    }
  }
}
