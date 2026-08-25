package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ItemEntity
import com.example.data.local.entity.PartyEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.repository.VyaparRepository
import com.example.ui.components.VyaparBottomNav
import com.example.ui.components.VyaparTopBar
import com.example.ui.screens.AddEditItemDialog
import com.example.ui.screens.AddEditPartyDialog
import com.example.ui.screens.CreateInvoiceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InvoiceDetailDialog
import com.example.ui.screens.ItemsScreen
import com.example.ui.screens.PartiesScreen
import com.example.ui.screens.PartyDetailDialog
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StockAdjustDialog
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.VyaparTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.VyaparViewModel
import com.example.ui.viewmodel.VyaparViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = VyaparRepository(database)
        val factory = VyaparViewModelFactory(repository)

        setContent {
            val viewModel: VyaparViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
            VyaparTheme {
                VyaparApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VyaparApp(viewModel: VyaparViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val businessProfile by viewModel.businessProfile.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val transactions by viewModel.filteredTransactions.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val parties by viewModel.filteredParties.collectAsState()
    val items by viewModel.filteredItems.collectAsState()

    val partySearchQuery by viewModel.partySearchQuery.collectAsState()
    val partyTypeFilter by viewModel.partyTypeFilter.collectAsState()
    val itemSearchQuery by viewModel.itemSearchQuery.collectAsState()
    val itemCategoryFilter by viewModel.itemCategoryFilter.collectAsState()
    val transactionTypeFilter by viewModel.transactionTypeFilter.collectAsState()

    // Dialog States
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }
    var createInvoiceInitialType by remember { mutableStateOf("SALE") }

    var selectedPartyForDetail by remember { mutableStateOf<PartyEntity?>(null) }
    var partyToEdit by remember { mutableStateOf<PartyEntity?>(null) }
    var showAddPartyDialog by remember { mutableStateOf(false) }
    var addPartyDefaultType by remember { mutableStateOf("CUSTOMER") }

    var itemToEdit by remember { mutableStateOf<ItemEntity?>(null) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var itemToAdjustStock by remember { mutableStateOf<ItemEntity?>(null) }

    var selectedTransactionForDetail by remember { mutableStateOf<TransactionEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            VyaparTopBar(
                businessProfile = businessProfile,
                lowStockCount = metrics.lowStockCount,
                onSettingsClick = { viewModel.selectTab(AppTab.SETTINGS) },
                onLowStockClick = { viewModel.selectTab(AppTab.ITEMS) }
            )
        },
        bottomBar = {
            VyaparBottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.DASHBOARD -> {
                    DashboardScreen(
                        metrics = metrics,
                        recentTransactions = allTransactions,
                        onOpenCreateInvoice = { type ->
                            createInvoiceInitialType = type
                            showCreateInvoiceDialog = true
                        },
                        onOpenAddParty = {
                            addPartyDefaultType = "CUSTOMER"
                            showAddPartyDialog = true
                        },
                        onOpenAddItem = {
                            showAddItemDialog = true
                        },
                        onTransactionClick = { txn ->
                            selectedTransactionForDetail = txn
                        },
                        onNavigateTab = { tab ->
                            viewModel.selectTab(tab)
                        }
                    )
                }

                AppTab.PARTIES -> {
                    PartiesScreen(
                        parties = parties,
                        searchQuery = partySearchQuery,
                        typeFilter = partyTypeFilter,
                        totalReceivables = metrics.totalReceivables,
                        totalPayables = metrics.totalPayables,
                        onSearchChange = { viewModel.setPartySearchQuery(it) },
                        onTypeFilterChange = { viewModel.setPartyTypeFilter(it) },
                        onPartyClick = { party ->
                            selectedPartyForDetail = party
                        },
                        onAddPartyClick = {
                            addPartyDefaultType = if (partyTypeFilter == "SUPPLIER") "SUPPLIER" else "CUSTOMER"
                            showAddPartyDialog = true
                        },
                        onRecordPaymentClick = { party ->
                            createInvoiceInitialType = if (party.type == "CUSTOMER") "PAYMENT_IN" else "PAYMENT_OUT"
                            showCreateInvoiceDialog = true
                        }
                    )
                }

                AppTab.ITEMS -> {
                    ItemsScreen(
                        items = items,
                        searchQuery = itemSearchQuery,
                        categoryFilter = itemCategoryFilter,
                        totalStockValue = metrics.totalStockValue,
                        lowStockCount = metrics.lowStockCount,
                        onSearchChange = { viewModel.setItemSearchQuery(it) },
                        onCategoryFilterChange = { viewModel.setItemCategoryFilter(it) },
                        onItemClick = { item ->
                            itemToEdit = item
                        },
                        onAdjustStockClick = { item ->
                            itemToAdjustStock = item
                        },
                        onAddItemClick = {
                            showAddItemDialog = true
                        }
                    )
                }

                AppTab.BILLS -> {
                    TransactionsScreen(
                        transactions = transactions,
                        typeFilter = transactionTypeFilter,
                        totalSales = metrics.totalSales,
                        totalPurchases = metrics.totalPurchases,
                        onTypeFilterChange = { viewModel.setTransactionTypeFilter(it) },
                        onTransactionClick = { txn ->
                            selectedTransactionForDetail = txn
                        },
                        onOpenCreateInvoice = {
                            createInvoiceInitialType = "SALE"
                            showCreateInvoiceDialog = true
                        }
                    )
                }

                AppTab.REPORTS -> {
                    ReportsScreen(
                        metrics = metrics,
                        transactions = allTransactions,
                        parties = parties,
                        items = items,
                        businessProfile = businessProfile,
                        onTransactionClick = { txn ->
                            selectedTransactionForDetail = txn
                        }
                    )
                }

                AppTab.SETTINGS -> {
                    SettingsScreen(
                        profile = businessProfile,
                        onSaveProfile = { updated ->
                            viewModel.updateBusinessProfile(updated)
                            scope.launch {
                                snackbarHostState.showSnackbar("Business profile updated successfully")
                            }
                        }
                    )
                }
            }
        }
    }

    // --- DIALOG MODALS ---

    // 1. Create Invoice / Voucher Modal
    if (showCreateInvoiceDialog) {
        CreateInvoiceScreen(
            initialType = createInvoiceInitialType,
            parties = parties,
            inventoryItems = items,
            onDismiss = { showCreateInvoiceDialog = false },
            onSaveTransaction = { type, partyId, partyName, partyPhone, partyGstin, date, dueDate, invoiceItems, discountAmount, paidAmount, paymentMode, expenseCategory, notes ->
                viewModel.createTransaction(
                    type = type,
                    partyId = partyId,
                    partyName = partyName,
                    partyPhone = partyPhone,
                    partyGstin = partyGstin,
                    date = date,
                    dueDate = dueDate,
                    items = invoiceItems,
                    discountAmount = discountAmount,
                    paidAmount = paidAmount,
                    paymentMode = paymentMode,
                    expenseCategory = expenseCategory,
                    notes = notes
                )
                showCreateInvoiceDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Transaction recorded successfully")
                }
            }
        )
    }

    // 2. Party Detail Ledger Dialog
    selectedPartyForDetail?.let { party ->
        PartyDetailDialog(
            party = party,
            transactions = allTransactions,
            onDismiss = { selectedPartyForDetail = null },
            onEditParty = {
                partyToEdit = party
                selectedPartyForDetail = null
            },
            onRecordPayment = {
                createInvoiceInitialType = if (party.type == "CUSTOMER") "PAYMENT_IN" else "PAYMENT_OUT"
                showCreateInvoiceDialog = true
                selectedPartyForDetail = null
            },
            onCreateInvoice = {
                createInvoiceInitialType = if (party.type == "CUSTOMER") "SALE" else "PURCHASE"
                showCreateInvoiceDialog = true
                selectedPartyForDetail = null
            },
            onTransactionClick = { txn ->
                selectedTransactionForDetail = txn
            }
        )
    }

    // 3. Add Party Dialog
    if (showAddPartyDialog) {
        AddEditPartyDialog(
            initialParty = null,
            defaultType = addPartyDefaultType,
            onDismiss = { showAddPartyDialog = false },
            onSave = { newParty ->
                viewModel.saveParty(newParty)
                showAddPartyDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Party '${newParty.name}' added successfully")
                }
            }
        )
    }

    // 4. Edit Party Dialog
    partyToEdit?.let { party ->
        AddEditPartyDialog(
            initialParty = party,
            onDismiss = { partyToEdit = null },
            onSave = { updated ->
                viewModel.saveParty(updated)
                partyToEdit = null
                scope.launch {
                    snackbarHostState.showSnackbar("Party updated successfully")
                }
            },
            onDelete = { toDelete ->
                viewModel.deleteParty(toDelete)
                partyToEdit = null
                scope.launch {
                    snackbarHostState.showSnackbar("Party deleted")
                }
            }
        )
    }

    // 5. Add Item Dialog
    if (showAddItemDialog) {
        AddEditItemDialog(
            initialItem = null,
            onDismiss = { showAddItemDialog = false },
            onSave = { newItem ->
                viewModel.saveItem(newItem)
                showAddItemDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Item '${newItem.name}' added to inventory")
                }
            }
        )
    }

    // 6. Edit Item Dialog
    itemToEdit?.let { item ->
        AddEditItemDialog(
            initialItem = item,
            onDismiss = { itemToEdit = null },
            onSave = { updated ->
                viewModel.saveItem(updated)
                itemToEdit = null
                scope.launch {
                    snackbarHostState.showSnackbar("Item updated successfully")
                }
            },
            onDelete = { toDelete ->
                viewModel.deleteItem(toDelete)
                itemToEdit = null
                scope.launch {
                    snackbarHostState.showSnackbar("Item deleted from inventory")
                }
            }
        )
    }

    // 7. Adjust Stock Quick Dialog
    itemToAdjustStock?.let { item ->
        StockAdjustDialog(
            item = item,
            onDismiss = { itemToAdjustStock = null },
            onConfirmAdjust = { delta ->
                viewModel.adjustStock(item.id, delta)
                itemToAdjustStock = null
                scope.launch {
                    snackbarHostState.showSnackbar("Stock updated for '${item.name}'")
                }
            }
        )
    }

    // 8. Tax Invoice / Voucher Detail Dialog
    selectedTransactionForDetail?.let { txn ->
        InvoiceDetailDialog(
            transaction = txn,
            businessProfile = businessProfile,
            onDismiss = { selectedTransactionForDetail = null },
            onDelete = { toDelete ->
                viewModel.deleteTransaction(toDelete)
                selectedTransactionForDetail = null
                scope.launch {
                    snackbarHostState.showSnackbar("Invoice deleted")
                }
            }
        )
    }
}
