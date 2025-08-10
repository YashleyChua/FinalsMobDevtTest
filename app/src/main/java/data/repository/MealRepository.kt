package com.example.kusinaphlite.data.repository

import com.example.kusinaphlite.data.model.Meal
import com.example.kusinaphlite.data.network.RetrofitClient

class MealRepository {
    private val api = RetrofitClient.apiService

    suspend fun getMeals(category: String): List<Meal> {
        return api.getMealsByCategory(category).meals ?: emptyList()
    }

    suspend fun getMealsByArea(area: String): List<Meal> {
        return api.getMealsByArea(area).meals ?: emptyList()
    }

    suspend fun getMealDetails(id: String): Meal? {
        return api.getMealDetails(id).meals?.firstOrNull()
    }

    suspend fun searchMeals(query: String): List<Meal> {
        return api.searchMeals(query).meals ?: emptyList()
    }
}
