package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.VibrantAmber
import com.example.ui.theme.VibrantAmberContainer
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderSubtle
import com.example.ui.theme.VibrantCanvas
import com.example.ui.theme.VibrantMint
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantOnAmberContainer
import com.example.ui.theme.VibrantOnBlueContainer
import com.example.ui.theme.VibrantOnMintContainer
import com.example.ui.theme.VibrantOnPinkContainer
import com.example.ui.theme.VibrantOnPurpleContainer
import com.example.ui.theme.VibrantOnSlateContainer
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantSlateContainer
import com.example.ui.theme.VibrantSurface
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.DashboardMetrics
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    metrics: DashboardMetrics,
    recentTransactions: List<TransactionEntity>,
    onOpenCreateInvoice: (String) -> Unit,
    onOpenAddParty: () -> Unit,
    onOpenAddItem: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantCanvas),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Vibrant 2-Column Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Primary Row: Cash In Hand / Receivables & Total Sales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Blue Card: Cash in Hand / Receivables
                    VibrantMetricCard(
                        title = "Cash / Receivables",
                        amount = formatCurrency(metrics.totalReceivables),
                        icon = Icons.Default.AccountBalanceWallet,
                        containerColor = VibrantBlueContainer,
                        contentColor = VibrantOnBlueContainer,
                        accentColor = VibrantBlue,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("receivables_card")
                            .clickable { onNavigateTab(AppTab.PARTIES) }
                    )

                    // Purple Card: Total Sales Revenue
                    VibrantMetricCard(
                        title = "Total Sales",
                        amount = formatCurrency(metrics.totalSales),
                        icon = Icons.Default.TrendingUp,
                        containerColor = VibrantPurpleContainer,
                        contentColor = VibrantOnPurpleContainer,
                        accentColor = VibrantPurple,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sales_metric_card")
                            .clickable { onNavigateTab(AppTab.BILLS) }
                    )
                }

                // Secondary Row: Payables & Stock Value
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pink Card: Payables ("You'll Give")
                    VibrantMetricCard(
                        title = "Payables (Suppliers)",
                        amount = formatCurrency(metrics.totalPayables),
                        icon = Icons.Default.ArrowUpward,
                        containerColor = VibrantPinkContainer,
                        contentColor = VibrantOnPinkContainer,
                        accentColor = VibrantPink,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("payables_card")
                            .clickable { onNavigateTab(AppTab.PARTIES) }
                    )

                    // Mint Card: Stock Valuation
                    VibrantMetricCard(
                        title = "Stock Valuation",
                        amount = formatCurrency(metrics.totalStockValue),
                        icon = Icons.Default.Inventory2,
                        containerColor = VibrantMintContainer,
                        contentColor = VibrantOnMintContainer,
                        accentColor = VibrantMint,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stock_value_card")
                            .clickable { onNavigateTab(AppTab.ITEMS) }
                    )
                }
            }
        }

        // 2. Low Stock Warning (if any)
        if (metrics.lowStockCount > 0) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateTab(AppTab.ITEMS) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = PendingAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${metrics.lowStockCount} Items Low in Stock",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Tap to review inventory & restock items",
                                color = Color(0xFFB45309),
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = PendingAmber,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 3. Quick Actions - Vibrant Pastel 4-Icon Strip
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate400,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(start = 2.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Action 1: Sale (Pink)
                    VibrantQuickActionButton(
                        icon = Icons.Default.AddShoppingCart,
                        label = "Sale",
                        containerColor = VibrantPinkContainer,
                        contentColor = VibrantOnPinkContainer,
                        onClick = { onOpenCreateInvoice("SALE") },
                        modifier = Modifier.weight(1f)
                    )

                    // Action 2: Stock (Mint)
                    VibrantQuickActionButton(
                        icon = Icons.Default.Inventory2,
                        label = "Stock",
                        containerColor = VibrantMintContainer,
                        contentColor = VibrantOnMintContainer,
                        onClick = onOpenAddItem,
                        modifier = Modifier.weight(1f)
                    )

                    // Action 3: Party (Amber)
                    VibrantQuickActionButton(
                        icon = Icons.Default.Group,
                        label = "Party",
                        containerColor = VibrantAmberContainer,
                        contentColor = VibrantOnAmberContainer,
                        onClick = onOpenAddParty,
                        modifier = Modifier.weight(1f)
                    )

                    // Action 4: Bill / Purchase (Slate)
                    VibrantQuickActionButton(
                        icon = Icons.Default.Description,
                        label = "Bill",
                        containerColor = VibrantSlateContainer,
                        contentColor = VibrantOnSlateContainer,
                        onClick = { onOpenCreateInvoice("PURCHASE") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Recent Transactions Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT TRANSACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate400,
                        letterSpacing = 1.5.sp
                    )
                    TextButton(
                        onClick = { onNavigateTab(AppTab.BILLS) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "VIEW ALL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Card container with rounded-3xl
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (recentTransactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(VibrantPurpleContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = VibrantPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No transactions recorded yet",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSlate700
                            )
                            Text(
                                text = "Tap 'Sale' or 'Bill' above to create your first invoice",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSlate400
                            )
                        }
                    } else {
                        Column(modifier = Modifier.padding(16.dp)) {
                            recentTransactions.take(6).forEachIndexed { index, txn ->
                                VibrantTransactionRow(
                                    txn = txn,
                                    onClick = { onTransactionClick(txn) }
                                )
                                if (index < recentTransactions.take(6).lastIndex) {
                                    HorizontalDivider(
                                        color = VibrantBorderSubtle,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VibrantMetricCard(
    title: String,
    amount: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.15f)),
        modifier = modifier.height(130.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // White circular icon pill
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = amount,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun VibrantQuickActionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(containerColor)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSlate700
        )
    }
}

@Composable
fun VibrantTransactionRow(
    txn: TransactionEntity,
    onClick: () -> Unit
) {
    val isIncome = txn.type == "SALE" || txn.type == "PAYMENT_IN"
    val isExpense = txn.type == "EXPENSE" || txn.type == "PURCHASE" || txn.type == "PAYMENT_OUT"

    val partyInitials = if (txn.partyName.isNotBlank()) {
        val words = txn.partyName.trim().split(" ").filter { it.isNotBlank() }
        if (words.size >= 2) {
            "${words[0].first()}${words[1].first()}".uppercase()
        } else {
            txn.partyName.take(2).uppercase()
        }
    } else {
        "TX"
    }

    // Avatar pastel background based on hash
    val avatarBg = when ((txn.partyName.hashCode() and 0x7FFFFFFF) % 4) {
        0 -> Color(0xFFEFF6FF) // Blue
        1 -> Color(0xFFFAF5FF) // Purple
        2 -> Color(0xFFECFDF5) // Emerald
        else -> Color(0xFFFFFBEB) // Amber
    }
    val avatarText = when ((txn.partyName.hashCode() and 0x7FFFFFFF) % 4) {
        0 -> Color(0xFF2563EB)
        1 -> Color(0xFF7C3AED)
        2 -> Color(0xFF059669)
        else -> Color(0xFFD97706)
    }

    val dateFormatter = SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault())

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Circular Avatar Initials
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarBg)
            ) {
                Text(
                    text = partyInitials,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = avatarText
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = txn.partyName.ifBlank { "Cash Transaction" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateFormatter.format(Date(txn.date)),
                    fontSize = 11.sp,
                    color = TextSlate400
                )
            }
        }

        // Amount
        Text(
            text = "${if (isIncome) "+" else "-"}${formatCurrency(txn.grandTotal)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isIncome) IncomeGreen else TextSlate700
        )
    }
}

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 2
    return "₹" + formatter.format(amount)
}
