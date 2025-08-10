package com.example.kusinaphlite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kusinaphlite.ui.viewmodel.MealViewModel

@Composable
fun MealDetailScreen(mealId: String, viewModel: MealViewModel) {
    val meal by viewModel.selectedMeal.collectAsState()

    LaunchedEffect(mealId) {
        viewModel.loadMealDetail(mealId)
    }

    meal?.let {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(it.strMeal ?: "Unknown Meal", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Category: ${it.strCategory ?: "N/A"}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(it.strInstructions ?: "No instructions found.")
        }
    }
}
