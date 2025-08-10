package ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kusinaphlite.data.model.Meal
import com.example.kusinaphlite.ui.viewmodel.MealViewModel
import androidx.compose.material3.OutlinedTextFieldDefaults


@Composable
fun HomeScreen(navController: NavController, viewModel: MealViewModel) {
    val meals by viewModel.meals.collectAsState()
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAllMeals()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("KusinaPH", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Search bar: outline removed by using Transparent border colors
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                viewModel.updateSearchQuery(it)
            },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(meals) { meal ->
                MealListItem(meal = meal) {
                    meal.idMeal?.let { id -> navController.navigate("detail/$id") }
                }
            }
        }
    }
}

@Composable
fun MealListItem(meal: Meal, onClick: () -> Unit) {
    val cardShape = RoundedCornerShape(8.dp)
    val blueBorder = Color(0xFF1976D2)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // apply explicit blue border so it remains visible
            .border(width = 2.dp, color = blueBorder, shape = cardShape)
            // make whole card clickable
            .clickable { onClick() },
        shape = cardShape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(text = meal.strMeal ?: "Unknown Meal", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))

            // Hide category if it's blank or exactly "N/A"
            meal.strCategory
                ?.takeIf { it.isNotBlank() && it != "N/A" }
                ?.let { category ->
                    Text(text = "Category: $category", style = MaterialTheme.typography.bodyMedium)
                }
        }
    }
}
