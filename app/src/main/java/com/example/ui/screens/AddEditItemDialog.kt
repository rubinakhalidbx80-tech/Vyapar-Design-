package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.ItemEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateMuted

@Composable
fun AddEditItemDialog(
    initialItem: ItemEntity?,
    onDismiss: () -> Unit,
    onSave: (ItemEntity) -> Unit,
    onDelete: ((ItemEntity) -> Unit)? = null
) {
    val isEdit = initialItem != null
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var code by remember { mutableStateOf(initialItem?.code ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "General") }
    var hsn by remember { mutableStateOf(initialItem?.hsn ?: "") }
    var unit by remember { mutableStateOf(initialItem?.unit ?: "PCS") }
    var salePriceText by remember { mutableStateOf(if (initialItem != null) initialItem.salePrice.toString() else "") }
    var purchasePriceText by remember { mutableStateOf(if (initialItem != null) initialItem.purchasePrice.toString() else "") }
    var taxRate by remember { mutableStateOf(initialItem?.taxRate ?: 5.0) }
    var stockQuantityText by remember { mutableStateOf(if (initialItem != null) initialItem.stockQuantity.toString() else "10") }
    var minStockText by remember { mutableStateOf(if (initialItem != null) initialItem.minStockAlert.toString() else "5") }
    var notes by remember { mutableStateOf(initialItem?.notes ?: "") }

    var nameError by remember { mutableStateOf(false) }

    val units = listOf("PCS", "BAG", "KG", "BOX", "TIN", "PKT", "LTR", "MTR")
    val taxRates = listOf(0.0, 5.0, 12.0, 18.0, 28.0)
    val categories = listOf("General", "Grains & Pulses", "Oils & Ghee", "Flour & Atta", "Grocery Essentials", "Cleaning & Household", "Confectionery")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEdit) "Edit Inventory Item" else "Add New Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Item Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Item / Product Name *") },
                    placeholder = { Text("e.g. Basmati Rice 25kg, Tata Salt 1kg") },
                    isError = nameError,
                    supportingText = if (nameError) { { Text("Product name is required", color = ExpenseRed) } } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SKU & HSN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Item Code / SKU") },
                        placeholder = { Text("RIC-001") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = hsn,
                        onValueChange = { hsn = it },
                        label = { Text("HSN / SAC Code") },
                        placeholder = { Text("1006") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Chips
                Text("CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Unit Selector Chips
                Text("UNIT MEASURE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    units.forEach { u ->
                        FilterChip(
                            selected = unit == u,
                            onClick = { unit = u },
                            label = { Text(u, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sale Price & Purchase Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = salePriceText,
                        onValueChange = { salePriceText = it },
                        label = { Text("Sale Price (₹) *") },
                        placeholder = { Text("2450.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Purchase Price (₹)") },
                        placeholder = { Text("2100.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // GST Tax Rate
                Text("GST TAX RATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    taxRates.forEach { rate ->
                        FilterChip(
                            selected = taxRate == rate,
                            onClick = { taxRate = rate },
                            label = { Text("${rate.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stock & Low Stock Alert
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = stockQuantityText,
                        onValueChange = { stockQuantityText = it },
                        label = { Text("Opening Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minStockText,
                        onValueChange = { minStockText = it },
                        label = { Text("Low Stock Alert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEdit && onDelete != null && initialItem != null) {
                        OutlinedButton(
                            onClick = { onDelete(initialItem) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                                return@Button
                            }
                            val sp = salePriceText.toDoubleOrNull() ?: 0.0
                            val pp = purchasePriceText.toDoubleOrNull() ?: 0.0
                            val stock = stockQuantityText.toDoubleOrNull() ?: 0.0
                            val minStock = minStockText.toDoubleOrNull() ?: 5.0

                            val item = ItemEntity(
                                id = initialItem?.id ?: 0L,
                                name = name.trim(),
                                code = code.trim(),
                                category = category.trim(),
                                hsn = hsn.trim(),
                                unit = unit,
                                salePrice = sp,
                                purchasePrice = pp,
                                taxRate = taxRate,
                                stockQuantity = stock,
                                minStockAlert = minStock,
                                notes = notes.trim()
                            )
                            onSave(item)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("save_item_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEdit) "Update Item" else "Save Item", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
