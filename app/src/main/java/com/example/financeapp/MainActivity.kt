package com.example.financeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.features.goal.GoalRoute
import com.example.financeapp.ui.features.home.HomeRoute
import com.example.financeapp.ui.features.insight.InsightRoute
import com.example.financeapp.ui.features.transaction.TransactionRoute
import com.example.financeapp.ui.route.Goals
import com.example.financeapp.ui.route.Home
import com.example.financeapp.ui.route.Insight
import com.example.financeapp.ui.route.NavRoutes
import com.example.financeapp.ui.route.Transaction
import com.example.financeapp.ui.theme.FinanceAppTheme
import dagger.hilt.android.AndroidEntryPoint

private val priBlu = Color(0xFF1976D2)
private val surBlu = Color(0xFFE3F2FD)

sealed class BottomBarNavigationDestination(val route: NavRoutes, val title: String, val icon: ImageVector) {
    object HomeDestination : BottomBarNavigationDestination(Home, "Home", Icons.Default.Home)
    object TransactionDestination : BottomBarNavigationDestination(Transaction, "Transaction", Icons.AutoMirrored.Filled.List)
    object GoalsDestination : BottomBarNavigationDestination(Goals, "Goals", Icons.Default.TrackChanges)
    object InsightDestination : BottomBarNavigationDestination(Insight, "Insights", Icons.Default.Analytics)
}
fun triggerBiometricAuth(activity: FragmentActivity, onSuccess: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Finance App")
        .setSubtitle("Confirm your identity to access your data")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        .build()

    val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess()
        }
    })

    biometricPrompt.authenticate(promptInfo)
}

@Composable
fun LockScreen(onUnlock: () -> Unit) {
    val context = LocalContext.current as FragmentActivity

    LaunchedEffect(Unit) {
        triggerBiometricAuth(context, onUnlock)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(priBlu),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("App Locked", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { triggerBiometricAuth(context, onUnlock) },
            colors = ButtonDefaults.buttonColors(containerColor = surBlu),
            modifier = Modifier.height(50.dp).padding(horizontal = 32.dp)
        ) {
            Text("Unlock with Fingerprint / PIN", color = priBlu, fontWeight = FontWeight.Bold)
        }
    }
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() { // <-- Note: Changed to FragmentActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceAppTheme {
                var isAuthenticated by remember { mutableStateOf(false) }

                if (!isAuthenticated) {
                    LockScreen(onUnlock = { isAuthenticated = true })
                } else {

                    val navItems = listOf(
                        BottomBarNavigationDestination.HomeDestination,
                        BottomBarNavigationDestination.TransactionDestination,
                        BottomBarNavigationDestination.GoalsDestination,
                        BottomBarNavigationDestination.InsightDestination
                    )
                    val navCtrl = rememberNavController()
                    val currRoute = navCtrl.currentBackStackEntryAsState().value?.destination

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(containerColor = Color.White) {
                                navItems.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                                        label = { Text(text = item.title) },
                                        selected = currRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true,
                                        onClick = {
                                            navCtrl.navigate(item.route) {
                                                popUpTo(navCtrl.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = priBlu,
                                            selectedTextColor = priBlu,
                                            indicatorColor = surBlu,
                                            unselectedIconColor = Color.Gray,
                                            unselectedTextColor = Color.Gray
                                        )
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        NavHost(
                            navController = navCtrl,
                            startDestination = Home,
                            modifier = Modifier.padding(padding),
                            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeIn(tween(300)) },
                            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeOut(tween(300)) },
                            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeIn(tween(300)) },
                            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeOut(tween(300)) }
                        ) {
                            composable<Home> { HomeRoute() }
                            composable<Transaction> { TransactionRoute() }
                            composable<Goals> { GoalRoute() }
                            composable<Insight> { InsightRoute() }
                        }
                    }
                }
            }
        }
    }
}