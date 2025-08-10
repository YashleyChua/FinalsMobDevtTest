package com.example.kusinaphlite.ui.components

object CategoryGuesser {
    private val map = mapOf(
        "Dessert" to listOf("cake","cookie","brownie","pudding","dessert","ice cream","gelato","pastry","chocolate"),
        "Pasta" to listOf("pasta","spaghetti","lasagna","penne","macaroni","fettuccine"),
        "Chicken" to listOf("chicken","fried chicken","roast chicken"),
        "Seafood" to listOf("fish","salmon","tuna","shrimp","prawn","crab","seafood"),
        "Soup" to listOf("soup","broth","stew","chowder"),
        "Salad" to listOf("salad","lettuce","greens","vinaigrette")
    )

    fun guess(title: String?, ingredients: String?): String {
        val text = ((title ?: "") + " " + (ingredients ?: "")).lowercase()
        map.forEach { (cat, keys) ->
            if (keys.any { key -> text.contains(key) }) return cat
        }
        return "Other"
    }
}
