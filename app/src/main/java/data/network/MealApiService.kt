package com.example.kusinaphlite.data.network

import com.example.kusinaphlite.data.model.MealResponse
import com.example.kusinaphlite.data.model.CategoriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealResponse

    @GET("filter.php")
    suspend fun getMealsByArea(@Query("a") area: String): MealResponse

    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") id: String): MealResponse

    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    // New endpoints for getting lists
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse

    // Debug endpoint to test if API is working
    @GET("search.php")
    suspend fun testApi(@Query("s") query: String = "chicken"): MealResponse
}
