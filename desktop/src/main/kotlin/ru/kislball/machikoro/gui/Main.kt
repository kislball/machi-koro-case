package ru.kislball.machikoro.gui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Test"
    ) {
        App()
    }
}

@Composable
fun App() {
    Text("Hello, Compose Desktop!")
}