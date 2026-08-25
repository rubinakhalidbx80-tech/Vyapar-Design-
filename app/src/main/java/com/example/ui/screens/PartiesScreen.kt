package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PartyEntity
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
import com.example.ui.theme.VibrantOnBlueContainer
import com.example.ui.theme.VibrantOnPinkContainer
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPinkContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant

@Composable
fun PartiesScreen(
    parties: List<PartyEntity>,
    searchQuery: String,
    typeFilter: String,
    totalReceivables: Double,
    totalPayables: Double,
    onSearchChange: (String) -> Unit,
    onTypeFilterChange: (String) -> Unit,
    onPartyClick: (PartyEntity) -> Unit,
    onAddPartyClick: () -> Unit,
    onRecordPaymentClick: (PartyEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantCanvas)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Receivables / Payables Overview in Vibrant Palette
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
                        // Receivables Card
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VibrantBlueContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBlue.copy(0.15f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "To Receive (Customers)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantOnBlueContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatCurrency(totalReceivables),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantOnBlueContainer
                                )
                            }
                        }

                        // Payables Card
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VibrantPinkContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantPink.copy(0.15f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "To Pay (Suppliers)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VibrantOnPinkContainer.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatCurrency(totalPayables),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantOnPinkContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Type Filter Tabs
                    val tabs = listOf("ALL" to "All Parties", "CUSTOMER" to "Customers", "SUPPLIER" to "Suppliers")
                    val selectedIndex = when (typeFilter) {
                        "CUSTOMER" -> 1
                        "SUPPLIER" -> 2
                        else -> 0
                    }

                    TabRow(
                        selectedTabIndex = selectedIndex,
                        containerColor = Color.Transparent,
                        contentColor = VibrantPurple,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                color = VibrantPurple,
                                height = 3.dp
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, (type, label) ->
                            Tab(
                                selected = selectedIndex == index,
                                onClick = { onTypeFilterChange(type) },
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (selectedIndex == index) VibrantPurple else TextSlate500
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // 2. Search Bar
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search by name, phone or GSTIN...", fontSize = 13.sp, color = TextSlate400) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSlate400
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSlate400
                                )
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
                        .testTag("parties_search_input")
                )
            }

            // 3. Parties List
            if (parties.isEmpty()) {
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
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = VibrantPurple,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No matching parties found" else "No parties added yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add customers or suppliers to track sales, purchases & dues",
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
                    items(parties, key = { it.id }) { party ->
                        PartyCardItem(
                            party = party,
                            onClick = { onPartyClick(party) },
                            onCall = {
                                if (party.phone.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${party.phone}"))
                                    context.startActivity(intent)
                                }
                            },
                            onShareReminder = {
                                val message = if (party.type == "CUSTOMER" && party.currentBalance > 0) {
                                    "Dear ${party.name}, gentle reminder regarding outstanding payment of ${formatCurrency(party.currentBalance)} pending towards your account. Please settle at your earliest convenience."
                                } else {
                                    "Hello ${party.name}, current balance with us is ${formatCurrency(party.currentBalance)}."
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Ledger Balance"))
                            },
                            onRecordPayment = { onRecordPaymentClick(party) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Party in VibrantPurple
        FloatingActionButton(
            onClick = onAddPartyClick,
            containerColor = VibrantPurple,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("add_party_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Party")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (typeFilter == "SUPPLIER") "Add Supplier" else "Add Customer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun PartyCardItem(
    party: PartyEntity,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onShareReminder: () -> Unit,
    onRecordPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCustomer = party.type == "CUSTOMER"
    val hasBalance = party.currentBalance > 0.01

    val avatarBg = when ((party.name.hashCode() and 0x7FFFFFFF) % 4) {
        0 -> Color(0xFFEFF6FF)
        1 -> Color(0xFFFAF5FF)
        2 -> Color(0xFFECFDF5)
        else -> Color(0xFFFFFBEB)
    }
    val avatarText = when ((party.name.hashCode() and 0x7FFFFFFF) % 4) {
        0 -> Color(0xFF2563EB)
        1 -> Color(0xFF7C3AED)
        2 -> Color(0xFF059669)
        else -> Color(0xFFD97706)
    }

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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(avatarBg)
                ) {
                    Text(
                        text = party.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = avatarText
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Type badge & phone
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = party.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextSlate900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCustomer) VibrantPurpleContainer else VibrantBlueContainer
                        ) {
                            Text(
                                text = if (isCustomer) "Customer" else "Supplier",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCustomer) VibrantPurple else VibrantBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (party.phone.isNotBlank()) party.phone else if (party.gstin.isNotBlank()) "GST: ${party.gstin}" else "No phone attached",
                        fontSize = 12.sp,
                        color = TextSlate400
                    )
                }

                // Balance
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(party.currentBalance),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = when {
                            !hasBalance -> TextSlate400
                            isCustomer -> IncomeGreen
                            else -> ExpenseRed
                        }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when {
                            !hasBalance -> "Settled (₹0)"
                            isCustomer -> "You'll Receive"
                            else -> "You'll Pay"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            !hasBalance -> TextSlate400
                            isCustomer -> IncomeGreen
                            else -> ExpenseRed
                        }
                    )
                }
            }

            // Action strip
            if (hasBalance || party.phone.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (party.phone.isNotBlank()) {
                        IconButton(
                            onClick = onCall,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Party",
                                tint = VibrantPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    if (hasBalance) {
                        IconButton(
                            onClick = onShareReminder,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Reminder",
                                tint = VibrantPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VibrantPurpleContainer,
                        modifier = Modifier.clickable(onClick = onRecordPayment)
                    ) {
                        Text(
                            text = if (isCustomer) "+ Record Payment" else "+ Pay Supplier",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurple,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
