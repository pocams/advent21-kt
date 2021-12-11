import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File
import java.lang.Float.min

class Day1State {
    var puzzleData: List<Int> = listOf()
    val dataFile get() = if (useRealData) { "data/day1.txt" } else { "data/day1-example.txt" }

    val maxDepth get() = puzzleData.maxOrNull() ?: 1
    val minDepth get() = puzzleData.minOrNull() ?: 0

    var useRealData by mutableStateOf(false)
    var part2 by mutableStateOf(false)
    var selectedIndex by mutableStateOf(0)
    var increasedIndexes = mutableStateListOf<Int>()

    val currentDepth get() = puzzleData[selectedIndex]

    val canStep get() = if (part2) {
        selectedIndex + 3 < puzzleData.count()
    } else {
        selectedIndex + 1 < puzzleData.count()
    }

    init {
        loadData()
    }

    fun colorForIndex(index: Int): Color = when {
        index == selectedIndex -> Colors.Yellow
        index == selectedIndex + 3 && part2 -> Colors.Yellow
        increasedIndexes.contains(index) -> Colors.Green
        selectedIndex > index -> Colors.DarkBlue
        else -> Colors.Blue
    }

    fun loadData() {
        this.reset()
        this.puzzleData = File(dataFile).readLines().map { it.toInt() }
    }

    fun reset() {
        selectedIndex = 0
        increasedIndexes.clear()
    }

    fun step() {
        if (part2) {
            if (selectedIndex + 3 < puzzleData.count()) {
                if (puzzleData[selectedIndex + 3] > currentDepth) {
                    increasedIndexes.add(selectedIndex + 1)
                }
                selectedIndex += 1
            }
        } else {
            if (selectedIndex + 1 < puzzleData.count()) {
                if (puzzleData[selectedIndex + 1] > currentDepth) {
                    increasedIndexes.add(selectedIndex + 1)
                }
                selectedIndex += 1
            }
        }
    }
}

@Composable
fun Day1Screen(goBack: () -> Unit) {
    val state = remember { Day1State() }

    Column(Modifier.fillMaxSize().padding(5.dp)) {
        Box(Modifier.height(80.dp)) {
            Day1Status(state, goBack)
        }

        Box(Modifier.padding(5.dp)) {
            Day1Sonar(state)
        }
    }
}

@Composable
fun Day1Status(state: Day1State, goBack: () -> Unit) {
    val composableScope = rememberCoroutineScope()

    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(modifier = Modifier.fillMaxHeight(.8f), onClick = {
            goBack()
        }) {
            Text("Back")
        }

        Spacer(Modifier.width(20.dp))

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Use real data?")
                Switch(
                    checked = state.useRealData,
                    onCheckedChange = {
                        state.useRealData = it
                        state.loadData()
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Part 2?")
                Switch(
                    checked = state.part2,
                    onCheckedChange = {
                        state.part2 = it
                        state.reset()
                    }
                )
            }
        }

        Button(modifier = Modifier.fillMaxHeight(.8f), onClick = {
            state.reset()
            composableScope.launch {
                while (state.canStep) {
                    yield()
                    state.step()
                }
            }
        }) {
            Text("Scan")
       }
       Card {
           Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
               Text("Depth")
               Spacer(Modifier.height(8.dp))
               Text(state.currentDepth.toString(), fontWeight = FontWeight.Bold)
           }
       }

        Card {
            Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Increases")
                Spacer(Modifier.height(8.dp))
                Text(state.increasedIndexes.count().toString(), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Day1Sonar(state: Day1State) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val minDepthY = 0
        val maxDepthY = this.size.height
        // Increase the range a bit so we always see some kind of bar
        val depthRange = (state.maxDepth - state.minDepth).toFloat() * 1.2f
        val yRange = maxDepthY - minDepthY
        val totalBarWidth = min((this.size.width / state.puzzleData.count().toFloat()), 10f)
        val barWidth = totalBarWidth * 0.75f
        val barSpacing = totalBarWidth * 0.25f

        for ((index, depth) in state.puzzleData.withIndex()) {
            val x = index * barWidth + index * barSpacing
            val percentOfMaxDepth = (depth - state.minDepth) / depthRange
            val y = minDepthY + percentOfMaxDepth * yRange
            val height = maxDepthY - y
            val color = state.colorForIndex(index)
            drawRect(color, Offset(x, y), Size(barWidth, height))
        }
    }
}
