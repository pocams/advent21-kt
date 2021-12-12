import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

enum class Screen {
    Menu,
    Day1,
    Day4,
    Day5,
    Day11
}

@Composable
fun menu(onScreenChange: (Screen) -> Unit) {
    Column (Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Text("Advent of Code 2021", fontSize = 32.sp)

        Button(onClick = { onScreenChange(Screen.Day1) }) {
            Text("Day 1")
        }

        Button(onClick = { onScreenChange(Screen.Day4) }) {
            Text("Day 4")
        }

        Button(onClick = { onScreenChange(Screen.Day5) }) {
            Text("Day 5")
        }

        Button(onClick = { onScreenChange(Screen.Day11) }) {
            Text("Day 11")
        }
    }
}

@Composable
fun topLevel() {
    var screen by remember { mutableStateOf(Screen.Menu) }

    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(screen == Screen.Menu, enter = slideInHorizontally(initialOffsetX = { -it }), exit = slideOutHorizontally(targetOffsetX = { -it })) {
            menu(onScreenChange = { screen = it })
        }

        AnimatedVisibility(screen == Screen.Day1, enter = slideInHorizontally(initialOffsetX = { it }), exit = slideOutHorizontally(targetOffsetX = { it })) {
            Day1Screen { screen = Screen.Menu }
        }

        AnimatedVisibility(screen == Screen.Day4, enter = slideInHorizontally(initialOffsetX = { it }), exit = slideOutHorizontally(targetOffsetX = { it })) {
            Day4Screen { screen = Screen.Menu }
        }

        AnimatedVisibility(screen == Screen.Day5, enter = slideInHorizontally(initialOffsetX = { it }), exit = slideOutHorizontally(targetOffsetX = { it })) {
            Day5Screen { screen = Screen.Menu }
        }

        AnimatedVisibility(screen == Screen.Day11, enter = slideInHorizontally(initialOffsetX = { it }), exit = slideOutHorizontally(targetOffsetX = { it })) {
            Day11Screen { screen = Screen.Menu }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Advent of Code 2021",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        MaterialTheme {
            topLevel()
        }
    }
}

//         val count = remember { mutableStateOf(0) }
//            Row {
//                Column {
//                    Button(modifier = Modifier.align(Alignment.CenterHorizontally),
//                        onClick = {
//                            count.value++
//                        }) {
//                        Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
//                    }
//                    Button(modifier = Modifier.align(Alignment.CenterHorizontally),
//                        onClick = {
//                            count.value = 0
//                        }) {
//                        Text("Reset")
//                    }
//                    Day1Canvas(listOf(1, 2, 3, 4, 5, 4, 3, 2, 1, 0))
//                }
//
//                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
//                    Card(Modifier.fillMaxWidth(), elevation = 8.dp) { Text("Hello") }
//                    Card(elevation = 8.dp) { Text("One") }
//                    Card(elevation = 8.dp) { Text("Two") }
//                    Card(elevation = 8.dp) { Text("Three") }
//                }
//            }
//        }
//    }
