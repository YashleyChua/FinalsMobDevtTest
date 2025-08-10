package com.example.kusinaphlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ui.screens.HomeScreen
import com.example.kusinaphlite.ui.screens.MealDetailScreen
import com.example.kusinaphlite.ui.viewmodel.MealViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MealViewModel = viewModel()
            val navController = rememberNavController()

            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController, viewModel)
                    }
                    composable(
                        "detail/{mealId}",
                        arguments = listOf(navArgument("mealId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("mealId").orEmpty()
                        MealDetailScreen(id, viewModel, navController)
                    }
                }
            }
        }
    }
}
