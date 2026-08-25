package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantCanvas
import com.example.ui.theme.VibrantOnBlueContainer
import com.example.ui.theme.VibrantOnPurpleContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    typeFilter: String,
    totalSales: Double,
    totalPurchases: Double,
    onTypeFilterChange: (String) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onOpenCreateInvoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        "ALL" to "All Vouchers",
        "SALE" to "Sales (Invoices)",
        "PURCHASE" to "Purchases",
        "EXPENSE" to "Expenses",
        "PAYMENT_IN" to "Payments In",
        "PAYMENT_OUT" to "Payments Out",
        "ESTIMATE" to "Estimates"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantCanvas)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Summary Bar in Vibrant Palette
            Surface(
                color = VibrantSurface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Sales (Purple)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VibrantPurpleContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantPurple.copy(0.15f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Total Sales",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantOnPurpleContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatCurrency(totalSales),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantOnPurpleContainer
                                )
                            }
                        }

                        // Total Purchases (Blue)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VibrantBlueContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBlue.copy(0.15f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Total Purchases",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantOnBlueContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatCurrency(totalPurchases),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantOnBlueContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { (type, label) ->
                            val isSelected = typeFilter == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { onTypeFilterChange(type) },
                                label = {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VibrantPurple,
                                    selectedLabelColor = Color.White,
                                    containerColor = VibrantSurfaceVariant,
                                    labelColor = TextSlate700
                                ),
                                border = null
                            )
                        }
                    }
                }
            }

            // List of Transactions
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(VibrantPurpleContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = VibrantPurple,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No invoices or bills found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap + Create Bill below to record a sale or purchase",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate400
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(transactions, key = { it.id }) { txn ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
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

        // Floating Action Button to Create Invoice
        FloatingActionButton(
            onClick = onOpenCreateInvoice,
            containerColor = VibrantPurple,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("create_invoice_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Invoice")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "+ Create Bill", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
