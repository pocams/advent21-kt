import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.streams.toList

data class GridState(val grid: List<List<Int>>, val flashes: List<Pair<Int, Int>>, val totalFlashes: Int) {
    fun increment(): GridState = GridState(
        grid.map { col ->
            col.map { it + 1 }
        }, flashes, totalFlashes)

    fun flashAt(row: Int, col: Int): GridState {
        val newGrid = grid.map { it.toMutableList() }
        // Increase this cell past 10, so we know it's flashed and doesn't need to flash again
        newGrid[row][col] += 1

        for (dr in listOf(-1, 0, 1)) {
            for (dc in listOf(-1, 0, 1)) {
                val newRow = row + dr
                val newCol = col + dc
                if (newRow >= 0 && newRow < newGrid.size && newCol >= 0 && newCol < newGrid[newRow].size && newGrid[newRow][newCol] < 10) {
                    newGrid[newRow][newCol] += 1
                }
            }
        }

        val newFlashes = flashes.toMutableList()
        newFlashes.add(Pair(row, col))
        return GridState(newGrid, newFlashes, totalFlashes + 1)
    }

    fun clearFlashes() = GridState(grid.map { row ->
        row.map { col ->
            if (col == 11) { 0 } else { col }
        }
    }, flashes, totalFlashes)

    fun step(): GridState {
        var g = increment()

        do {
            var flashed = false
            for (row in 0 until g.grid.size) {
                for (col in 0 until g.grid[row].size) {
                    if (g.grid[row][col] == 10) {
                        g = g.flashAt(row, col)
                        flashed = true
                    }
                }
            }
        } while (flashed)

        return g.clearFlashes()
    }
}

class Day11State(initialGrid: GridState) {
    var step by mutableStateOf(0)
    val gridAtStep = mutableListOf(initialGrid)

    val grid get() = gridAtStep[step]

    fun goToStep(newStep: Int) {
        while (gridAtStep.size <= newStep) {
            gridAtStep.add(gridAtStep.last().step())
        }
        step = newStep
    }

    companion object {
        fun fromFile(filename: String): Day11State {
            val grid = GridState(
                File(filename).readLines().map { line ->
                    line.chars().map {
                        it - '0'.code
                    }.toList()
                }.toList()
            , listOf(), 0)

            return Day11State(grid)
        }
    }
}

@Composable
fun Day11Screen(goBack: () -> Unit) {
    val (useRealData, setUseRealData) = remember { mutableStateOf(false) }
    val state by if (useRealData) {
        remember { mutableStateOf(Day11State.fromFile("data/day11.txt")) }
    } else {
        remember { mutableStateOf(Day11State.fromFile("data/day11-example.txt")) }
    }

    Column(Modifier.fillMaxSize().padding(5.dp)) {
        Box(Modifier.fillMaxHeight(0.15f)) {
            Day11Controls(state, goBack, useRealData, setUseRealData)
        }

        Box(Modifier.fillMaxSize().align(Alignment.CenterHorizontally)) {
            Day11Display(state)
        }
    }
}

@Composable
fun Day11Controls(state: Day11State, goBack: () -> Unit, useRealData: Boolean, setUseRealData: (Boolean) -> Unit) {
    val composableScope = rememberCoroutineScope()
    var runCoroutine by remember { mutableStateOf<Job?>(null) }

    Row {
        Box(Modifier.padding(5.dp)) {
            Button(onClick = goBack) {
                Text("Back")
            }
        }

        Spacer(Modifier.width(8.dp))

        Box(Modifier.padding(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Use real data?")
                Spacer(Modifier.width(8.dp))
                Switch(useRealData, onCheckedChange = setUseRealData)
            }
        }

        Box(Modifier.padding(5.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Steps", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(state.step.toString())
            }
        }

        Box(Modifier.padding(5.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Flashes", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(state.grid.totalFlashes.toString())
            }
        }

        Spacer(Modifier.width(8.dp))

        Column {
            Row {
                Button(onClick = {
                    runCoroutine?.cancel()
                    state.goToStep(state.step - 1)
                }, enabled = state.step > 0, modifier = Modifier.width(40.dp)) {
                    Text("-")
                }

                Button(onClick = {
                    runCoroutine?.cancel()
                    state.goToStep(state.step + 1)
                }, enabled = state.step < 500, modifier = Modifier.width(40.dp)) {
                    Text("+")
                }
            }

            Row {
                Button(onClick = {
                    if (runCoroutine?.isActive != true) {
                        runCoroutine = composableScope.launch {
                            while (state.step < 500) {
                                state.goToStep(state.step + 1)
                                delay(100)
                            }
                        }
                    } else {
                        runCoroutine?.cancel()
                    }
                }, enabled = state.step < 500, modifier = Modifier.width(80.dp)) {
                    Text("Run")
                }
            }
        }

        Box(Modifier.padding(5.dp)) {
            Slider(value = state.step.toFloat(), valueRange = 0f..500f, onValueChange = { state.goToStep(it.toInt()) })
        }
    }
}

@Composable
fun Day11Display(state: Day11State) {
    Column(Modifier.border(2.dp, Colors.DarkGray).aspectRatio(1f).fillMaxSize()) {
        for (row in state.grid.grid) {
            Row(Modifier.weight(1f).fillMaxSize()) {
                for (col in row) {
                    Box(Modifier.border(2.dp, Colors.DarkGray).background(Colors.Yellows[col]).weight(1f).fillMaxSize()) {
//                        Text(col.toString())
                    }
                }
            }
        }
    }
}