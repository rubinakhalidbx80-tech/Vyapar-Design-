package com.example.data.model

data class InvoiceItem(
    val itemId: Long = 0,
    val itemName: String,
    val hsn: String = "",
    val quantity: Double = 1.0,
    val unit: String = "PCS",
    val unitPrice: Double = 0.0,
    val taxRate: Double = 0.0, // e.g. 0.0, 5.0, 12.0, 18.0, 28.0
    val discountPercent: Double = 0.0
) {
    val totalWithoutTax: Double
        get() {
            val base = quantity * unitPrice
            val discount = base * (discountPercent / 100.0)
            return (base - discount).coerceAtLeast(0.0)
        }

    val taxAmount: Double
        get() = totalWithoutTax * (taxRate / 100.0)

    val totalAmount: Double
        get() = totalWithoutTax + taxAmount
}
