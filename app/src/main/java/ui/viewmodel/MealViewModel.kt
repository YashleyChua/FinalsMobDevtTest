package com.example.kusinaphlite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kusinaphlite.data.model.Meal
import com.example.kusinaphlite.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {
    private val repository = MealRepository()

    private val _allMeals = MutableStateFlow<List<Meal>>(emptyList())

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    private val _selectedMeal = MutableStateFlow<Meal?>(null)
    val selectedMeal: StateFlow<Meal?> = _selectedMeal

    private val _searchQuery = MutableStateFlow("")
    @Suppress("unused")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _availableCategories = MutableStateFlow<List<String>>(emptyList())
    val availableCategories: StateFlow<List<String>> = _availableCategories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadAllMeals()
        loadCategories()
    }

    fun loadAllMeals() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First test if API is working
                val testMeals = try {
                    repository.testApi()
                } catch (t: Throwable) {
                    emptyList()
                }

                val filipino = try {
                    repository.getMealsByArea("Filipino")
                } catch (t: Throwable) {
                    emptyList()
                }

                val seafood = try {
                    repository.getMeals("Seafood")
                } catch (t: Throwable) {
                    emptyList()
                }

                val beef = try {
                    repository.getMeals("Beef")
                } catch (t: Throwable) {
                    emptyList()
                }

                val chicken = try {
                    repository.getMeals("Chicken")
                } catch (t: Throwable) {
                    emptyList()
                }

                // Combine and deduplicate by idMeal
                val combined = (testMeals + filipino + seafood + beef + chicken).distinctBy { it.idMeal }
                
                // If we still have no meals, try a broader search
                if (combined.isEmpty()) {
                    val broadSearch = try {
                        repository.searchMeals("chicken")
                    } catch (t: Throwable) {
                        emptyList()
                    }
                    _allMeals.value = broadSearch
                } else {
                    _allMeals.value = combined
                }
                
                // If still no meals, create some sample meals
                if (_allMeals.value.isEmpty()) {
                    _allMeals.value = createSampleMeals()
                }
                
                applyFilters()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createSampleMeals(): List<Meal> {
        return listOf(
            Meal(
                idMeal = "1",
                strMeal = "Chicken Adobo",
                strCategory = "Chicken",
                strInstructions = "A classic Filipino dish made with chicken, soy sauce, vinegar, and garlic.",
                strMealThumb = null
            ),
            Meal(
                idMeal = "2",
                strMeal = "Sinigang na Isda",
                strCategory = "Seafood",
                strInstructions = "A sour tamarind soup with fish and vegetables.",
                strMealThumb = null
            ),
            Meal(
                idMeal = "3",
                strMeal = "Beef Caldereta",
                strCategory = "Beef",
                strInstructions = "A rich beef stew with tomato sauce and vegetables.",
                strMealThumb = null
            ),
            Meal(
                idMeal = "4",
                strMeal = "Lechon Kawali",
                strCategory = "Pork",
                strInstructions = "Crispy fried pork belly, a Filipino favorite.",
                strMealThumb = null
            ),
            Meal(
                idMeal = "5",
                strMeal = "Halo-Halo",
                strCategory = "Dessert",
                strInstructions = "A colorful Filipino dessert with shaved ice, sweet beans, and ice cream.",
                strMealThumb = null
            )
        )
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = repository.getCategories()
                val categoryNames = categories.mapNotNull { it.strCategory }.sorted()
                _availableCategories.value = categoryNames
            } catch (t: Throwable) {
                // If API fails, fall back to extracting from meals
                val categories = _allMeals.value
                    .mapNotNull { it.strCategory }
                    .filter { it.isNotBlank() && it != "N/A" }
                    .distinct()
                    .sorted()
                _availableCategories.value = categories
            }
        }
    }

    fun loadMealDetail(id: String?) {
        if (id.isNullOrBlank()) return
        viewModelScope.launch {
            _selectedMeal.value = try {
                repository.getMealDetails(id)
            } catch (t: Throwable) {
                null
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateSelectedCategory(category: String?) {
        _selectedCategory.value = category
        
        if (category != null) {
            // For Filipino category, load by area instead of category
            if (category.equals("Filipino", ignoreCase = true)) {
                loadFilipinoMeals()
            } else {
                // Fetch meals for the selected category from API
                loadMealsForCategory(category)
            }
        } else {
            // If "All Categories" is selected, load all meals
            loadAllMeals()
        }
    }

    private fun loadFilipinoMeals() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val filipinoMeals = try {
                    repository.getMealsByArea("Filipino")
                } catch (t: Throwable) {
                    emptyList()
                }
                _allMeals.value = filipinoMeals
                applyFilters()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadMealsForCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First, try to get meals by category
                val meals = try {
                    repository.getMeals(category)
                } catch (t: Throwable) {
                    emptyList()
                }
                
                // If no meals from category endpoint, try search
                if (meals.isEmpty()) {
                    val searchResults = try {
                        repository.searchMeals(category)
                    } catch (t: Throwable) {
                        emptyList()
                    }
                    
                    // Filter search results by category
                    val filteredResults = searchResults.filter { meal ->
                        meal.strCategory?.trim()?.equals(category.trim(), ignoreCase = true) == true
                    }
                    
                    _allMeals.value = filteredResults
                } else {
                    _allMeals.value = meals
                }
                
                // If still no meals, try some common meal searches
                if (_allMeals.value.isEmpty()) {
                    val fallbackMeals = try {
                        when (category.lowercase()) {
                            "seafood" -> repository.searchMeals("fish")
                            "beef" -> repository.searchMeals("beef")
                            "chicken" -> repository.searchMeals("chicken")
                            "pork" -> repository.searchMeals("pork")
                            "dessert" -> repository.searchMeals("cake")
                            else -> repository.searchMeals("chicken") // default fallback
                        }
                    } catch (t: Throwable) {
                        emptyList()
                    }
                    _allMeals.value = fallbackMeals
                }
                
                applyFilters()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        loadAllMeals() // Reload all meals when clearing filters
    }

    private fun applyFilters() {
        val searchQuery = _searchQuery.value.lowercase().trim()
        val selectedCategory = _selectedCategory.value
        
        var filteredMeals = _allMeals.value
        
        // Apply category filter (if we have meals with category info)
        if (!selectedCategory.isNullOrBlank()) {
            filteredMeals = filteredMeals.filter { meal ->
                val mealCategory = meal.strCategory?.trim()
                val selectedCat = selectedCategory.trim()
                
                // Check explicit category first
                if (mealCategory == selectedCat) {
                    true
                } else {
                    // If no explicit category, infer from meal name
                    val inferredCategory = inferCategoryFromMealName(meal.strMeal)
                    inferredCategory == selectedCat
                }
            }
        }
        
        // Apply search filter
        if (searchQuery.isNotEmpty()) {
            filteredMeals = filteredMeals.filter { meal ->
                (meal.strMeal ?: "").lowercase().contains(searchQuery)
            }
        }
        
        _meals.value = filteredMeals
    }

    // Helper function to infer category from meal name (same as in HomeScreen)
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
}
