plugins {
  id("buildlogic.kotlin-application-conventions")
}

dependencies {
  implementation(project(":core"))
}

application {
  mainClass.set("ru.kislball.machikoro.cli.MainKt")
}
