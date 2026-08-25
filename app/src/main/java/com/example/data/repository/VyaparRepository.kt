package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.InvoiceItemJsonHelper
import com.example.data.local.dao.BusinessProfileDao
import com.example.data.local.dao.ItemDao
import com.example.data.local.dao.PartyDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.ItemEntity
import com.example.data.local.entity.PartyEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.InvoiceItem
import kotlinx.coroutines.flow.Flow

class VyaparRepository(
    private val partyDao: PartyDao,
    private val itemDao: ItemDao,
    private val transactionDao: TransactionDao,
    private val profileDao: BusinessProfileDao
) {
    constructor(database: AppDatabase) : this(
        database.partyDao(),
        database.itemDao(),
        database.transactionDao(),
        database.businessProfileDao()
    )

    // Parties
    val allParties: Flow<List<PartyEntity>> = partyDao.getAllParties()
    fun getPartiesByType(type: String): Flow<List<PartyEntity>> = partyDao.getPartiesByType(type)
    fun getPartyFlow(id: Long): Flow<PartyEntity?> = partyDao.getPartyFlowById(id)
    suspend fun getPartyById(id: Long): PartyEntity? = partyDao.getPartyById(id)
    suspend fun insertParty(party: PartyEntity): Long = partyDao.insertParty(party)
    suspend fun updateParty(party: PartyEntity) = partyDao.updateParty(party)
    suspend fun deleteParty(party: PartyEntity) = partyDao.deleteParty(party)
    val totalReceivables: Flow<Double?> = partyDao.getTotalReceivables()
    val totalPayables: Flow<Double?> = partyDao.getTotalPayables()

    // Items
    val allItems: Flow<List<ItemEntity>> = itemDao.getAllItems()
    val lowStockItems: Flow<List<ItemEntity>> = itemDao.getLowStockItems()
    val totalStockValue: Flow<Double?> = itemDao.getTotalStockValue()
    suspend fun getItemById(id: Long): ItemEntity? = itemDao.getItemById(id)
    suspend fun insertItem(item: ItemEntity): Long = itemDao.insertItem(item)
    suspend fun updateItem(item: ItemEntity) = itemDao.updateItem(item)
    suspend fun deleteItem(item: ItemEntity) = itemDao.deleteItem(item)
    suspend fun adjustStock(itemId: Long, delta: Double) = itemDao.adjustStock(itemId, delta)

    // Transactions
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByType(type)
    fun getTransactionsByParty(partyId: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByParty(partyId)
    fun getTransactionFlow(id: Long): Flow<TransactionEntity?> = transactionDao.getTransactionFlowById(id)
    val totalSales: Flow<Double?> = transactionDao.getTotalSales()
    val totalPurchases: Flow<Double?> = transactionDao.getTotalPurchases()
    val totalExpenses: Flow<Double?> = transactionDao.getTotalExpenses()
    val totalMoneyIn: Flow<Double?> = transactionDao.getTotalMoneyIn()
    val totalMoneyOut: Flow<Double?> = transactionDao.getTotalMoneyOut()

    // Business Profile
    val businessProfile: Flow<BusinessProfileEntity?> = profileDao.getBusinessProfile()
    suspend fun getBusinessProfileDirect(): BusinessProfileEntity? = profileDao.getBusinessProfileDirect()
    suspend fun updateBusinessProfile(profile: BusinessProfileEntity) = profileDao.insertOrUpdateProfile(profile)

    // Process & Save New Transaction
    suspend fun createTransaction(
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
        notes: String = "",
        customInvoiceNumber: String? = null
    ): Long {
        val subtotal = items.sumOf { it.totalWithoutTax }
        val taxAmount = items.sumOf { it.taxAmount }
        val grandTotal = if (type == "EXPENSE") {
            subtotal
        } else {
            (subtotal + taxAmount - discountAmount).coerceAtLeast(0.0)
        }
        val actualPaid = paidAmount.coerceAtMost(grandTotal)
        val balance = (grandTotal - actualPaid).coerceAtLeast(0.0)
        val status = when {
            balance <= 0.01 -> "PAID"
            actualPaid > 0.01 -> "PARTIAL"
            else -> "UNPAID"
        }

        // Generate invoice number if not provided
        val invoiceNo = customInvoiceNumber ?: generateInvoiceNumber(type)

        val transaction = TransactionEntity(
            invoiceNumber = invoiceNo,
            type = type,
            partyId = partyId,
            partyName = partyName.ifBlank { if (type == "SALE") "Cash Customer" else if (type == "PURCHASE") "Cash Supplier" else "General" },
            partyPhone = partyPhone,
            partyGstin = partyGstin,
            date = date,
            dueDate = dueDate,
            subtotal = subtotal,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            grandTotal = grandTotal,
            paidAmount = actualPaid,
            balanceAmount = balance,
            paymentMode = paymentMode,
            status = status,
            expenseCategory = expenseCategory,
            notes = notes,
            itemsJson = InvoiceItemJsonHelper.toJson(items)
        )

        val txId = transactionDao.insertTransaction(transaction)

        // 1. Stock adjustments
        if (type == "SALE") {
            items.forEach { item ->
                if (item.itemId > 0) {
                    itemDao.adjustStock(item.itemId, -item.quantity)
                }
            }
        } else if (type == "PURCHASE") {
            items.forEach { item ->
                if (item.itemId > 0) {
                    itemDao.adjustStock(item.itemId, item.quantity)
                }
            }
        }

        // 2. Party balance adjustments
        if (partyId != null && partyId > 0) {
            when (type) {
                "SALE" -> {
                    if (balance > 0) {
                        partyDao.updatePartyBalance(partyId, balance)
                    }
                }
                "PURCHASE" -> {
                    if (balance > 0) {
                        partyDao.updatePartyBalance(partyId, balance)
                    }
                }
                "PAYMENT_IN" -> {
                    partyDao.updatePartyBalance(partyId, -actualPaid)
                }
                "PAYMENT_OUT" -> {
                    partyDao.updatePartyBalance(partyId, -actualPaid)
                }
            }
        }

        return txId
    }

    suspend fun generateInvoiceNumber(type: String): String {
        val count = transactionDao.countTransactionsByType(type) + 1001
        val prefix = when (type) {
            "SALE" -> "INV-"
            "PURCHASE" -> "PUR-"
            "EXPENSE" -> "EXP-"
            "PAYMENT_IN" -> "REC-"
            "PAYMENT_OUT" -> "PAY-"
            "ESTIMATE" -> "EST-"
            else -> "TXN-"
        }
        return "$prefix$count"
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        // Revert stock adjustments
        val items = InvoiceItemJsonHelper.fromJson(transaction.itemsJson)
        if (transaction.type == "SALE") {
            items.forEach { item ->
                if (item.itemId > 0) {
                    itemDao.adjustStock(item.itemId, item.quantity)
                }
            }
            if (transaction.partyId != null && transaction.balanceAmount > 0) {
                partyDao.updatePartyBalance(transaction.partyId, -transaction.balanceAmount)
            }
        } else if (transaction.type == "PURCHASE") {
            items.forEach { item ->
                if (item.itemId > 0) {
                    itemDao.adjustStock(item.itemId, -item.quantity)
                }
            }
            if (transaction.partyId != null && transaction.balanceAmount > 0) {
                partyDao.updatePartyBalance(transaction.partyId, -transaction.balanceAmount)
            }
        } else if (transaction.type == "PAYMENT_IN" && transaction.partyId != null) {
            partyDao.updatePartyBalance(transaction.partyId, transaction.paidAmount)
        } else if (transaction.type == "PAYMENT_OUT" && transaction.partyId != null) {
            partyDao.updatePartyBalance(transaction.partyId, transaction.paidAmount)
        }

        transactionDao.deleteTransaction(transaction)
    }
}
