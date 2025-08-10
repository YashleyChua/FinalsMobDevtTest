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
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadAllMeals()
    }

    fun loadAllMeals() {
        viewModelScope.launch {
            val filipino = try {
                repository.getMealsByArea("Filipino")
            } catch (t: Throwable) {
                emptyList()
            }

            val defaults = try {
                repository.getMeals("Seafood")
            } catch (t: Throwable) {
                emptyList()
            }

            // Combine and deduplicate by idMeal
            val combined = (defaults + filipino).distinctBy { it.idMeal }
            _allMeals.value = combined
            applyFilters()
        }
    }

    fun loadMeals(category: String) {
        viewModelScope.launch {
            val list = try {
                repository.getMeals(category)
            } catch (t: Throwable) {
                emptyList()
            }
            _allMeals.value = list
            applyFilters()
        }
    }

    fun loadMealsByArea(area: String?) {
        if (area.isNullOrBlank()) return
        viewModelScope.launch {
            val list = try {
                repository.getMealsByArea(area)
            } catch (t: Throwable) {
                emptyList()
            }
            _allMeals.value = list
            applyFilters()
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

    private fun applyFilters() {
        val q = _searchQuery.value.lowercase().trim()
        if (q.isEmpty()) {
            _meals.value = _allMeals.value
            return
        }
        _meals.value = _allMeals.value.filter { meal ->
            (meal.strMeal ?: "").lowercase().contains(q)
        }
    }
}
