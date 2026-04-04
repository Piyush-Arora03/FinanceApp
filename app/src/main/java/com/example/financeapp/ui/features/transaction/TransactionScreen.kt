package com.example.financeapp.ui.features.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.data.local.FinancialTransaction
import com.example.financeapp.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    searchQueryText: String,
    activeFilterType: String,
    filteredTransactionsList: List<FinancialTransaction>,
    onSearchQueryChanged: (String) -> Unit,
    onFilterTypeSelected: (String) -> Unit,
    onDeleteTransactionClick: (FinancialTransaction) -> Unit
) {

    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF3F4F6))) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Primary.copy(0.2f),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Transactions",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQueryText,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Search transactions...", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Income", "Expense").forEach { filterOptionName ->
                        FilterChipComponent(
                            filterLabel = filterOptionName,
                            isSelected = activeFilterType == filterOptionName,
                            onFilterSelected = { onFilterTypeSelected(filterOptionName) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val dateFormatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            val groupedTransactionsMap = filteredTransactionsList.groupBy { transaction ->
                dateFormatter.format(Date(transaction.transactionDateTimestamp))
            }

            groupedTransactionsMap.forEach { (formattedDateString, dailyTransactionsList) ->
                item {
                    Text(
                        text = formattedDateString,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                items(dailyTransactionsList) { financialTransaction ->
                    TransactionListItemCard(
                        transactionItem = financialTransaction,
                        onDeleteClick = { onDeleteTransactionClick(financialTransaction) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipComponent(
    filterLabel: String,
    isSelected: Boolean,
    onFilterSelected: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onFilterSelected() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f)
    ) {
        Text(
            text = filterLabel,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color(0xFFD500F9) else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TransactionListItemCard(
    transactionItem: FinancialTransaction,
    onDeleteClick: () -> Unit
) {
    val isIncomeTransaction = transactionItem.transactionType.equals("income", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isIncomeTransaction) Color(0xFF00E676) else Color(0xFFE040FB),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = transactionItem.categoryName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = transactionItem.categoryName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = transactionItem.transactionDescriptionNote, color = Color.Gray, fontSize = 14.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isIncomeTransaction) "+\$${transactionItem.transactionAmount}" else "-\$${transactionItem.transactionAmount}",
                    color = if (isIncomeTransaction) Color(0xFF00C853) else Color(0xFFD50000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.size(20.dp).clickable { TODO() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFFD50000),
                        modifier = Modifier.size(20.dp).clickable { onDeleteClick() }
                    )
                }
            }
        }
    }
}