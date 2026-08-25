package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ItemEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.VibrantAmberContainer
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantCanvas
import com.example.ui.theme.VibrantMint
import com.example.ui.theme.VibrantMintContainer
import com.example.ui.theme.VibrantOnAmberContainer
import com.example.ui.theme.VibrantOnMintContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant

@Composable
fun ItemsScreen(
    items: List<ItemEntity>,
    searchQuery: String,
    categoryFilter: String,
    totalStockValue: Double,
    lowStockCount: Int,
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (String) -> Unit,
    onItemClick: (ItemEntity) -> Unit,
    onAdjustStockClick: (ItemEntity) -> Unit,
    onAddItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("ALL") + items.map { it.category }.distinct().filter { it.isNotBlank() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantCanvas)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Inventory Summary Top Banner in Vibrant Mint & Amber
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
                        // Total Stock Valuation (Mint)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VibrantMintContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantMint.copy(0.15f)),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Total Stock Valuation",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantOnMintContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatCurrency(totalStockValue),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantOnMintContainer
                                )
                            }
                        }

                        // Low Stock Alert Badge (Amber)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (lowStockCount > 0) VibrantAmberContainer else VibrantSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (lowStockCount > 0) Color(0xFFFDE68A) else VibrantBorder
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Low Stock Alert",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (lowStockCount > 0) VibrantOnAmberContainer else TextSlate500
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$lowStockCount Items",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (lowStockCount > 0) Color(0xFF92400E) else TextSlate900
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filter Scrollable Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = categoryFilter == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategoryFilterChange(cat) },
                                label = {
                                    Text(
                                        text = if (cat == "ALL") "All Categories (${items.size})" else cat,
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

            // 2. Search Field
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search by item name, SKU or HSN code...", fontSize = 13.sp, color = TextSlate400) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSlate400)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSlate400)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantPurple,
                        unfocusedBorderColor = VibrantBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("items_search_input")
                )
            }

            // 3. Items List
            if (items.isEmpty()) {
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
                            .background(VibrantMintContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = VibrantMint,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No items found" else "No inventory items added",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add your products with GST rates and stock quantities",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate400
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        ItemCardRow(
                            item = item,
                            onClick = { onItemClick(item) },
                            onAdjustStock = { onAdjustStockClick(item) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Item
        FloatingActionButton(
            onClick = onAddItemClick,
            containerColor = VibrantPurple,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("add_item_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add Item", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ItemCardRow(
    item: ItemEntity,
    onClick: () -> Unit,
    onAdjustStock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = item.isLowStock

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VibrantSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Item details
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextSlate900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isLowStock) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VibrantAmberContainer
                            ) {
                                Text(
                                    text = "LOW STOCK",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (item.code.isNotBlank()) {
                            Text(
                                text = "SKU: ${item.code} • ",
                                fontSize = 11.sp,
                                color = TextSlate400
                            )
                        }
                        if (item.hsn.isNotBlank()) {
                            Text(
                                text = "HSN: ${item.hsn} • ",
                                fontSize = 11.sp,
                                color = TextSlate400
                            )
                        }
                        Text(
                            text = "GST: ${item.taxRate.toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VibrantPurple
                        )
                    }
                }

                // Current Stock
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.stockQuantity.toInt()} ${item.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isLowStock) ExpenseRed else TextSlate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Val: ${formatCurrency(item.stockValue)}",
                        fontSize = 11.sp,
                        color = TextSlate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Stock adjust strip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sale: ",
                        fontSize = 12.sp,
                        color = TextSlate400
                    )
                    Text(
                        text = formatCurrency(item.salePrice),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IncomeGreen
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Purchase: ",
                        fontSize = 12.sp,
                        color = TextSlate400
                    )
                    Text(
                        text = formatCurrency(item.purchasePrice),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate700
                    )
                }

                // Adjust Stock Quick Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = VibrantPurpleContainer,
                    modifier = Modifier.clickable(onClick = onAdjustStock)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            tint = VibrantPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Adjust Stock",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurple
                        )
                    }
                }
            }
        }
    }
}
