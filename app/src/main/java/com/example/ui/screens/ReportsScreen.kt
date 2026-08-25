package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.ItemEntity
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
import com.example.ui.theme.VibrantBorderSubtle
import com.example.ui.theme.VibrantCanvas
import com.example.ui.theme.VibrantMint
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantOnBlueContainer
import com.example.ui.theme.VibrantOnPurpleContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.viewmodel.DashboardMetrics

@Composable
fun ReportsScreen(
    metrics: DashboardMetrics,
    transactions: List<TransactionEntity>,
    parties: List<PartyEntity>,
    items: List<ItemEntity>,
    businessProfile: BusinessProfileEntity?,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedReportTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Profit & Loss", "GST GSTR-1", "Daybook", "Stock Summary")

    val totalSales = metrics.totalSales
    val totalPurchases = metrics.totalPurchases
    val totalExpenses = metrics.totalExpenses
    val grossProfit = totalSales - totalPurchases
    val netProfit = grossProfit - totalExpenses

    // GST calculations
    val salesTransactions = transactions.filter { it.type == "SALE" }
    val purchaseTransactions = transactions.filter { it.type == "PURCHASE" }
    val totalGstOutput = salesTransactions.sumOf { it.taxAmount }
    val totalGstInput = purchaseTransactions.sumOf { it.taxAmount }
    val netGstPayable = (totalGstOutput - totalGstInput).coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantCanvas),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Tab Selector Header
        item {
            Surface(
                color = VibrantSurface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedReportTab,
                    containerColor = VibrantSurface,
                    contentColor = VibrantPurple,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedReportTab]),
                            color = VibrantPurple,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedReportTab == index,
                            onClick = { selectedReportTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedReportTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selectedReportTab == index) VibrantPurple else TextSlate500
                                )
                            }
                        )
                    }
                }
            }
        }

        // Report 1: Profit & Loss Statement
        if (selectedReportTab == 0) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Net Profit Card in Vibrant Purple
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (netProfit >= 0) VibrantPurpleContainer else Color(0xFFFFD8E4)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (netProfit >= 0) VibrantPurple.copy(alpha = 0.2f) else ExpenseRed.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "NET BUSINESS PROFIT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (netProfit >= 0) VibrantOnPurpleContainer.copy(alpha = 0.7f) else Color(0xFF904A5D),
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                ) {
                                    Icon(
                                        imageVector = if (netProfit >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = if (netProfit >= 0) VibrantPurple else ExpenseRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = formatCurrency(netProfit),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (netProfit >= 0) VibrantOnPurpleContainer else Color(0xFF31111D)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (netProfit >= 0) "Your business is in healthy profit" else "Expenses exceeding revenue",
                                fontSize = 12.sp,
                                color = if (netProfit >= 0) VibrantOnPurpleContainer.copy(alpha = 0.8f) else Color(0xFF6B1D2F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // P&L Breakdown Table
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("P&L STATEMENT BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSlate400)
                            Spacer(modifier = Modifier.height(12.dp))

                            ReportRow(title = "1. Gross Sales Revenue (+)", amount = totalSales, color = IncomeGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            ReportRow(title = "2. Cost of Purchases (-)", amount = totalPurchases, color = TextSlate900)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = VibrantBorderSubtle)

                            ReportRow(title = "Gross Profit (1 - 2)", amount = grossProfit, color = if (grossProfit >= 0) IncomeGreen else ExpenseRed, isBold = true)
                            Spacer(modifier = Modifier.height(8.dp))

                            ReportRow(title = "3. Operating Expenses (-)", amount = totalExpenses, color = ExpenseRed)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = VibrantBorderSubtle)

                            ReportRow(title = "Net Profit / (Loss)", amount = netProfit, color = if (netProfit >= 0) IncomeGreen else ExpenseRed, isBold = true, fontSize = 15)
                        }
                    }
                }
            }
        }

        // Report 2: GST GSTR-1 Summary
        if (selectedReportTab == 1) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("GSTR-1 & 3B TAX SUMMARY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSlate400)
                                if (!businessProfile?.gstin.isNullOrBlank()) {
                                    Text(businessProfile?.gstin ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantPurple)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            ReportRow(title = "Outward Taxable Sales (Turnover)", amount = totalSales, color = TextSlate900, isBold = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            ReportRow(title = "• Total Output GST Collected", amount = totalGstOutput, color = VibrantPurple)
                            ReportRow(title = "  - CGST (Central 50%)", amount = totalGstOutput / 2.0, color = TextSlate400)
                            ReportRow(title = "  - SGST (State 50%)", amount = totalGstOutput / 2.0, color = TextSlate400)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = VibrantBorderSubtle)

                            ReportRow(title = "Inward Taxable Purchases", amount = totalPurchases, color = TextSlate900, isBold = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            ReportRow(title = "• Input Tax Credit (ITC Available)", amount = totalGstInput, color = VibrantBlue)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = VibrantBorderSubtle)

                            ReportRow(title = "Net GST Tax Payable to Govt", amount = netGstPayable, color = if (netGstPayable > 0) ExpenseRed else IncomeGreen, isBold = true, fontSize = 15)
                        }
                    }
                }
            }
        }

        // Report 3: Daybook (All Transactions)
        if (selectedReportTab == 2) {
            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "No daybook entries recorded.",
                        modifier = Modifier.padding(24.dp),
                        color = TextSlate400
                    )
                }
            } else {
                items(transactions) { txn ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
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

        // Report 4: Stock Summary
        if (selectedReportTab == 3) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("STOCK VALUATION & STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSlate400)
                            Spacer(modifier = Modifier.height(10.dp))
                            ReportRow(title = "Total Catalog Products", amount = items.size.toDouble(), color = TextSlate900, isBold = true, isCurrency = false)
                            Spacer(modifier = Modifier.height(6.dp))
                            ReportRow(title = "Total Inventory Valuation", amount = metrics.totalStockValue, color = VibrantMint, isBold = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            ReportRow(title = "Low Stock SKU Count", amount = metrics.lowStockCount.toDouble(), color = if (metrics.lowStockCount > 0) ExpenseRed else IncomeGreen, isBold = true, isCurrency = false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportRow(
    title: String,
    amount: Double,
    color: Color = TextSlate900,
    isBold: Boolean = false,
    fontSize: Int = 13,
    isCurrency: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = fontSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) TextSlate900 else TextSlate700
        )
        Text(
            text = if (isCurrency) formatCurrency(amount) else amount.toInt().toString(),
            fontSize = fontSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = color
        )
    }
}
