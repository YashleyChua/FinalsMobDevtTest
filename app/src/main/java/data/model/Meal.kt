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

// New response models for categories
data class CategoriesResponse(
    val categories: List<Category>?
)

data class Category(
    val idCategory: String?,
    val strCategory: String?,
    val strCategoryDescription: String?,
    val strCategoryThumb: String?
)
