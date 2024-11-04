package com.amshinski.miawiki.data

data class ItemData(
    val name: String,
    val recipes: List<RecipeData>,
    val obtainMethods: List<String>,
    val image: String
)

data class RecipeData(
    val ingredients: List<String>,
    val result: String
)