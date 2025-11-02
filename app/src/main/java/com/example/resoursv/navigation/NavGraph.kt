package com.example.resoursv.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.resoursv.ui.auth.LoginScreen
import com.example.resoursv.ui.recursos.RecursosListScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(onLoginSuccess = { navController.navigate("recursos") }) }
        composable("recursos") { RecursosListScreen(onAdd = { /*navigate to form*/ }) }
    }
}