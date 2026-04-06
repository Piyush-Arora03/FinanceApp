package com.example.financeapp.ui.features.transaction

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.data.local.entity.FinancialTransaction
import java.text.SimpleDateFormat
import java.util.*

private val priBlu = Color(0xFF1976D2)
private val secBlu = Color(0xFF42A5F5)
private val terBlu = Color(0xFF0D47A1)
private val surBlu = Color(0xFFE3F2FD)

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    searchQueryText: String,
    activeFilterType: String,
    filteredTransactionsList: List<FinancialTransaction>,
    onSearchQueryChanged: (String) -> Unit,
    onFilterTypeSelected: (String) -> Unit,
    onDeleteTransactionClick: (FinancialTransaction) -> Unit,
    generateCsvData: (List<FinancialTransaction>) -> String
) {
    val context = LocalContext.current
    var currentCsvData by remember { mutableStateOf("") }

    // This launcher opens the system UI to let the user choose where to save the file
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { targetUri ->
            try {
                // Write the CSV string into the file the user selected
                context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                    outputStream.write(currentCsvData.toByteArray())
                }
                Toast.makeText(context, "Export Successful!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF3F4F6))) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(color = priBlu, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Transactions", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                IconButton(
                    onClick = {
                        if (filteredTransactionsList.isNotEmpty()) {
                            currentCsvData = generateCsvData(filteredTransactionsList)
                            // Suggest a default filename with the current date
                            val filename = "FinanceExport_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.csv"
                            exportLauncher.launch(filename)
                        } else {
                            Toast.makeText(context, "No transactions to export", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Export CSV", tint = Color.White)
                }

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Income", "Expense").forEach { filterOpt ->
                        FilterChipComponent(
                            filterLabel = filterOpt,
                            isSelected = activeFilterType == filterOpt,
                            onFilterSelected = { onFilterTypeSelected(filterOpt) }
                        )
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val fmt = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            val groupedTx = filteredTransactionsList.groupBy { fmt.format(Date(it.transactionDateTimestamp)) }
            groupedTx.forEach { (dateStr, dailyTx) ->
                item {
                    Text(text = dateStr, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                }
                items(dailyTx) { tx ->
                    TransactionListItemCard(transactionItem = tx, onDeleteClick = { onDeleteTransactionClick(tx) })
                }
            }
        }
    }
}

@Composable
fun FilterChipComponent(filterLabel: String, isSelected: Boolean, onFilterSelected: () -> Unit) {
    Surface(modifier = Modifier.clickable { onFilterSelected() }, shape = RoundedCornerShape(20.dp), color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f)) {
        Text(text = filterLabel, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = if (isSelected) priBlu else Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TransactionListItemCard(transactionItem: FinancialTransaction, onDeleteClick: () -> Unit) {
    val isIncome = transactionItem.transactionType.equals("income", ignoreCase = true)
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(color = if (isIncome) terBlu else secBlu, shape = RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Text(text = transactionItem.categoryName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transactionItem.categoryName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = transactionItem.transactionDescriptionNote, color = Color.Gray, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isIncome) "+\$${transactionItem.transactionAmount}" else "-\$${transactionItem.transactionAmount}",
                    color = if (isIncome) terBlu else secBlu,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = priBlu, modifier = Modifier.size(20.dp).clickable { TODO() })
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFD50000), modifier = Modifier.size(20.dp).clickable { onDeleteClick() })
                }
            }
        }
    }
}