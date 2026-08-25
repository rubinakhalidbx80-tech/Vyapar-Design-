package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String, // e.g. "INV-1001", "PUR-2001", "EXP-3001"
    val type: String, // "SALE", "PURCHASE", "EXPENSE", "PAYMENT_IN", "PAYMENT_OUT", "ESTIMATE"
    val partyId: Long? = null,
    val partyName: String = "",
    val partyPhone: String = "",
    val partyGstin: String = "",
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // Default 7 days
    val subtotal: Double = 0.0,
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val paidAmount: Double = 0.0,
    val balanceAmount: Double = 0.0,
    val paymentMode: String = "CASH", // "CASH", "UPI", "BANK_TRANSFER", "CHEQUE", "CREDIT"
    val status: String = "PAID", // "PAID", "UNPAID", "PARTIAL"
    val expenseCategory: String = "", // e.g. "Rent", "Utilities", "Salary", "Logistics", "Marketing", "Others"
    val notes: String = "",
    val terms: String = "",
    val itemsJson: String = "[]" // JSON string of List<InvoiceItem>
)
