package com.example.kusinaphlite.data.repository

import com.example.kusinaphlite.data.model.Meal
import com.example.kusinaphlite.data.model.Category
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

    // New methods for getting categories
    suspend fun getCategories(): List<Category> {
        return api.getCategories().categories ?: emptyList()
    }

    // Debug method to test if API is working
    suspend fun testApi(): List<Meal> {
        return try {
            api.testApi().meals ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
