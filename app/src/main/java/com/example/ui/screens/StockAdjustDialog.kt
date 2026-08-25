package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.ItemEntity
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateMuted

@Composable
fun StockAdjustDialog(
    item: ItemEntity,
    onDismiss: () -> Unit,
    onConfirmAdjust: (Double) -> Unit
) {
    var isStockIn by remember { mutableStateOf(true) } // true = Add Stock, false = Reduce Stock
    var quantityText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Adjust Stock",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                        Text(
                            text = item.name,
                            fontSize = 13.sp,
                            color = SlateMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Current Stock Indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(text = "Current In Stock", fontSize = 12.sp, color = SlateMuted)
                        Text(
                            text = "${item.stockQuantity.toInt()} ${item.unit}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Type Selector (Stock In vs Stock Out)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = isStockIn,
                        onClick = { isStockIn = true },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stock In (+)", fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeGreen,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = !isStockIn,
                        onClick = { isStockIn = false },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stock Out (-)", fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseRed,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quantity Input
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        quantityText = it
                        error = false
                    },
                    label = { Text("Quantity (${item.unit}) *") },
                    placeholder = { Text("e.g. 10") },
                    isError = error,
                    supportingText = if (error) { { Text("Enter a valid quantity", color = ExpenseRed) } } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isStockIn) IncomeGreen else ExpenseRed,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stock_adjust_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reason / Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reason / Remarks (Optional)") },
                    placeholder = { Text(if (isStockIn) "e.g. New stock arrived" else "e.g. Damaged / Sample / Count audit") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Confirm Button
                Button(
                    onClick = {
                        val qty = quantityText.toDoubleOrNull()
                        if (qty == null || qty <= 0) {
                            error = true
                            return@Button
                        }
                        val delta = if (isStockIn) qty else -qty
                        onConfirmAdjust(delta)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStockIn) IncomeGreen else ExpenseRed
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_stock_adjust_button")
                ) {
                    Text(
                        text = if (isStockIn) "Confirm Stock In (+)" else "Confirm Stock Out (-)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
