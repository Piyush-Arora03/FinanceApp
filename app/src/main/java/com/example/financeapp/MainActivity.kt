package com.example.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.features.home.HomeRoute
import com.example.financeapp.ui.features.transaction.TransactionRoute
import com.example.financeapp.ui.route.Goals
import com.example.financeapp.ui.route.Home
import com.example.financeapp.ui.route.Insight
import com.example.financeapp.ui.route.NavRoutes
import com.example.financeapp.ui.route.Transaction
import com.example.financeapp.ui.theme.FinanceAppTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class BottomBarNavigationDestination(
    val route: NavRoutes,
    val title: String,
    val icon: ImageVector
){
    object HomeDestination : BottomBarNavigationDestination(Home,"Home", Icons.Default.Home)
    object TransactionDestination : BottomBarNavigationDestination(Transaction,"Transaction",Icons.AutoMirrored.Filled.List)
    object GoalsDestination : BottomBarNavigationDestination(Goals,"Goals", Icons.Default.TrackChanges)
    object InsightDestination : BottomBarNavigationDestination(Insight,"Insights",Icons.Default.Analytics)
}

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceAppTheme {
                val bottomNavItems : List<BottomBarNavigationDestination> = listOf(
                    BottomBarNavigationDestination.HomeDestination,
                    BottomBarNavigationDestination.TransactionDestination,
                    BottomBarNavigationDestination.GoalsDestination,
                    BottomBarNavigationDestination.InsightDestination
                )
                val navController = rememberNavController()
                val currRoute = navController.currentBackStackEntryAsState().value?.destination
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Home") }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            bottomNavItems.forEach { item ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title
                                        )
                                    },
                                    label = { Text(text = item.title) },
                                    selected = currRoute?.hierarchy?.any {
                                        it.route == item.route::class.qualifiedName
                                    } == true,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = Home,
                        modifier = Modifier.padding(innerPadding)
                        ){
                        composable<Home> {
                            HomeRoute()
                        }
                        composable<Transaction> {
                            TransactionRoute()
                        }
                        composable<Goals> {
                            Greeting(
                                name = "Goals",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable<Insight> {
                            Greeting(
                                name = "Insights",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FinanceAppTheme {
        Greeting("Android")
    }
}