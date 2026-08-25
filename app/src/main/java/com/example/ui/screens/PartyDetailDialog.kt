package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.PartyEntity
import com.example.data.local.entity.TransactionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantCanvas
import com.example.ui.theme.VibrantMint
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantOnBlueContainer
import com.example.ui.theme.VibrantOnPurpleContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant

@Composable
fun PartyDetailDialog(
    party: PartyEntity,
    transactions: List<TransactionEntity>,
    onDismiss: () -> Unit,
    onEditParty: () -> Unit,
    onRecordPayment: () -> Unit,
    onCreateInvoice: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit
) {
    val context = LocalContext.current
    val isCustomer = party.type == "CUSTOMER"
    val partyTransactions = transactions.filter { it.partyId == party.id }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = VibrantCanvas),
            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Surface(
                    color = VibrantSurface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (isCustomer) VibrantPurpleContainer else VibrantBlueContainer)
                                ) {
                                    Text(
                                        text = party.name.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = if (isCustomer) VibrantPurple else VibrantBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = party.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSlate900
                                    )
                                    Text(
                                        text = if (isCustomer) "Customer Ledger" else "Supplier Ledger",
                                        fontSize = 12.sp,
                                        color = TextSlate400
                                    )
                                }
                            }

                            Row {
                                IconButton(onClick = onEditParty) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSlate400)
                                }
                                IconButton(onClick = onDismiss) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSlate400)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Current Balance Strip
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (party.currentBalance > 0.01) {
                                if (isCustomer) VibrantMintContainer else Color(0xFFFFD8E4)
                            } else VibrantSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (party.currentBalance > 0.01) {
                                             if (isCustomer) "NET RECEIVABLE (YOU'LL GET)" else "NET PAYABLE (YOU'LL GIVE)"
                                        } else "ACCOUNT SETTLED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSlate500
                                    )
                                    Text(
                                        text = formatCurrency(party.currentBalance),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (party.currentBalance > 0.01) {
                                            if (isCustomer) IncomeGreen else ExpenseRed
                                        } else TextSlate900
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (party.phone.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${party.phone}"))
                                                context.startActivity(intent)
                                            },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(Color.White, CircleShape)
                                        ) {
                                            Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = VibrantPurple, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            val message = "Dear ${party.name},\nYour ledger balance with us is ${formatCurrency(party.currentBalance)}.\nTotal Transactions: ${partyTransactions.size}.\nThank you for doing business!"
                                            val shareIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, message)
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Ledger Statement"))
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(Color.White, CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = VibrantPurple, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        // Details text: GSTIN & Address
                        if (party.gstin.isNotBlank() || party.billingAddress.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (party.gstin.isNotBlank()) {
                                Text(
                                    text = "GSTIN: ${party.gstin}",
                                    fontSize = 11.sp,
                                    color = TextSlate400
                                )
                            }
                            if (party.billingAddress.isNotBlank()) {
                                Text(
                                    text = "Address: ${party.billingAddress}",
                                    fontSize = 11.sp,
                                    color = TextSlate400
                                )
                            }
                        }
                    }
                }

                // Ledger Transaction History
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "TRANSACTION LEDGER (${partyTransactions.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate400,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (partyTransactions.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "No transactions recorded for this party yet.",
                                fontSize = 13.sp,
                                color = TextSlate400
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(partyTransactions, key = { it.id }) { txn ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                                        VibrantTransactionRow(
                                            txn = txn,
                                            onClick = { onTransactionClick(txn) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Action Buttons
                Surface(
                    color = VibrantSurface,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRecordPayment,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantPurple),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isCustomer) "Record Pay In" else "Record Pay Out", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onCreateInvoice,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isCustomer) "+ Create Sale" else "+ Add Purchase", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
