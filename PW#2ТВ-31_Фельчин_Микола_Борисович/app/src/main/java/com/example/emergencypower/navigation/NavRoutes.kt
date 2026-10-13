package com.example.emergencypower.navigation

sealed class NavRoutes(val route: String) {
    object MainMenu : NavRoutes("main_menu")
    object Dashboard : NavRoutes("dashboard")
    object BatteryDetail : NavRoutes("battery_detail/{stationId}?mode={mode}") {
        fun createRoute(stationId: String, mode: String = "auto"): String {
            return "battery_detail/$stationId?mode=$mode"
        }
    }
    object GeneratorAts : NavRoutes("generator_ats")
    object Auth : NavRoutes("auth")
}
