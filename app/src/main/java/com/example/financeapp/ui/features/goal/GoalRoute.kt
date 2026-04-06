package com.example.financeapp.ui.features.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.data.local.entity.SavingsGoal
import com.example.financeapp.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalRoute(viewModel: GoalViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isCreateSheetVisible by remember { mutableStateOf(false) }
    var selectedGoalForFunds by remember { mutableStateOf<SavingsGoal?>(null) }

    when (val state = uiState) {
        is GoalViewModel.GoalScreenUIStates.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        }
        is GoalViewModel.GoalScreenUIStates.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.msg}", color = Color.Red)
            }
        }
        is GoalViewModel.GoalScreenUIStates.Success -> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { isCreateSheetVisible = true },
                        containerColor = Primary,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Goal")
                    }
                }
            ) { paddingValues ->
                GoalScreenContent(
                    modifier = Modifier.padding(paddingValues).background(Color(0xFFF3F4F6)),
                    successState = state.state,
                    onAddFundsClick = { targetGoal -> selectedGoalForFunds = targetGoal }
                )

                if (isCreateSheetVisible) {
                    CreateGoalBottomSheet(
                        onDismiss = { isCreateSheetVisible = false },
                        onSaveGoal = { title, targetAmt, deadline ->
                            viewModel.createGoal(title, targetAmt, deadline)
                            isCreateSheetVisible = false
                        }
                    )
                }

                // Inside your GoalRoute Scaffolds content...
                selectedGoalForFunds?.let { targetGoal ->
                    AddFundsDialog(
                        targetGoal = targetGoal,
                        onDismiss = { selectedGoalForFunds = null },
                        onConfirm = { addedAmt ->
                            viewModel.addFunds(targetGoal, addedAmt)
                            selectedGoalForFunds = null
                        }
                    )
                }
            }
        }
    }
}