package ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kusinaphlite.data.model.Meal
import com.example.kusinaphlite.ui.viewmodel.MealViewModel
import androidx.compose.material3.OutlinedTextFieldDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MealViewModel) {
    val meals by viewModel.meals.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAllMeals()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("KusinaPH", style = MaterialTheme.typography.headlineSmall) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.updateSearchQuery(it)
                },
                label = { Text("Search Filipino Recipes") },
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

            // Category Filter Dropdown
            CategoryDropdown(
                categories = availableCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    viewModel.updateSelectedCategory(category)
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Clear Filters Button (only show if filters are active)
            if (selectedCategory != null || searchText.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        viewModel.clearFilters()
                        searchText = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear filters"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All Filters")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Content area
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading recipes...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else if (meals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recipes found",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedCategory != null) 
                                "Try selecting a different category or clearing filters"
                            else 
                                "Try searching for a different recipe",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.clearFilters()
                                searchText = ""
                            }
                        ) {
                            Text("Clear Filters")
                        }
                    }
                }
            } else {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isLoading: Boolean = false
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (isLoading && categories.isEmpty()) "Loading categories..." 
                   else selectedCategory ?: "All Categories",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Category") },
            trailingIcon = {
                if (isLoading && categories.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading || categories.isNotEmpty()
        )

        if (categories.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                // "All Categories" option
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = "All Categories",
                            color = if (selectedCategory == null) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurface
                        ) 
                    },
                    onClick = {
                        onCategorySelected(null)
                        onExpandedChange(false)
                    }
                )
                
                // Category options
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = category,
                                color = if (selectedCategory == category) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurface
                            ) 
                        },
                        onClick = {
                            onCategorySelected(category)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MealListItem(meal: Meal, onClick: () -> Unit) {
    val cardShape = RoundedCornerShape(8.dp)
    val blueBorder = Color(0xFF1976D2)

    // Determine the category to display
    val displayCategory = meal.strCategory?.takeIf { it.isNotBlank() && it != "N/A" }
        ?: inferCategoryFromMealName(meal.strMeal)

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

            // Always show category if we can determine one
            if (displayCategory.isNotBlank()) {
                Text(text = "Category: $displayCategory", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// Helper function to infer category from meal name
private fun inferCategoryFromMealName(mealName: String?): String {
    if (mealName.isNullOrBlank()) return ""
    
    val name = mealName.lowercase()
    return when {
        name.contains("chicken") -> "Chicken"
        name.contains("beef") -> "Beef"
        name.contains("pork") -> "Pork"
        name.contains("fish") || name.contains("seafood") || name.contains("salmon") || name.contains("tuna") -> "Seafood"
        name.contains("cake") || name.contains("dessert") || name.contains("ice cream") || name.contains("halo") -> "Dessert"
        name.contains("soup") -> "Soup"
        name.contains("salad") -> "Salad"
        name.contains("pasta") || name.contains("noodle") -> "Pasta"
        name.contains("rice") -> "Rice"
        name.contains("egg") -> "Breakfast"
        else -> "Miscellaneous"
    }
}
