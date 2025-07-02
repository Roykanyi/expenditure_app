package com.example.expenditureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expenditureapp.ui.theme.ExpenditureAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenditureAppTheme {
                SpendingApp()
            }
        }
    }
}

@Composable
fun SpendingApp() {
    var currentScreen by remember { mutableStateOf("home") }

    var taxiList by remember { mutableStateOf(listOf<Int>()) }
    var foodList by remember { mutableStateOf(listOf<Int>()) }
    var schoolFeesList by remember { mutableStateOf(listOf<Pair<String, Int>>()) }

    when (currentScreen) {
        "home" -> HomeScreen { currentScreen = "menu" }

        "menu" -> SpendingMenu(
            onTaxi = { currentScreen = "taxi" },
            onFood = { currentScreen = "food" },
            onSchoolFees = { currentScreen = "schoolFees" },
            onSummary = { currentScreen = "summary" },
            onBack = { currentScreen = "home" }
        )

        "taxi" -> InputScreen(
            title = "Taxi Spending",
            list = taxiList,
            onAdd = { taxiList = taxiList + it },
            onBack = { currentScreen = "menu" }
        )

        "food" -> InputScreen(
            title = "Food Spending",
            list = foodList,
            onAdd = { foodList = foodList + it },
            onBack = { currentScreen = "menu" }
        )

        "schoolFees" -> SchoolFeesScreen(
            list = schoolFeesList,
            onAdd = { label, amount -> schoolFeesList = schoolFeesList + (label to amount) },
            onBack = { currentScreen = "menu" }
        )

        "summary" -> SummaryScreen(
            taxiTotal = taxiList.sum(),
            foodTotal = foodList.sum(),
            schoolTotal = schoolFeesList.sumOf { it.second },
            onBack = { currentScreen = "menu" }
        )
    }
}

@Composable
fun HomeScreen(onNavigate: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.money_pic),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to Expenditure App", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onNavigate, modifier = Modifier.fillMaxWidth()) {
                Text("Go to Spending")
            }
        }
    }
}

@Composable
fun SpendingMenu(
    onTaxi: () -> Unit,
    onFood: () -> Unit,
    onSchoolFees: () -> Unit,
    onSummary: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.photo_of_money),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select Spending Category", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onTaxi, modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text("Taxi") }
            Button(onClick = onFood, modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text("Food") }
            Button(onClick = onSchoolFees, modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text("School Fees") }
            Button(onClick = onSummary, modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text("View Summary") }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text("Back to Home") }
        }
    }
}

@Composable
fun InputScreen(title: String, list: List<Int>, onAdd: (Int) -> Unit, onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    val total = list.sum()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Enter amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            input.toIntOrNull()?.let {
                onAdd(it)
                input = ""
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: Ksh $total", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun SchoolFeesScreen(
    list: List<Pair<String, Int>>,
    onAdd: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val total = list.sumOf { it.second }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("School Fees", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            amount.toIntOrNull()?.let {
                if (desc.isNotBlank()) {
                    onAdd(desc, it)
                    desc = ""
                    amount = ""
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Add")
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(list) {
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(it.first, fontWeight = FontWeight.Bold)
                        Text("Ksh ${it.second}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Total School Fees: Ksh $total", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
fun SummaryScreen(taxiTotal: Int, foodTotal: Int, schoolTotal: Int, onBack: () -> Unit) {
    val grandTotal = taxiTotal + foodTotal + schoolTotal

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Spending Summary", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Taxi: Ksh $taxiTotal")
        Text("Food: Ksh $foodTotal")
        Text("School Fees: Ksh $schoolTotal")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total Spent: Ksh $grandTotal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
