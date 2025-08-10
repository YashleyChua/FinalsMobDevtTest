package com.example.kusinaphlite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kusinaphlite.ui.viewmodel.MealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(mealId: String, viewModel: MealViewModel, navController: NavController) {
    val meal by viewModel.selectedMeal.collectAsState()

    LaunchedEffect(mealId) {
        viewModel.loadMealDetail(mealId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar with Back Button
        TopAppBar(
            title = { Text("Recipe Details") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        // Meal Content
        meal?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = it.strMeal ?: "Unknown Meal", 
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (!it.strCategory.isNullOrBlank() && it.strCategory != "N/A") {
                    Text(
                        text = "Category: ${it.strCategory}", 
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Text(
                    text = "Instructions:", 
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it.strInstructions ?: "No instructions found.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } ?: run {
            // Loading or error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
