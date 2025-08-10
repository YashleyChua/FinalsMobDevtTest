package com.example.kusinaphlite.data.model

data class MealResponse(
    val meals: List<Meal>?
)

data class Meal(
    val idMeal: String?,
    val strMeal: String?,
    val strCategory: String?,
    val strInstructions: String?,
    val strMealThumb: String?
)
