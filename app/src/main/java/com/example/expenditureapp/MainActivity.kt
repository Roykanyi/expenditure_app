package com.example.expenditureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingApp() {
    var currentScreen by remember { mutableStateOf("home") }
    var taxiList by remember { mutableStateOf(listOf<Int>()) }
    var foodList by remember { mutableStateOf(listOf<Int>()) }
    var schoolFeesList by remember { mutableStateOf(listOf<Pair<String, Int>>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            "home" -> "Home"
                            "menu" -> "Spending Menu"
                            "taxi" -> "Taxi"
                            "food" -> "Food"
                            "schoolFees" -> "School Fees"
                            "summary" -> "Summary"
                            else -> ""
                        }
                    )
                },
                navigationIcon = {
                    if (currentScreen != "home") {
                        IconButton(onClick = {
                            currentScreen = if (currentScreen == "menu") "home" else "menu"
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            when (currentScreen) {
                "home" -> HomeScreen { currentScreen = "menu" }
                "menu" -> MenuScreen(
                    onTaxi = { currentScreen = "taxi" },
                    onFood = { currentScreen = "food" },
                    onSchoolFees = { currentScreen = "schoolFees" },
                    onSummary = { currentScreen = "summary" }
                )
                "taxi" -> InputScreen(
                    title = "Taxi Spending",
                    list = taxiList,
                    onAdd = { taxiList = taxiList + it }
                )
                "food" -> InputScreen(
                    title = "Food Spending",
                    list = foodList,
                    onAdd = { foodList = foodList + it }
                )
                "schoolFees" -> SchoolFeesScreen(
                    list = schoolFeesList,
                    onAdd = { desc, amount -> schoolFeesList = schoolFeesList + (desc to amount) }
                )
                "summary" -> SummaryScreen(
                    taxiTotal = taxiList.sum(),
                    foodTotal = foodList.sum(),
                    schoolTotal = schoolFeesList.sumOf { it.second }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Expenditure App", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text("Start Tracking")
        }
    }
}

@Composable
fun MenuScreen(
    onTaxi: () -> Unit,
    onFood: () -> Unit,
    onSchoolFees: () -> Unit,
    onSummary: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Spending Category", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onTaxi, modifier = Modifier.fillMaxWidth()) { Text("Taxi") }
        Button(onClick = onFood, modifier = Modifier.fillMaxWidth()) { Text("Food") }
        Button(onClick = onSchoolFees, modifier = Modifier.fillMaxWidth()) { Text("School Fees") }
        Button(onClick = onSummary, modifier = Modifier.fillMaxWidth()) { Text("View Summary") }
    }
}

@Composable
fun InputScreen(title: String, list: List<Int>, onAdd: (Int) -> Unit) {
    var input by remember { mutableStateOf("") }
    val total = list.sum()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(title, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Enter amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                input.toIntOrNull()?.let {
                    onAdd(it)
                    input = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: Ksh $total", fontSize = 16.sp)
    }
}

@Composable
fun SchoolFeesScreen(
    list: List<Pair<String, Int>>,
    onAdd: (String, Int) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val total = list.sumOf { it.second }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add School Fee Entry", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                amount.toIntOrNull()?.let {
                    if (desc.isNotBlank()) {
                        onAdd(desc, it)
                        desc = ""
                        amount = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add")
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(list) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("${it.first}: Ksh ${it.second}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Total School Fees: Ksh $total", fontSize = 16.sp)
    }
}

@Composable
fun SummaryScreen(taxiTotal: Int, foodTotal: Int, schoolTotal: Int) {
    val total = taxiTotal + foodTotal + schoolTotal

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Summary", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Taxi: Ksh $taxiTotal")
        Text("Food: Ksh $foodTotal")
        Text("School Fees: Ksh $schoolTotal")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total Spent: Ksh $total", fontSize = 18.sp)
    }
}
