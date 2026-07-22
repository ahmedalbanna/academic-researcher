package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.AcademicViewModel
import com.example.ui.screens.*
import com.example.ui.theme.AcademicResearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcademicResearchTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AcademicAppNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun AcademicAppNavigation() {
    val navController = rememberNavController()
    val academicViewModel: AcademicViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = academicViewModel,
                onNavigateToTool = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable("proposal") {
            ResearchProposalScreen(
                viewModel = academicViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("summary") {
            LiteratureSummaryScreen(
                viewModel = academicViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("writing") {
            AcademicWritingScreen(
                viewModel = academicViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("data") {
            DataAnalysisScreen(
                viewModel = academicViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("citation") {
            CitationManagerScreen(
                viewModel = academicViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("integrity") {
            AcademicIntegrityScreen(
                viewModel = academicViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("learning") {
            LearningModuleScreen(
                viewModel = academicViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
