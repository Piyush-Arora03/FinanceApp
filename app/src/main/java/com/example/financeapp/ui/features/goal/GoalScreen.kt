package com.example.financeapp.ui.features.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.data.local.entity.SavingsGoal
import com.example.financeapp.data.models.GoalScreenSuccessState
import com.example.financeapp.ui.theme.Light
import com.example.financeapp.ui.theme.Primary
import com.example.financeapp.ui.theme.Secondary
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState

@Composable
fun GoalScreenContent(
    modifier: Modifier = Modifier,
    successState: GoalScreenSuccessState,
    onAddFundsClick: (SavingsGoal) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GoalSummaryCard(
                totalSaved = successState.totalSavedAcrossAllGoals,
                totalTarget = successState.totalTargetAcrossAllGoals
            )
        }

        // 2. GitHub-style Savings Heatmap
        item {
            HeatmapSection(heatmapData = successState.contributionHeatmapData)
        }

        // 3. Active Goals List
        item {
            Text(
                text = "Active Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(successState.allActiveGoalsList) { goal ->
            GoalListItemCard(goalItem = goal, onQuickAddClick = { onAddFundsClick(goal) })
        }
    }
}

@Composable
fun GoalSummaryCard(totalSaved: Double, totalTarget: Double) {
    val progressPercentage = if (totalTarget > 0) (totalSaved / totalTarget).toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Primary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Total Savings Progress", color = Color.White.copy(alpha = 0.8f))
            Text(
                text = "\$${totalSaved.toInt()} / \$${totalTarget.toInt()}",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progressPercentage },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
fun HeatmapSection(heatmapData: List<com.example.financeapp.data.models.HeatmapDaySummary>) {
    val gridState = rememberLazyGridState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Savings Streak", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            // Increased to 182 days (approx 6 months)
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val pastDaysList = (181 downTo 0).map { daysAgo ->
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
                fmt.format(cal.time)
            }

            val heatmapMap = heatmapData.associateBy { it.formattedDateString }

            // Auto-scroll to the right (most recent day) when the grid loads
            LaunchedEffect(pastDaysList) {
                if (pastDaysList.isNotEmpty()) {
                    gridState.scrollToItem(pastDaysList.size - 1)
                }
            }

            LazyHorizontalGrid(
                rows = GridCells.Fixed(7),
                state = gridState,
                modifier = Modifier.height(120.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(pastDaysList) { dateString ->
                    val dailyContribution = heatmapMap[dateString]?.totalContributedAmount ?: 0.0

                    val blockColor = when {
                        dailyContribution == 0.0 -> Color(0xFFE5E7EB) // Gray/Empty
                        dailyContribution < 50.0 -> Color(0xFF42A5F5).copy(alpha = 0.5f) // Light Blue
                        else -> Color(0xFF1976D2) // Dark Blue for large contributions
                    }

                    Box(
                        modifier = Modifier
                            .size(14.dp) // Slightly increased block size for better visibility
                            .clip(RoundedCornerShape(2.dp))
                            .background(blockColor)
                    )
                }
            }
        }
    }
}

@Composable
fun GoalListItemCard(goalItem: SavingsGoal, onQuickAddClick: () -> Unit) {
    val progress = if (goalItem.targetSavingsAmount > 0) {
        (goalItem.currentlySavedAmount / goalItem.targetSavingsAmount).toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = goalItem.goalTitleName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\$${goalItem.currentlySavedAmount.toInt()} of \$${goalItem.targetSavingsAmount.toInt()}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(0.8f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Secondary,
                    trackColor = Light
                )
            }

            // Quick Add Button
            Surface(
                shape = CircleShape,
                color = Secondary,
                modifier = Modifier.clickable { onQuickAddClick() }.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Funds",
                    tint = Primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun AddFundsDialog(targetGoal: SavingsGoal, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var inputAmount by remember { mutableStateOf("") }

    // Calculate exactly how much is left to save
    val maxAddable = targetGoal.targetSavingsAmount - targetGoal.currentlySavedAmount

    val parsedAmount = inputAmount.toDoubleOrNull() ?: 0.0
    val isError = parsedAmount > maxAddable

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to ${targetGoal.goalTitleName}", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = inputAmount,
                    onValueChange = { inputAmount = it },
                    label = { Text("Amount") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Cannot exceed remaining \$${maxAddable.toInt()}", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Remaining to save: \$${maxAddable.toInt()}")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (parsedAmount > 0 && !isError) onConfirm(parsedAmount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = parsedAmount > 0 && !isError
            ) {
                Text("Add Funds")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalBottomSheet(onDismiss: () -> Unit, onSaveGoal: (String, Double, Long) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create Savings Goal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Goal Name (e.g., New Laptop)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = { Text("Target Amount") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val target = targetAmount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && target > 0) {
                        // Defaulting deadline to 3 months from now for simplicity
                        val deadline = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000)
                        onSaveGoal(title, target, deadline)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Save Goal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}