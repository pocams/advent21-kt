import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

data class Card(val rows: List<List<Int>>) {
    fun isWon(): Boolean = rows.any { row -> row.all { it.and(0x80) == 0x80 } }
            || (0 until 5).any { col -> rows.all { it[col].and(0x80) == 0x80 } }

    fun markNumber(number: Int): Card {
        return if (isWon()) {
            this
        } else {
            Card(
                rows.map { row ->
                    row.map { col ->
                        if (col == number) {
                            col.or(0x80)
                        } else {
                            col
                        }
                    }
                }
            )
        }
    }

    fun sumUnmarkedNumbers() = this.rows.sumOf { row ->
        row.sumOf {
            if (it.and(0x80) == 0x80) {
                0
            } else {
                it
            }
        }
    }
}

class Day4State {
    var callerNumbers: List<Int>
    lateinit var cards: SnapshotStateList<Card>
    var useRealData by mutableStateOf(false)
    var calledNumberIndex by mutableStateOf(0)
    var part1Answer by mutableStateOf<Int?>(null)
    var part2Answer by mutableStateOf<Int?>(null)
    var lastUnwonCardIndex by mutableStateOf<Int?>(null)

    val dataFile get() = if (useRealData) { "data/day4.txt" } else { "data/day4-example.txt" }

    fun loadData() {
        val lines = File(dataFile).readLines()
        callerNumbers = lines[0].split(",").map { it.toInt() }

        cards = lines.drop(2).chunked(6).map { it ->
            Card(
                it.take(5).map { it.trim().split(Regex("""\s+""")).map { it.toInt() }.toMutableList() }
            )
        }.toMutableStateList()

        part1Answer = null
        part2Answer = null
        lastUnwonCardIndex = null
        calledNumberIndex = 0
    }

    fun step() {
        cards.replaceAll { c -> c.markNumber(callerNumbers[calledNumberIndex]) }
        if (part1Answer == null) {
            val wonCard = cards.find { it.isWon() }
            if (wonCard != null) {
                part1Answer = wonCard.sumUnmarkedNumbers() * callerNumbers[calledNumberIndex]
            }
        }

        if (part2Answer == null) {
            if (lastUnwonCardIndex == null) {
                if (cards.count { !it.isWon() } == 1) {
                    lastUnwonCardIndex = cards.indexOfFirst { !it.isWon() }
                }
            } else {
                if (cards[lastUnwonCardIndex!!].isWon()) {
                    part2Answer = cards[lastUnwonCardIndex!!].sumUnmarkedNumbers() * callerNumbers[calledNumberIndex]
                }
            }
        }

        calledNumberIndex += 1
    }

    init {
        callerNumbers = listOf()
        loadData()
    }
}


@Composable
fun Day4Screen(goBack: () -> Unit) {
    val state = remember { Day4State() }

    Column(Modifier.fillMaxSize().padding(5.dp)) {
        Box {
            Day4Controls(state, goBack)
        }

        Box(Modifier.padding(5.dp)) {
            Day4Display(state)
        }
    }

}

@Composable
fun Day4Controls(state: Day4State, goBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {

        Button(modifier = Modifier.height(60.dp), onClick = {
            goBack()
        }) {
            Text("Back")
        }

        Button(modifier = Modifier.height(60.dp), onClick = {
            state.step()
        }) {
            Text("Step")
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

        }

        Spacer(Modifier.width(40.dp))

        for (grp in state.callerNumbers.withIndex().chunked(5)) {
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                for ((index, n) in grp) {
                    val color = when (index) {
                       in 0 until state.calledNumberIndex -> Colors.DarkGray
                        state.calledNumberIndex -> Colors.Yellow
                        else -> Colors.None
                    }
                    Box(Modifier.background(color)) {
                        Text(n.toString(), fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
        }

        Column {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Part 1:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(4.dp))
                Text(state.part1Answer?.toString() ?: "")
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Part 2:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(4.dp))
                Text(state.part2Answer?.toString() ?: "")
            }
        }
    }
}

@Composable
fun Day4Display(state: Day4State) {
    Column {
        for (cardgroup in state.cards.chunked(12)) {
            Row {
                for (card in cardgroup) {
                    Box(Modifier.padding(2.dp)) {
                        Day4Card(card)
                    }
                }
            }
        }
    }
}

@Composable
fun Day4Card(card: Card) {
    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        for (row in card.rows) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                for (number in row) {
                    val marked = number.and(0x80) == 0x80
                    val color = if (card.isWon()) {
                        Colors.Yellow
                    } else if (marked) {
                        Colors.Green
                    } else {
                        Colors.None
                    }
                    Box(Modifier.width(12.dp).height(12.dp).border(1.dp, Colors.DarkGray).background(color),
                        Alignment.Center) {
                        Text(number.and(0x7f).toString(), textAlign = TextAlign.Center, fontSize = 6.sp)
                    }
                }
            }
        }
    }
}
