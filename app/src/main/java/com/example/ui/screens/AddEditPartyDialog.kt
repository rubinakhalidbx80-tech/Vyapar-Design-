package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.PartyEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateMuted

@Composable
fun AddEditPartyDialog(
    initialParty: PartyEntity?,
    defaultType: String = "CUSTOMER",
    onDismiss: () -> Unit,
    onSave: (PartyEntity) -> Unit,
    onDelete: ((PartyEntity) -> Unit)? = null
) {
    val isEdit = initialParty != null
    var name by remember { mutableStateOf(initialParty?.name ?: "") }
    var type by remember { mutableStateOf(initialParty?.type ?: if (defaultType == "SUPPLIER") "SUPPLIER" else "CUSTOMER") }
    var phone by remember { mutableStateOf(initialParty?.phone ?: "") }
    var email by remember { mutableStateOf(initialParty?.email ?: "") }
    var gstin by remember { mutableStateOf(initialParty?.gstin ?: "") }
    var address by remember { mutableStateOf(initialParty?.billingAddress ?: "") }
    var openingBalanceText by remember { mutableStateOf(if (initialParty != null) initialParty.openingBalance.toString() else "0") }
    var creditLimitText by remember { mutableStateOf(if (initialParty != null) initialParty.creditLimit.toString() else "0") }

    var nameError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
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
                        text = if (isEdit) "Edit Party Details" else "Add New Party",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Party Type Selector
                Text(
                    text = "PARTY TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMuted
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = type == "CUSTOMER",
                        onClick = { type = "CUSTOMER" },
                        label = { Text("Customer (Buyer)", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == "SUPPLIER",
                        onClick = { type = "SUPPLIER" },
                        label = { Text("Supplier (Vendor)", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Party Name *") },
                    placeholder = { Text("e.g. Ramesh Traders, Pooja Store") },
                    isError = nameError,
                    supportingText = if (nameError) { { Text("Party name is required", color = ExpenseRed) } } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("party_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile / Phone Number") },
                    placeholder = { Text("+91 98765 43210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // GSTIN
                OutlinedTextField(
                    value = gstin,
                    onValueChange = { gstin = it.uppercase() },
                    label = { Text("GSTIN (Optional)") },
                    placeholder = { Text("27AAAAA0000A1Z5") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Billing / Shipping Address") },
                    placeholder = { Text("Shop #, Market Road, City, State, PIN") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opening Balance & Credit Limit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = openingBalanceText,
                        onValueChange = { openingBalanceText = it },
                        label = { Text("Opening Due (₹)") },
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
                        value = creditLimitText,
                        onValueChange = { creditLimitText = it },
                        label = { Text("Credit Limit (₹)") },
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

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEdit && onDelete != null && initialParty != null) {
                        OutlinedButton(
                            onClick = { onDelete(initialParty) },
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
                            val openBal = openingBalanceText.toDoubleOrNull() ?: 0.0
                            val credLimit = creditLimitText.toDoubleOrNull() ?: 0.0
                            val party = PartyEntity(
                                id = initialParty?.id ?: 0L,
                                name = name.trim(),
                                phone = phone.trim(),
                                email = email.trim(),
                                gstin = gstin.trim(),
                                type = type,
                                billingAddress = address.trim(),
                                openingBalance = openBal,
                                currentBalance = if (initialParty != null) initialParty.currentBalance else openBal,
                                creditLimit = credLimit
                            )
                            onSave(party)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("save_party_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEdit) "Update Party" else "Save Party", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
