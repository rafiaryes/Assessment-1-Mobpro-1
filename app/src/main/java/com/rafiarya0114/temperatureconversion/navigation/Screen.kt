package com.rafiarya0114.temperatureconversion.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("mainscreen")
    data object About: Screen("aboutscreen")
}