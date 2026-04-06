package com.example.financeapp.ui.features.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.financeapp.data.local.entity.FinancialTransaction
import com.example.financeapp.data.models.CategorySpendingSummary
import com.example.financeapp.data.models.DailySpendingSummary


private val priBlu = Color(0xFF1976D2)
private val secBlu = Color(0xFF42A5F5)
private val terBlu = Color(0xFF0D47A1)
private val surBlu = Color(0xFFE3F2FD)

@Composable
fun HomeScreen(
    totalCurrentBalance: Double,
    totalMonthlyIncome: Double,
    totalMonthlyExpenses: Double,
    savingsRatePercentage: Double,
    recentTransactions: List<FinancialTransaction>,
    weeklySpendingData: List<DailySpendingSummary>,
    categorySpendingData: List<CategorySpendingSummary>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BalanceSummaryCard(
                balance = totalCurrentBalance,
                income = totalMonthlyIncome,
                expenses = totalMonthlyExpenses
            )
        }
        item {
            SavingsRateCard(rate = savingsRatePercentage)
        }
        item {
            WeeklySpendingChart(dailyData = weeklySpendingData)
        }
        item {
            TopCategoriesChart(categoryData = categorySpendingData)
        }
        item {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(recentTransactions) { transaction ->
            TransactionListItem(transactionItem = transaction)
        }
    }
}

@Composable
fun BalanceSummaryCard(balance: Double, income: Double, expenses: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(
                color = priBlu,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total Balance",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\$${balance}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Income", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("\$$income", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Expenses", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("\$$expenses", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsRateCard(rate: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = priBlu)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Savings Rate",
                    color = surBlu,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${"%.2f".format(rate)}%",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WeeklySpendingChart(dailyData: List<DailySpendingSummary>) {
    val maximumSpendingAmount = dailyData.maxOfOrNull { it.spendingAmount } ?: 1f

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = surBlu
                ) {
                    Text(
                        text = "7 days",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = priBlu,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dailyData.forEach { dailySummary ->
                    val calculatedHeightPercentage = dailySummary.spendingAmount / maximumSpendingAmount
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight(calculatedHeightPercentage)
                        ) {
                            drawRoundRect(
                                color = priBlu,
                                size = Size(size.width, size.height),
                                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dailySummary.dayOfWeek,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopCategoriesChart(categoryData: List<CategorySpendingSummary>) {
    val totalAmountSpentAcrossCategories = categoryData.sumOf { it.totalAmountSpent }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Top Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(
                    modifier = Modifier.size(120.dp).padding(8.dp)
                ) {
                    var currentStartAngle = -90f
                    val chartStrokeWidth = 24.dp.toPx()

                    categoryData.forEach { categorySummary ->
                        val calculatedSweepAngle = ((categorySummary.totalAmountSpent / totalAmountSpentAcrossCategories) * 360f).toFloat()
                        drawArc(
                            color = categorySummary.categoryIndicatorColor,
                            startAngle = currentStartAngle,
                            sweepAngle = calculatedSweepAngle - 4f,
                            useCenter = false,
                            style = Stroke(width = chartStrokeWidth, cap = StrokeCap.Round)
                        )
                        currentStartAngle += calculatedSweepAngle
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categoryData.forEach { categorySummary ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.size(12.dp).clip(CircleShape).background(categorySummary.categoryIndicatorColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = categorySummary.categoryName,
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "\$${categorySummary.totalAmountSpent.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = terBlu
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(transactionItem: FinancialTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = transactionItem.categoryName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transactionItem.transactionDescriptionNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = if(transactionItem.transactionType == "income") "+\$${transactionItem.transactionAmount}" else "-\$${transactionItem.transactionAmount}",
                color = if(transactionItem.transactionType == "income") terBlu else secBlu,
                fontWeight = FontWeight.Bold
            )
        }
    }
}