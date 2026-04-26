plugins {
  id("buildlogic.kotlin-application-conventions")
  id("com.gradleup.shadow") version "9.4.1"
}

dependencies {
  implementation(project(":core"))
  testImplementation(kotlin("test"))
}

application { mainClass.set("ru.kislball.machikoro.cli.MainKt") }
