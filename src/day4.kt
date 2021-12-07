import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

data class Card(val rows: List<List<Int>>) {
}

class Day4State {
    var callerNumbers: List<Int>
    var cards: List<Card>
    val dataFile get() = if (useRealData) { "data/day4.txt" } else { "data/day4-example.txt" }
    var useRealData by mutableStateOf(false)
    var part2 by mutableStateOf(false)

    fun loadData() {
        val lines = File(dataFile).readLines()
        callerNumbers = lines[0].split(",").map { it.toInt() }

        cards = lines.drop(2).chunked(6).map { it ->
            Card(
                it.take(5).map { it.trim().split(Regex("""\s+""")).map { it.toInt() } }
            )
        }
    }

    init {
        callerNumbers = listOf()
        cards = listOf()
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
                    Box(Modifier.width(12.dp).height(12.dp).border(1.dp, Colors.DarkGray),
                        Alignment.Center) {
                        Text(number.toString(), textAlign = TextAlign.Center, fontSize = 6.sp)
                    }
                }
            }
        }
    }
}
