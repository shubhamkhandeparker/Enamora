package com.shubham.enamora.ui.model

import androidx.annotation.DrawableRes

data class CharacterUiModel(
    val id: String,
    val name: String,
    val age: Int,
    @DrawableRes val portraitResId: Int,
    val isAvailable: Boolean,
    val availabilityText: String,
    val recentMessagePreview: String,
    val tagline: String,
    val interests: List<String>
) {
    init {
        require(age >= 18) {
            "Every Enamora character must be an adult."
        }
    }
}