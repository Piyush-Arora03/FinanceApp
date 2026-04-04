package com.example.financeapp.ui.features.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.reflect.KFunction1

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.data.local.FinancialTransaction
import com.example.financeapp.data.models.SpendingCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionRoute(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val currentSearchQuery by viewModel.searchQueryText.collectAsStateWithLifecycle()
    val currentActiveFilter by viewModel.activeFilterType.collectAsStateWithLifecycle()
    val currentFilteredTransactions by viewModel.filteredTransactionsState.collectAsStateWithLifecycle()

    var isAddTransactionSheetVisible = remember { mutableStateOf(false) }
    val primaryBlue = Color(0xFF1976D2)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddTransactionSheetVisible.value = true },
                containerColor = primaryBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Transaction")
            }
        }
    ) { scaffoldPaddingValues ->
        TransactionScreen(
            modifier = Modifier.padding(scaffoldPaddingValues),
            searchQueryText = currentSearchQuery,
            activeFilterType = currentActiveFilter,
            filteredTransactionsList = currentFilteredTransactions,
            onSearchQueryChanged = viewModel::updateSearchQueryText,
            onFilterTypeSelected = viewModel::updateActiveFilterType,
            onDeleteTransactionClick = viewModel::deleteSpecificTransaction
        )

        if (isAddTransactionSheetVisible.value) {
            AddTransactionBottomSheet(
                onDismissRequest = { isAddTransactionSheetVisible.value = false },
                onSaveTransaction = { amount, type, category, description ->
                    viewModel.saveNewTransaction(amount, type, category, description)
                    isAddTransactionSheetVisible.value = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismissRequest: () -> Unit,
    onSaveTransaction: (Double, String, String, String) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val primaryBlue = Color(0xFF1976D2)

    var selectedTransactionType = remember { mutableStateOf("expense") }
    var enteredTransactionAmount = remember { mutableStateOf("") }
    var enteredTransactionDescription = remember { mutableStateOf("") }
    var selectedCategoryName = remember { mutableStateOf(SpendingCategory.Other) }
    var isCategoryDropdownExpanded = remember { mutableStateOf(false) }

    val availableCategoriesList = listOf(
        SpendingCategory.FoodAndDining, SpendingCategory.Transportation,
        SpendingCategory.Shopping, SpendingCategory.Entertainment,
        SpendingCategory.HealthAndBeauty, SpendingCategory.Home,
        SpendingCategory.Education, SpendingCategory.Utilities,
        SpendingCategory.Travel, SpendingCategory.Other
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Add Transaction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TabRow(
                selectedTabIndex = if (selectedTransactionType.value == "expense") 0 else 1,
                containerColor = Color(0xFFF3F4F6),
                indicator = { },
                divider = { }
            ) {
                Tab(
                    selected = selectedTransactionType.value == "expense",
                    onClick = { selectedTransactionType.value = "expense" },
                    modifier = Modifier.background(if (selectedTransactionType.value == "expense") primaryBlue else Color.Transparent)
                ) {
                    Text("Expense", modifier = Modifier.padding(12.dp), color = if (selectedTransactionType.value == "expense") Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                }
                Tab(
                    selected = selectedTransactionType.value == "income",
                    onClick = { selectedTransactionType.value = "income" },
                    modifier = Modifier.background(if (selectedTransactionType.value == "income") primaryBlue else Color.Transparent)
                ) {
                    Text("Income", modifier = Modifier.padding(12.dp), color = if (selectedTransactionType.value == "income") Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
            OutlinedTextField(
                   value = enteredTransactionAmount.value,
                onValueChange = { enteredTransactionAmount.value = it },
                label = { Text("Amount") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded.value,
                onExpandedChange = { isCategoryDropdownExpanded.value = !isCategoryDropdownExpanded.value }
            ) {
                OutlinedTextField(
                    value = selectedCategoryName.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded.value) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded.value,
                    onDismissRequest = { isCategoryDropdownExpanded.value = false }
                ) {
                    availableCategoriesList.forEach { categoryOption ->
                        DropdownMenuItem(
                            text = { Text(categoryOption) },
                            onClick = {
                                selectedCategoryName.value = categoryOption
                                isCategoryDropdownExpanded.value = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = enteredTransactionDescription.value,
                onValueChange = { enteredTransactionDescription.value = it },
                label = { Text("Note / Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val parsedAmountValue = enteredTransactionAmount.value.toDoubleOrNull() ?: 0.0
                    if (parsedAmountValue > 0) {
                        onSaveTransaction(parsedAmountValue, selectedTransactionType.value, selectedCategoryName.value, enteredTransactionDescription.value)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Save Transaction", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}