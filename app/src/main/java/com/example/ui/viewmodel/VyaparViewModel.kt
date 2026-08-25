package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.ItemEntity
import com.example.data.local.entity.PartyEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.InvoiceItem
import com.example.data.repository.VyaparRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    DASHBOARD,
    PARTIES,
    ITEMS,
    BILLS,
    REPORTS,
    SETTINGS
}

data class DashboardMetrics(
    val totalSales: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalReceivables: Double = 0.0,
    val totalPayables: Double = 0.0,
    val totalStockValue: Double = 0.0,
    val lowStockCount: Int = 0,
    val netCashflow: Double = 0.0
)

class VyaparViewModel(
    private val repository: VyaparRepository
) : ViewModel() {

    // Current Active Tab
    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Search and Filters
    private val _partySearchQuery = MutableStateFlow("")
    val partySearchQuery: StateFlow<String> = _partySearchQuery.asStateFlow()

    private val _partyTypeFilter = MutableStateFlow("ALL") // "ALL", "CUSTOMER", "SUPPLIER"
    val partyTypeFilter: StateFlow<String> = _partyTypeFilter.asStateFlow()

    private val _itemSearchQuery = MutableStateFlow("")
    val itemSearchQuery: StateFlow<String> = _itemSearchQuery.asStateFlow()

    private val _itemCategoryFilter = MutableStateFlow("ALL")
    val itemCategoryFilter: StateFlow<String> = _itemCategoryFilter.asStateFlow()

    private val _transactionTypeFilter = MutableStateFlow("ALL") // "ALL", "SALE", "PURCHASE", "EXPENSE", "PAYMENT_IN", "PAYMENT_OUT"
    val transactionTypeFilter: StateFlow<String> = _transactionTypeFilter.asStateFlow()

    // Dialog & Navigation Selection States
    val selectedTransaction = MutableStateFlow<TransactionEntity?>(null)
    val selectedParty = MutableStateFlow<PartyEntity?>(null)
    val selectedItem = MutableStateFlow<ItemEntity?>(null)

    val isCreatingInvoice = MutableStateFlow(false)
    val createInvoiceType = MutableStateFlow("SALE")

    val isAddingParty = MutableStateFlow(false)
    val editingParty = MutableStateFlow<PartyEntity?>(null)

    val isAddingItem = MutableStateFlow(false)
    val editingItem = MutableStateFlow<ItemEntity?>(null)

    val isAdjustingStock = MutableStateFlow(false)
    val stockAdjustItem = MutableStateFlow<ItemEntity?>(null)

    val isEditingProfile = MutableStateFlow(false)

    // Data Streams from Repository
    val allParties = repository.allParties.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allItems = repository.allItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val lowStockItems = repository.lowStockItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTransactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val businessProfile = repository.businessProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered Parties
    val filteredParties = combine(allParties, _partySearchQuery, _partyTypeFilter) { parties, query, type ->
        parties.filter { party ->
            val matchesType = when (type) {
                "CUSTOMER" -> party.type == "CUSTOMER"
                "SUPPLIER" -> party.type == "SUPPLIER"
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    party.name.contains(query, ignoreCase = true) ||
                    party.phone.contains(query, ignoreCase = true) ||
                    party.gstin.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Items
    val filteredItems = combine(allItems, _itemSearchQuery, _itemCategoryFilter) { items, query, category ->
        items.filter { item ->
            val matchesCat = category == "ALL" || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.code.contains(query, ignoreCase = true) ||
                    item.hsn.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Transactions
    val filteredTransactions = combine(allTransactions, _transactionTypeFilter) { transactions, type ->
        if (type == "ALL") transactions else transactions.filter { it.type == type }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metrics Combine
    val dashboardMetrics = combine(
        repository.totalSales,
        repository.totalPurchases,
        repository.totalExpenses,
        repository.totalReceivables,
        repository.totalPayables,
        repository.totalStockValue,
        repository.lowStockItems
    ) { args: Array<Any?> ->
        val s = (args[0] as? Double) ?: 0.0
        val p = (args[1] as? Double) ?: 0.0
        val e = (args[2] as? Double) ?: 0.0
        val receivables = (args[3] as? Double) ?: 0.0
        val payables = (args[4] as? Double) ?: 0.0
        val stockValue = (args[5] as? Double) ?: 0.0
        @Suppress("UNCHECKED_CAST")
        val lowStock = (args[6] as? List<ItemEntity>) ?: emptyList()
        DashboardMetrics(
            totalSales = s,
            totalPurchases = p,
            totalExpenses = e,
            totalReceivables = receivables,
            totalPayables = payables,
            totalStockValue = stockValue,
            lowStockCount = lowStock.size,
            netCashflow = s - p - e
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    // Actions & Convenience Methods
    fun setPartySearchQuery(query: String) { _partySearchQuery.value = query }
    fun setPartyTypeFilter(type: String) { _partyTypeFilter.value = type }
    fun setItemSearchQuery(query: String) { _itemSearchQuery.value = query }
    fun setItemCategoryFilter(category: String) { _itemCategoryFilter.value = category }
    fun setTransactionTypeFilter(type: String) { _transactionTypeFilter.value = type }

    fun openCreateInvoice(type: String = "SALE") {
        createInvoiceType.value = type
        isCreatingInvoice.value = true
    }

    fun closeCreateInvoice() {
        isCreatingInvoice.value = false
    }

    fun createTransaction(
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
        expenseCategory: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.createTransaction(
                type = type,
                partyId = partyId,
                partyName = partyName,
                partyPhone = partyPhone,
                partyGstin = partyGstin,
                date = date,
                dueDate = dueDate,
                items = items,
                discountAmount = discountAmount,
                paidAmount = paidAmount,
                paymentMode = paymentMode,
                expenseCategory = expenseCategory,
                notes = notes
            )
            isCreatingInvoice.value = false
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            if (selectedTransaction.value?.id == transaction.id) {
                selectedTransaction.value = null
            }
        }
    }

    fun saveParty(party: PartyEntity) {
        viewModelScope.launch {
            if (party.id == 0L) {
                repository.insertParty(party)
            } else {
                repository.updateParty(party)
            }
            isAddingParty.value = false
            editingParty.value = null
        }
    }

    fun deleteParty(party: PartyEntity) {
        viewModelScope.launch {
            repository.deleteParty(party)
            if (selectedParty.value?.id == party.id) {
                selectedParty.value = null
            }
        }
    }

    fun saveItem(item: ItemEntity) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.insertItem(item)
            } else {
                repository.updateItem(item)
            }
            isAddingItem.value = false
            editingItem.value = null
        }
    }

    fun deleteItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
            if (selectedItem.value?.id == item.id) {
                selectedItem.value = null
            }
        }
    }

    fun adjustStock(itemId: Long, delta: Double) {
        viewModelScope.launch {
            repository.adjustStock(itemId, delta)
            isAdjustingStock.value = false
            stockAdjustItem.value = null
        }
    }

    fun updateBusinessProfile(profile: BusinessProfileEntity) {
        viewModelScope.launch {
            repository.updateBusinessProfile(profile)
            isEditingProfile.value = false
        }
    }
}
