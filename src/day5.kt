import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.lang.Integer.max
import kotlin.math.sign


data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
    companion object {
        val lineRegex = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")

        fun fromString(line: String): Line {
            val match = lineRegex.matchEntire(line) ?: throw IllegalArgumentException("Unparseable line: $line")
            val x1 = match.groupValues[1].toInt()
            val y1 = match.groupValues[2].toInt()
            val x2 = match.groupValues[3].toInt()
            val y2 = match.groupValues[4].toInt()
            return Line(x1, y1, x2, y2)
        }
    }

    val largestCoordinate get() = sequenceOf(x1, y1, x2, y2).reduce { a, b -> max(a, b) }

    fun isStraight() = x1 == x2 || y1 == y2

    fun coveredCoordinates() = sequence {
        var cursor = Pair(x1, y1)
        while (true) {
            yield(cursor)
            val xstep = (x2 - cursor.first).sign
            val ystep = (y2 - cursor.second).sign
            if (xstep == 0 && ystep == 0) break
            cursor = Pair(cursor.first + xstep, cursor.second + ystep)
        }
    }
}

class Day5State {
    var lines: List<Line>
    val dataFile get() = if (useRealData) { "data/day5.txt" } else { "data/day5-example.txt" }
    var useRealData by mutableStateOf(false)
    var part2 by mutableStateOf(false)

    val canvasSize get() = lines.map { it.largestCoordinate }.reduce { a, b -> max(a, b) } + 1

    val part1Counts get() = lines.filter { it.isStraight() }.flatMap { it.coveredCoordinates() }.groupingBy { it }.eachCount()
    val part2Counts get() = lines.flatMap { it.coveredCoordinates() }.groupingBy { it }.eachCount()
    val counts get() = if (part2) { part2Counts } else { part1Counts }

    val overlaps get() = counts.values.filter { it > 1 }.count()

    fun loadData() {
        lines = File(dataFile).readLines().map { Line.fromString(it) }
    }

    init {
        lines = listOf()
        loadData()
    }
}

@Composable
fun Day5Screen() {
    val state = remember { Day5State() }

    Column(Modifier.fillMaxSize().padding(5.dp)) {
        Box {
            Day5Controls(state)
        }

        Box(Modifier.padding(5.dp)) {
            Day5Display(state)
        }
    }

}

@Composable
fun Day5Controls(state: Day5State) {
    Row(verticalAlignment = Alignment.CenterVertically) {

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
                    }
                )
            }
        }

        Spacer(Modifier.width(40.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Overlap count", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(Modifier.height(12.dp))
            Text(state.overlaps.toString(), fontSize = 20.sp)
        }

    }
}

@Composable
fun Day5Display(state: Day5State) {
    Row {
        Box(Modifier.padding(PaddingValues(end = 5.dp))) {
            Day5Canvas(state)
        }
        Day5List(state)
    }
}

@Composable
fun Day5List(state: Day5State) {
    val verticalScroll = rememberScrollState(0)
    Box(Modifier.verticalScroll(verticalScroll)) {
        Column {
            for (line in state.lines) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Text("${line.x1}, ${line.y1} -> ${line.x2}, ${line.y2}")
                }
            }
        }

//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd)
//                .fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(verticalScroll)
//        )
    }
}

@Composable
fun Day5Canvas(state: Day5State) {
    Canvas(modifier = Modifier.aspectRatio(1.0f)) {
        drawRect(Colors.DarkGray, size=this.size)
        val totalTileSize = this.size.width / state.canvasSize.toFloat()
        val tileSize = totalTileSize * 0.75f
        val tileSpacing = totalTileSize - tileSize
        val counts = state.counts

        for ((point, count) in counts) {
            val x = point.first * totalTileSize + tileSpacing / 2
            val y = point.second * totalTileSize + tileSpacing / 2
//            println("at $point: $count @ $x, $y [$tileSize]")
            drawRect(
                when (count) {
                    1 -> Colors.Blue
                    2 -> Colors.Green
                    else -> Colors.Red
                },
                Offset(x, y),
                Size(tileSize, tileSize))
        }
    }
}
