package com.example.financeapp.ui.route

import kotlinx.serialization.Serializable

interface NavRoutes

@Serializable
object Home: NavRoutes

@Serializable
object Transaction: NavRoutes

@Serializable
object Goals: NavRoutes

@Serializable
object Insight: NavRoutes



