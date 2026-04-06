package com.example.financeapp.ui.features.insight

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val priBlu = Color(0xFF1976D2)
private val secBlu = Color(0xFF42A5F5)
private val surBlu = Color(0xFFE3F2FD)

@Composable
fun InsightScreenContent(
    uiState: InsightUiState,
    currentTimeframe: Timeframe,
    onTimeframeChange: (Timeframe) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF3F4F6)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Timeframe Toggle
        item {
            TimeframeToggle(currentTimeframe, onTimeframeChange)
        }

        // 2. Summary & Takeaways
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Key Takeaways", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• You spent \$${uiState.totalExpense.toInt()} this period.", color = Color.Gray)
                    if (uiState.topExpenseCategory != "None") {
                        Text("• Your highest spending was on ${uiState.topExpenseCategory}.", color = Color.Gray)
                    }
                }
            }
        }

        // 3. Donut Chart (Expenses)
        item {
            DonutChartCard(uiState.totalExpense, uiState.categoryBreakdown)
        }

        // 4. Income vs Expense Bar Chart
        item {
            ComparisonBarChart(uiState.totalIncome, uiState.totalExpense)
        }
    }
}

@Composable
fun TimeframeToggle(current: Timeframe, onSelect: (Timeframe) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(surBlu, RoundedCornerShape(12.dp)).padding(4.dp)
    ) {
        listOf(Timeframe.MONTH to "This Month", Timeframe.YEAR to "This Year").forEach { (timeframe, label) ->
            val isSelected = current == timeframe
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) priBlu else Color.Transparent)
                    .clickable { onSelect(timeframe) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, color = if (isSelected) Color.White else priBlu, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DonutChartCard(totalExpense: Double, slices: List<CategorySlice>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Where Your Money Went", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    var currentAngle = -90f
                    if (slices.isEmpty()) {
                        drawArc(color = Color.LightGray, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 40f, cap = StrokeCap.Round))
                    } else {
                        slices.forEach { slice ->
                            val sweepAngle = ((slice.amount / totalExpense) * 360f).toFloat()
                            drawArc(
                                color = slice.color,
                                startAngle = currentAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 50f)
                            )
                            currentAngle += sweepAngle
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total", color = Color.Gray, fontSize = 14.sp)
                    Text("\$${totalExpense.toInt()}", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = priBlu)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legend
            slices.forEach { slice ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(slice.color))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(slice.category, modifier = Modifier.weight(1f), color = Color.Gray)
                    Text("\$${slice.amount.toInt()}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ComparisonBarChart(income: Double, expense: Double) {
    val maxAmount = maxOf(income, expense, 1.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Cash Flow", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                // Income Bar
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("\$${income.toInt()}", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Canvas(modifier = Modifier.width(40.dp).fillMaxHeight((income / maxAmount).toFloat())) {
                        drawRoundRect(color = Color(0xFF43A047), size = Size(size.width, size.height), cornerRadius = CornerRadius(12.dp.toPx()))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Income", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Expense Bar
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("\$${expense.toInt()}", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Canvas(modifier = Modifier.width(40.dp).fillMaxHeight((expense / maxAmount).toFloat())) {
                        drawRoundRect(color = Color(0xFFE53935), size = Size(size.width, size.height), cornerRadius = CornerRadius(12.dp.toPx()))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Expenses", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}