plugins {
  id("buildlogic.kotlin-application-conventions")
}

dependencies {
  implementation(project(":core"))
  testImplementation(kotlin("test"))
}

application {
  mainClass.set("ru.kislball.machikoro.cli.MainKt")
}
