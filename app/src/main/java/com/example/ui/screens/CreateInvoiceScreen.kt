package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.ItemEntity
import com.example.data.local.entity.PartyEntity
import com.example.data.model.InvoiceItem
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.PendingAmberLight
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateMuted

@Composable
fun CreateInvoiceScreen(
    initialType: String = "SALE",
    parties: List<PartyEntity>,
    inventoryItems: List<ItemEntity>,
    onDismiss: () -> Unit,
    onSaveTransaction: (
        type: String,
        partyId: Long?,
        partyName: String,
        partyPhone: String,
        partyGstin: String,
        date: Long,
        dueDate: Long,
        items: List<InvoiceItem>,
        discountAmount: Double,
        paidAmount: Double,
        paymentMode: String,
        expenseCategory: String,
        notes: String
    ) -> Unit
) {
    var txnType by remember { mutableStateOf(initialType) } // "SALE", "PURCHASE", "EXPENSE", "PAYMENT_IN", "PAYMENT_OUT", "ESTIMATE"

    // Party selection
    var selectedParty by remember { mutableStateOf<PartyEntity?>(null) }
    var partyNameInput by remember { mutableStateOf("") }
    var partyPhoneInput by remember { mutableStateOf("") }
    var partyGstinInput by remember { mutableStateOf("") }
    var isPartyDropdownExpanded by remember { mutableStateOf(false) }

    // Line Items
    val invoiceItems = remember { mutableStateListOf<InvoiceItem>() }
    var showAddItemDialog by remember { mutableStateOf(false) }

    // Financials
    var overallDiscountText by remember { mutableStateOf("0") }
    var paidAmountText by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("CASH") } // "CASH", "UPI", "BANK_TRANSFER", "CHEQUE"
    var expenseCategory by remember { mutableStateOf("General") }
    var expenseAmountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val isExpense = txnType == "EXPENSE"
    val isPaymentOnly = txnType == "PAYMENT_IN" || txnType == "PAYMENT_OUT"

    // Subtotals
    val subtotal = invoiceItems.sumOf { it.totalWithoutTax }
    val totalTax = invoiceItems.sumOf { it.taxAmount }
    val overallDiscount = overallDiscountText.toDoubleOrNull() ?: 0.0

    val grandTotal = if (isExpense) {
        expenseAmountText.toDoubleOrNull() ?: 0.0
    } else if (isPaymentOnly) {
        paidAmountText.toDoubleOrNull() ?: 0.0
    } else {
        (subtotal + totalTax - overallDiscount).coerceAtLeast(0.0)
    }

    val currentPaid = if (paidAmountText.isNotBlank()) paidAmountText.toDoubleOrNull() ?: 0.0 else (if (isExpense || isPaymentOnly) grandTotal else grandTotal)
    val balanceDue = (grandTotal - currentPaid).coerceAtLeast(0.0)

    val relevantParties = when (txnType) {
        "SALE", "PAYMENT_IN", "ESTIMATE" -> parties.filter { it.type == "CUSTOMER" }
        "PURCHASE", "PAYMENT_OUT" -> parties.filter { it.type == "SUPPLIER" }
        else -> parties
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.95f)
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Header Bar
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when (txnType) {
                                    "SALE" -> "New GST Sale Invoice"
                                    "PURCHASE" -> "New Purchase Bill"
                                    "EXPENSE" -> "Record Expense"
                                    "PAYMENT_IN" -> "Record Payment In (Receipt)"
                                    "PAYMENT_OUT" -> "Record Payment Out"
                                    "ESTIMATE" -> "New Estimate / Quotation"
                                    else -> "New Transaction"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Type Switcher Chips
                        val types = listOf(
                            "SALE" to "Sale",
                            "PURCHASE" to "Purchase",
                            "EXPENSE" to "Expense",
                            "PAYMENT_IN" to "Payment In",
                            "PAYMENT_OUT" to "Payment Out",
                            "ESTIMATE" to "Estimate"
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            types.forEach { (t, label) ->
                                val isSelected = txnType == t
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        txnType = t
                                        selectedParty = null
                                    },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // 2. Scrollable Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Party Selection Box (if not expense)
                    if (!isExpense) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (txnType == "SALE" || txnType == "PAYMENT_IN" || txnType == "ESTIMATE") "CUSTOMER DETAILS" else "SUPPLIER DETAILS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMuted
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Select Existing Party Button / Dropdown
                                Box {
                                    OutlinedButton(
                                        onClick = { isPartyDropdownExpanded = true },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = selectedParty?.name ?: "Select from Existing Parties (${relevantParties.size})",
                                                color = if (selectedParty != null) SlateDark else SlateMuted,
                                                fontWeight = if (selectedParty != null) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = isPartyDropdownExpanded,
                                        onDismissRequest = { isPartyDropdownExpanded = false }
                                    ) {
                                        relevantParties.forEach { party ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(party.name, fontWeight = FontWeight.Bold)
                                                        Text("Phone: ${party.phone.ifBlank { "N/A" }} • Bal: ${formatCurrency(party.currentBalance)}", fontSize = 11.sp, color = SlateMuted)
                                                    }
                                                },
                                                onClick = {
                                                    selectedParty = party
                                                    partyNameInput = party.name
                                                    partyPhoneInput = party.phone
                                                    partyGstinInput = party.gstin
                                                    isPartyDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Or Manual Entry
                                OutlinedTextField(
                                    value = partyNameInput,
                                    onValueChange = {
                                        partyNameInput = it
                                        if (selectedParty?.name != it) selectedParty = null
                                    },
                                    label = { Text("Or Enter Party / Walk-in Name") },
                                    placeholder = { Text("e.g. Cash Customer, Apex Industries") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldPrimary,
                                        unfocusedBorderColor = SlateBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("invoice_party_name_input")
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = partyPhoneInput,
                                        onValueChange = { partyPhoneInput = it },
                                        label = { Text("Phone") },
                                        placeholder = { Text("+91...") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = SlateBorder
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = partyGstinInput,
                                        onValueChange = { partyGstinInput = it },
                                        label = { Text("GSTIN") },
                                        placeholder = { Text("27AAAA...") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = SlateBorder
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Expense Specific Inputs
                    if (isExpense) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("EXPENSE CATEGORY & AMOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                Spacer(modifier = Modifier.height(8.dp))

                                val expCats = listOf("General", "Electricity & Utilities", "Shop Rent", "Staff Salary", "Logistics & Transport", "Tea & Refreshments", "Marketing & Ads")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    expCats.forEach { cat ->
                                        FilterChip(
                                            selected = expenseCategory == cat,
                                            onClick = { expenseCategory = cat },
                                            label = { Text(cat, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = ExpenseRed,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = partyNameInput,
                                    onValueChange = { partyNameInput = it },
                                    label = { Text("Paid To / Vendor Name") },
                                    placeholder = { Text("e.g. MSEDCL, Shop Landlord, Transport Tempo") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldPrimary,
                                        unfocusedBorderColor = SlateBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = expenseAmountText,
                                    onValueChange = { expenseAmountText = it },
                                    label = { Text("Total Expense Amount (₹) *") },
                                    placeholder = { Text("e.g. 2500") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ExpenseRed,
                                        unfocusedBorderColor = SlateBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Payment-Only specific input (e.g. direct receipt of money)
                    if (isPaymentOnly) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("PAYMENT AMOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                Spacer(modifier = Modifier.height(8.dp))

                                if (selectedParty != null) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = IncomeGreenLight,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.padding(10.dp)
                                        ) {
                                            Text("Current Outstanding Due:", fontSize = 12.sp, color = SlateMuted)
                                            Text(formatCurrency(selectedParty?.currentBalance ?: 0.0), fontWeight = FontWeight.Bold, color = IncomeGreen)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                OutlinedTextField(
                                    value = paidAmountText,
                                    onValueChange = { paidAmountText = it },
                                    label = { Text("Amount Received / Paid (₹) *") },
                                    placeholder = { Text("e.g. 5000") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldPrimary,
                                        unfocusedBorderColor = SlateBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Line Items Table (For Sale / Purchase / Estimate)
                    if (!isExpense && !isPaymentOnly) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("ITEMS & PRODUCTS (${invoiceItems.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                    Button(
                                        onClick = { showAddItemDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight, contentColor = EmeraldPrimary),
                                        modifier = Modifier.testTag("add_item_to_invoice_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (invoiceItems.isEmpty()) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 20.dp)
                                    ) {
                                        Text("No items added yet. Tap '+ Add Item' to select from inventory.", fontSize = 12.sp, color = SlateMuted)
                                    }
                                } else {
                                    invoiceItems.forEachIndexed { index, item ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(item.itemName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                                    Text(
                                                        text = "${item.quantity} ${item.unit} x ${formatCurrency(item.unitPrice)} • GST ${item.taxRate.toInt()}%",
                                                        fontSize = 11.sp,
                                                        color = SlateMuted
                                                    )
                                                }

                                                Text(formatCurrency(item.totalAmount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)

                                                IconButton(
                                                    onClick = { invoiceItems.removeAt(index) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = ExpenseRed, modifier = Modifier.size(16.dp))
                                                }
                                            }

                                            if (index < invoiceItems.size - 1) {
                                                HorizontalDivider(modifier = Modifier.padding(top = 6.dp), color = Color(0xFFF1F5F9))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Summary Calculations Card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("TAX & PAYMENT SUMMARY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Subtotal (Taxable):", fontSize = 13.sp, color = SlateMuted)
                                    Text(formatCurrency(subtotal), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total GST Tax:", fontSize = 13.sp, color = SlateMuted)
                                    Text(formatCurrency(totalTax), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Discount (₹):", fontSize = 13.sp, color = SlateMuted)
                                    OutlinedTextField(
                                        value = overallDiscountText,
                                        onValueChange = { overallDiscountText = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EmeraldPrimary,
                                            unfocusedBorderColor = SlateBorder
                                        ),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SlateBorder)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Grand Total:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                    Text(formatCurrency(grandTotal), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                }
                            }
                        }
                    }

                    // Payment Mode & Paid Amount (For Sale / Purchase)
                    if (!isExpense && !isPaymentOnly) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("PAYMENT SETTLEMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Amount Chips
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FilterChip(
                                        selected = paidAmountText == grandTotal.toString() || (paidAmountText.isBlank() && grandTotal > 0),
                                        onClick = { paidAmountText = grandTotal.toString() },
                                        label = { Text("Full Paid (${formatCurrency(grandTotal)})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = IncomeGreen,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChip(
                                        selected = paidAmountText == "0",
                                        onClick = { paidAmountText = "0" },
                                        label = { Text("Unpaid (Credit)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ExpenseRed,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = paidAmountText,
                                    onValueChange = { paidAmountText = it },
                                    label = { Text("Amount Paid Now (₹)") },
                                    placeholder = { Text(grandTotal.toString()) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldPrimary,
                                        unfocusedBorderColor = SlateBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("PAYMENT MODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                val modes = listOf("CASH" to "Cash", "UPI" to "UPI / QR", "BANK_TRANSFER" to "Bank", "CHEQUE" to "Cheque")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    modes.forEach { (mode, label) ->
                                        FilterChip(
                                            selected = paymentMode == mode,
                                            onClick = { paymentMode = mode },
                                            label = { Text(label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = EmeraldPrimary,
                                                selectedLabelColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Notes / Remarks
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Notes / Transport / Reference") },
                                placeholder = { Text("e.g. Delivered via tempo, PO #123") },
                                maxLines = 2,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = SlateBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 3. Bottom Confirm Strip
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("Total Amount", fontSize = 11.sp, color = SlateMuted)
                            Text(formatCurrency(grandTotal), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                            if (!isExpense && !isPaymentOnly && balanceDue > 0.01) {
                                Text("Balance Due: ${formatCurrency(balanceDue)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                            }
                        }

                        Button(
                            onClick = {
                                val paid = if (paidAmountText.isNotBlank()) paidAmountText.toDoubleOrNull() ?: 0.0 else (if (isExpense || isPaymentOnly) grandTotal else grandTotal)
                                val partyName = partyNameInput.ifBlank { selectedParty?.name ?: (if (txnType == "SALE") "Cash Customer" else "Cash Supplier") }
                                val partyPhone = partyPhoneInput.ifBlank { selectedParty?.phone ?: "" }
                                val partyGstin = partyGstinInput.ifBlank { selectedParty?.gstin ?: "" }

                                onSaveTransaction(
                                    txnType,
                                    selectedParty?.id,
                                    partyName,
                                    partyPhone,
                                    partyGstin,
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000),
                                    invoiceItems.toList(),
                                    overallDiscount,
                                    paid,
                                    paymentMode,
                                    expenseCategory,
                                    notes
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("confirm_save_invoice_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save & Generate", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to Pick / Add an Item to the Invoice
    if (showAddItemDialog) {
        AddItemToInvoiceDialog(
            inventoryItems = inventoryItems,
            onDismiss = { showAddItemDialog = false },
            onAddItem = { newItem ->
                invoiceItems.add(newItem)
                showAddItemDialog = false
            }
        )
    }
}

@Composable
fun AddItemToInvoiceDialog(
    inventoryItems: List<ItemEntity>,
    onDismiss: () -> Unit,
    onAddItem: (InvoiceItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf<ItemEntity?>(null) }

    var itemName by remember { mutableStateOf("") }
    var hsn by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("PCS") }
    var unitPriceText by remember { mutableStateOf("") }
    var taxRate by remember { mutableStateOf(5.0) }
    var discountPercentText by remember { mutableStateOf("0") }

    val filteredInventory = inventoryItems.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.code.contains(searchQuery, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select / Add Item", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SlateDark)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search or Choose from Inventory
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search inventory products...", fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SlateMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable inventory list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredInventory) { item ->
                        val isPicked = selectedItem?.id == item.id
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPicked) EmeraldLight else Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedItem = item
                                    itemName = item.name
                                    hsn = item.hsn
                                    unit = item.unit
                                    unitPriceText = item.salePrice.toString()
                                    taxRate = item.taxRate
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Column {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateDark)
                                    Text("Stock: ${item.stockQuantity.toInt()} ${item.unit} • GST: ${item.taxRate.toInt()}%", fontSize = 11.sp, color = SlateMuted)
                                }
                                Text(formatCurrency(item.salePrice), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Item Details Form
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, unfocusedBorderColor = SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, unfocusedBorderColor = SlateBorder),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unitPriceText,
                        onValueChange = { unitPriceText = it },
                        label = { Text("Rate / Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, unfocusedBorderColor = SlateBorder),
                        modifier = Modifier.weight(1.5f)
                    )

                    OutlinedTextField(
                        value = discountPercentText,
                        onValueChange = { discountPercentText = it },
                        label = { Text("Disc %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, unfocusedBorderColor = SlateBorder),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val qty = quantityText.toDoubleOrNull() ?: 1.0
                        val price = unitPriceText.toDoubleOrNull() ?: 0.0
                        val disc = discountPercentText.toDoubleOrNull() ?: 0.0
                        if (itemName.isNotBlank() && price > 0) {
                            onAddItem(
                                InvoiceItem(
                                    itemId = selectedItem?.id ?: 0L,
                                    itemName = itemName.trim(),
                                    hsn = hsn.trim(),
                                    quantity = qty,
                                    unit = unit,
                                    unitPrice = price,
                                    taxRate = taxRate,
                                    discountPercent = disc
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add To Invoice", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
