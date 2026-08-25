package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parties")
data class PartyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val gstin: String = "",
    val type: String = "CUSTOMER", // "CUSTOMER" or "SUPPLIER"
    val billingAddress: String = "",
    val openingBalance: Double = 0.0,
    val currentBalance: Double = 0.0, // Positive: You'll Receive (Customer owes you) / You'll Give (You owe Supplier)
    val creditLimit: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
