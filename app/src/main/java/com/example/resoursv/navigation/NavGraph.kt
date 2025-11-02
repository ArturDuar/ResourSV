package com.example.resoursv.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.resoursv.controller.AuthController
import com.example.resoursv.ui.auth.LoginScreen
import com.example.resoursv.ui.auth.RegisterScreen
import com.example.resoursv.ui.recursos.RecursoFormScreen
import com.example.resoursv.ui.recursos.RecursosListScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val authController = AuthController(auth)

    // Verificar si hay usuario autenticado
    val startDestination = if (auth.currentUser != null) "recursos" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("recursos") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegistered = {
                    navController.navigate("recursos") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("recursos") {
            RecursosListScreen(
                onAdd = {
                    navController.navigate("recurso_form")
                },
                onEdit = { recursoId ->
                    navController.navigate("recurso_form/$recursoId")
                },
                onLogout = {
                    authController.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("recurso_form") {
            RecursoFormScreen(
                onSaved = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "recurso_form/{recursoId}",
            arguments = listOf(navArgument("recursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId")
            RecursoFormScreen(
                recursoId = recursoId,
                onSaved = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}