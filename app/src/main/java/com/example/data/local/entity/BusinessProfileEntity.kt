package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfileEntity(
    @PrimaryKey
    val id: Int = 1, // Single profile row
    val businessName: String = "My Store & Traders",
    val ownerName: String = "Business Owner",
    val phone: String = "+91 98765 43210",
    val email: String = "store@vyaparbusiness.in",
    val gstin: String = "27AAAAA0000A1Z5",
    val businessType: String = "Retail & Wholesale",
    val address: String = "Shop #4, Trade Complex, Market Road, Mumbai 400001",
    val state: String = "Maharashtra",
    val upiId: String = "mystore@upi",
    val bankName: String = "State Bank of India",
    val accountNumber: String = "987654321012",
    val ifscCode: String = "SBIN0001234",
    val invoicePrefix: String = "INV-",
    val termsAndConditions: String = "1. Goods once sold will not be taken back without original bill.\n2. Subject to local jurisdiction.\n3. Interest @ 18% p.a. charged on delayed payments beyond due date.",
    val currencySymbol: String = "₹"
)
