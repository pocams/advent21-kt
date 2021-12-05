import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Advent of Code 2021",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        MaterialTheme {
            Box(Modifier.fillMaxSize()) {
                Day1Screen()
            }
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
