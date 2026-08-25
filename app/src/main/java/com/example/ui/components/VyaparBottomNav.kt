package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.viewmodel.AppTab

@Composable
fun VyaparBottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = modifier
    ) {
        val items = listOf(
            Triple(AppTab.DASHBOARD, "Dashboard", Icons.Filled.Dashboard to Icons.Outlined.Dashboard),
            Triple(AppTab.PARTIES, "Parties", Icons.Filled.People to Icons.Outlined.People),
            Triple(AppTab.ITEMS, "Items", Icons.Filled.Inventory2 to Icons.Outlined.Inventory2),
            Triple(AppTab.BILLS, "Bills", Icons.Filled.ReceiptLong to Icons.Outlined.ReceiptLong),
            Triple(AppTab.REPORTS, "Reports", Icons.Filled.Assessment to Icons.Outlined.Assessment)
        )

        items.forEach { (tab, label, icons) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VibrantPurple,
                    selectedTextColor = VibrantPurple,
                    indicatorColor = VibrantPurpleContainer,
                    unselectedIconColor = TextSlate400,
                    unselectedTextColor = TextSlate400
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
