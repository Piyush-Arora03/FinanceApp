package com.example.financeapp.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.data.models.CategorySpendingSummary

@Composable
fun TopCategoriesCard(
    categorySpendingData: List<CategorySpendingSummary>,
    modifier: Modifier = Modifier
) {
    val totalSpendingAcrossCategories = categorySpendingData.sumOf { it.totalAmountSpent }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
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
                // Donut Chart Canvas
                Canvas(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp)
                ) {
                    var currentStartAngle = -90f // Start from the top
                    val chartStrokeWidth = 24.dp.toPx()

                    categorySpendingData.forEach { category ->
                        val sweepAngle = ((category.totalAmountSpent / totalSpendingAcrossCategories) * 360f).toFloat()

                        drawArc(
                            color = category.categoryIndicatorColor,
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle - 4f, // -4f to create a slight gap between segments
                            useCenter = false,
                            style = Stroke(width = chartStrokeWidth, cap = StrokeCap.Round)
                        )
                        currentStartAngle += sweepAngle
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Legend List
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categorySpendingData.forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(category.categoryIndicatorColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category.categoryName,
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$${category.totalAmountSpent.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }
                }
            }
        }
    }
}