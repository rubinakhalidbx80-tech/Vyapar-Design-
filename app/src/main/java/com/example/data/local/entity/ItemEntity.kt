package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String = "", // SKU / Barcode
    val category: String = "General",
    val hsn: String = "",
    val unit: String = "PCS", // PCS, KG, BOX, MTR, LTR, PKT
    val salePrice: Double = 0.0,
    val purchasePrice: Double = 0.0,
    val taxRate: Double = 0.0, // 0%, 5%, 12%, 18%, 28%
    val stockQuantity: Double = 0.0,
    val minStockAlert: Double = 5.0,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val stockValue: Double
        get() = stockQuantity * purchasePrice

    val isLowStock: Boolean
        get() = stockQuantity <= minStockAlert
}
