package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.InvoiceItemJsonHelper
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.TransactionEntity
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
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.SlateMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceDetailDialog(
    transaction: TransactionEntity,
    businessProfile: BusinessProfileEntity?,
    onDismiss: () -> Unit,
    onDelete: (TransactionEntity) -> Unit
) {
    val context = LocalContext.current
    val items = InvoiceItemJsonHelper.fromJson(transaction.itemsJson)
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault())

    val isSale = transaction.type == "SALE"
    val isPurchase = transaction.type == "PURCHASE"
    val isExpense = transaction.type == "EXPENSE"

    val titleLabel = when (transaction.type) {
        "SALE" -> "TAX INVOICE"
        "PURCHASE" -> "PURCHASE BILL"
        "EXPENSE" -> "EXPENSE VOUCHER"
        "PAYMENT_IN" -> "PAYMENT RECEIPT"
        "PAYMENT_OUT" -> "PAYMENT VOUCHER"
        "ESTIMATE" -> "ESTIMATE / QUOTATION"
        else -> "TRANSACTION VOUCHER"
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
                .fillMaxHeight(0.96f)
                .padding(vertical = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Action Header
                Surface(
                    color = SlateDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = transaction.invoiceNumber,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    val billText = buildShareText(transaction, businessProfile, items)
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, billText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Tax Invoice"))
                                },
                                modifier = Modifier.testTag("share_invoice_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                            }

                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }
                }

                // Printable Invoice Paper Layout
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Business Header
                            Text(
                                text = businessProfile?.businessName ?: "Vyapar Business",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                            if (!businessProfile?.address.isNullOrBlank()) {
                                Text(text = businessProfile?.address ?: "", fontSize = 11.sp, color = SlateMuted)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                if (!businessProfile?.phone.isNullOrBlank()) {
                                    Text(text = "Phone: ${businessProfile?.phone}", fontSize = 11.sp, color = SlateMuted)
                                }
                                if (!businessProfile?.gstin.isNullOrBlank()) {
                                    Text(text = "GSTIN: ${businessProfile?.gstin}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SlateBorder)

                            // Invoice Banner
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldLight
                                ) {
                                    Text(
                                        text = titleLabel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (transaction.status) {
                                        "PAID" -> IncomeGreenLight
                                        "PARTIAL" -> PendingAmberLight
                                        else -> ExpenseRedLight
                                    }
                                ) {
                                    Text(
                                        text = "STATUS: ${transaction.status}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (transaction.status) {
                                            "PAID" -> IncomeGreen
                                            "PARTIAL" -> PendingAmber
                                            else -> ExpenseRed
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Invoice Meta & Billed To in 2 Columns
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "BILLED TO:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                    Text(text = transaction.partyName.ifBlank { "Cash Customer" }, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                    if (transaction.partyPhone.isNotBlank()) {
                                        Text(text = "Mob: ${transaction.partyPhone}", fontSize = 11.sp, color = SlateMuted)
                                    }
                                    if (transaction.partyGstin.isNotBlank()) {
                                        Text(text = "GSTIN: ${transaction.partyGstin}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                    Text(text = "INVOICE DETAILS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                    Text(text = "No: ${transaction.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SlateDark)
                                    Text(text = "Date: ${dateFormatter.format(Date(transaction.date))}", fontSize = 10.sp, color = SlateMuted)
                                    Text(text = "Mode: ${transaction.paymentMode}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SlateBorder)

                            // Items Table
                            if (items.isNotEmpty()) {
                                // Table Header
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("#", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted, modifier = Modifier.width(20.dp))
                                        Text("Item Description", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted, modifier = Modifier.weight(1.8f))
                                        Text("Qty", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted, modifier = Modifier.weight(0.8f))
                                        Text("Rate", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted, modifier = Modifier.weight(1f))
                                        Text("Tax", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted, modifier = Modifier.weight(0.8f))
                                        Text("Total", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                items.forEachIndexed { i, item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${i + 1}", fontSize = 11.sp, color = SlateMuted, modifier = Modifier.width(20.dp))
                                        Column(modifier = Modifier.weight(1.8f)) {
                                            Text(item.itemName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                                            if (item.hsn.isNotBlank()) {
                                                Text("HSN: ${item.hsn}", fontSize = 9.sp, color = SlateMuted)
                                            }
                                        }
                                        Text("${item.quantity.toInt()} ${item.unit}", fontSize = 11.sp, color = SlateDark, modifier = Modifier.weight(0.8f))
                                        Text(formatCurrency(item.unitPrice), fontSize = 11.sp, color = SlateDark, modifier = Modifier.weight(1f))
                                        Text("${item.taxRate.toInt()}%", fontSize = 11.sp, color = SlateMuted, modifier = Modifier.weight(0.8f))
                                        Text(formatCurrency(item.totalAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                    }
                                    if (i < items.size - 1) {
                                        HorizontalDivider(color = Color(0xFFF8FAFC))
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SlateBorder)
                            }

                            // Calculations Breakdown
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Subtotal (Taxable):", fontSize = 12.sp, color = SlateMuted)
                                    Text(formatCurrency(transaction.subtotal), fontSize = 12.sp, color = SlateDark)
                                }

                                if (transaction.taxAmount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("CGST (Central Tax 50%):", fontSize = 12.sp, color = SlateMuted)
                                        Text(formatCurrency(transaction.taxAmount / 2.0), fontSize = 12.sp, color = EmeraldPrimary)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("SGST (State Tax 50%):", fontSize = 12.sp, color = SlateMuted)
                                        Text(formatCurrency(transaction.taxAmount / 2.0), fontSize = 12.sp, color = EmeraldPrimary)
                                    }
                                }

                                if (transaction.discountAmount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Discount Applied:", fontSize = 12.sp, color = SlateMuted)
                                        Text("- " + formatCurrency(transaction.discountAmount), fontSize = 12.sp, color = ExpenseRed)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = SlateBorder)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Grand Total:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                    Text(formatCurrency(transaction.grandTotal), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Amount Paid:", fontSize = 12.sp, color = SlateMuted)
                                    Text(formatCurrency(transaction.paidAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                                }

                                if (transaction.balanceAmount > 0.01) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Balance Due:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                        Text(formatCurrency(transaction.balanceAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                    }
                                }
                            }

                            // Bank & UPI QR Payment Info Box
                            if (!businessProfile?.upiId.isNullOrBlank() || !businessProfile?.accountNumber.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("PAYMENT BANK / UPI DETAILS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                            if (!businessProfile?.upiId.isNullOrBlank()) {
                                                Text("UPI ID: ${businessProfile?.upiId}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                                            }
                                            if (!businessProfile?.bankName.isNullOrBlank()) {
                                                Text("${businessProfile?.bankName} • A/C: ${businessProfile?.accountNumber} • IFSC: ${businessProfile?.ifscCode}", fontSize = 10.sp, color = SlateMuted)
                                            }
                                        }
                                    }
                                }
                            }

                            // Terms & Signature
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text("Terms & Conditions:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                                    Text(
                                        text = businessProfile?.termsAndConditions ?: "Thank you for doing business with us.",
                                        fontSize = 8.sp,
                                        color = SlateMuted,
                                        maxLines = 3
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "For ${businessProfile?.businessName ?: "Company"}", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(text = "Authorized Signatory", fontSize = 9.sp, color = SlateMuted)
                                }
                            }
                        }
                    }
                }

                // Bottom Action Buttons
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onDelete(transaction) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val billText = buildShareText(transaction, businessProfile, items)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, billText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Tax Invoice"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(2f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share via WhatsApp / SMS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

fun buildShareText(
    txn: TransactionEntity,
    profile: BusinessProfileEntity?,
    items: List<com.example.data.model.InvoiceItem>
): String {
    val sb = StringBuilder()
    sb.append("🧾 *${profile?.businessName ?: "Vyapar"}*\n")
    if (!profile?.gstin.isNullOrBlank()) sb.append("GSTIN: ${profile?.gstin}\n")
    sb.append("----------------------------\n")
    sb.append("Invoice: *${txn.invoiceNumber}*\n")
    sb.append("Customer: *${txn.partyName}*\n")
    sb.append("----------------------------\n")
    items.forEachIndexed { i, item ->
        sb.append("${i + 1}. ${item.itemName} (${item.quantity.toInt()} ${item.unit}) = ${formatCurrency(item.totalAmount)}\n")
    }
    sb.append("----------------------------\n")
    sb.append("Grand Total: *${formatCurrency(txn.grandTotal)}*\n")
    sb.append("Paid Amount: *${formatCurrency(txn.paidAmount)}*\n")
    if (txn.balanceAmount > 0.01) {
        sb.append("Balance Due: *${formatCurrency(txn.balanceAmount)}*\n")
    }
    if (!profile?.upiId.isNullOrBlank()) {
        sb.append("\nPay via UPI: *${profile?.upiId}*\n")
    }
    sb.append("\nThank you for your business!")
    return sb.toString()
}
