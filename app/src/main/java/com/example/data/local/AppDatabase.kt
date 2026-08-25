package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BusinessProfileDao
import com.example.data.local.dao.ItemDao
import com.example.data.local.dao.PartyDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.ItemEntity
import com.example.data.local.entity.PartyEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.InvoiceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PartyEntity::class,
        ItemEntity::class,
        TransactionEntity::class,
        BusinessProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun partyDao(): PartyDao
    abstract fun itemDao(): ItemDao
    abstract fun transactionDao(): TransactionDao
    abstract fun businessProfileDao(): BusinessProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vyapar_business_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val profileDao = db.businessProfileDao()
            val partyDao = db.partyDao()
            val itemDao = db.itemDao()
            val transDao = db.transactionDao()

            // 1. Initial Business Profile
            profileDao.insertOrUpdateProfile(
                BusinessProfileEntity(
                    id = 1,
                    businessName = "Shree Ganesh Enterprises",
                    ownerName = "Rajesh Sharma",
                    phone = "+91 98200 12345",
                    email = "contact@shreeganeshent.in",
                    gstin = "27AABCS1429B1Z8",
                    businessType = "Wholesale & Retail Trading",
                    address = "Plot 14, APMC Market Sector 19, Vashi, Navi Mumbai, MH - 400703",
                    state = "Maharashtra",
                    upiId = "shreeganesh@icici",
                    bankName = "HDFC Bank Ltd",
                    accountNumber = "50200049281729",
                    ifscCode = "HDFC0001234",
                    invoicePrefix = "INV-",
                    termsAndConditions = "1. Goods once sold will not be returned.\n2. Payment strictly due within 15 days.\n3. Subject to Navi Mumbai jurisdiction."
                )
            )

            // 2. Initial Parties (Customers & Suppliers)
            val cust1 = PartyEntity(
                name = "Aakash Supermarket",
                phone = "+91 98190 55443",
                email = "aakashmart@gmail.com",
                gstin = "27AAACA1234A1Z1",
                type = "CUSTOMER",
                billingAddress = "Shop 12, Sunrise Arcade, Thane West, MH",
                openingBalance = 0.0,
                currentBalance = 14500.0, // Customer owes us
                creditLimit = 50000.0
            )
            val cust2 = PartyEntity(
                name = "Ramesh General Stores",
                phone = "+91 98670 11223",
                email = "rameshstore@yahoo.com",
                gstin = "27BBBCA5678B1Z2",
                type = "CUSTOMER",
                billingAddress = "45 Station Road, Dadar East, Mumbai",
                openingBalance = 0.0,
                currentBalance = 6200.0,
                creditLimit = 25000.0
            )
            val cust3 = PartyEntity(
                name = "Pooja Provision Store",
                phone = "+91 97690 99887",
                email = "poojastore@gmail.com",
                gstin = "",
                type = "CUSTOMER",
                billingAddress = "Gala 5, Vegetable Market, Andheri East",
                openingBalance = 0.0,
                currentBalance = 0.0,
                creditLimit = 15000.0
            )
            val supp1 = PartyEntity(
                name = "Hindustan FMCG Distributors",
                phone = "+91 98210 77889",
                email = "sales@hindustanfmcg.com",
                gstin = "27CCCDA9876C1Z3",
                type = "SUPPLIER",
                billingAddress = "Godown B-4, Bhiwandi Logistics Park, MH",
                openingBalance = 0.0,
                currentBalance = 38500.0, // We owe supplier
                creditLimit = 100000.0
            )
            val supp2 = PartyEntity(
                name = "National Packaging & Mills",
                phone = "+91 98330 44556",
                email = "orders@natpack.in",
                gstin = "27EEERA5432E1Z5",
                type = "SUPPLIER",
                billingAddress = "MIDC Phase 2, Rabale, Navi Mumbai",
                openingBalance = 0.0,
                currentBalance = 12400.0,
                creditLimit = 50000.0
            )

            val pCust1Id = partyDao.insertParty(cust1)
            val pCust2Id = partyDao.insertParty(cust2)
            partyDao.insertParty(cust3)
            val pSupp1Id = partyDao.insertParty(supp1)
            partyDao.insertParty(supp2)

            // 3. Initial Inventory Items
            val item1 = ItemEntity(
                name = "Basmati Premium Rice 25kg",
                code = "RIC-001",
                category = "Grains & Pulses",
                hsn = "1006",
                unit = "BAG",
                salePrice = 2450.0,
                purchasePrice = 2100.0,
                taxRate = 5.0,
                stockQuantity = 45.0,
                minStockAlert = 10.0,
                notes = "Royal Feast 1121 Extra Long"
            )
            val item2 = ItemEntity(
                name = "Sunflower Cooking Oil 15L Tin",
                code = "OIL-102",
                category = "Oils & Ghee",
                hsn = "1512",
                unit = "TIN",
                salePrice = 1850.0,
                purchasePrice = 1620.0,
                taxRate = 5.0,
                stockQuantity = 22.0,
                minStockAlert = 5.0,
                notes = "Refined Grade 1"
            )
            val item3 = ItemEntity(
                name = "Tata Salt Iodized 1kg",
                code = "SLT-201",
                category = "Grocery Essentials",
                hsn = "2501",
                unit = "PKT",
                salePrice = 28.0,
                purchasePrice = 22.5,
                taxRate = 0.0,
                stockQuantity = 180.0,
                minStockAlert = 30.0,
                notes = "Vacuum Evaporated"
            )
            val item4 = ItemEntity(
                name = "Aashirvaad Shudh Chakki Atta 10kg",
                code = "ATT-305",
                category = "Flour & Atta",
                hsn = "1101",
                unit = "BAG",
                salePrice = 460.0,
                purchasePrice = 395.0,
                taxRate = 5.0,
                stockQuantity = 3.0, // Low stock!
                minStockAlert = 8.0,
                notes = "100% Whole Wheat"
            )
            val item5 = ItemEntity(
                name = "Detergent Bar Box (50 Pcs)",
                code = "DET-401",
                category = "Cleaning & Household",
                hsn = "3402",
                unit = "BOX",
                salePrice = 850.0,
                purchasePrice = 690.0,
                taxRate = 18.0,
                stockQuantity = 4.0, // Low stock!
                minStockAlert = 10.0,
                notes = "Lemon fresh stain remover"
            )
            val item6 = ItemEntity(
                name = "Cadbury Dairy Milk Silk 60g Pack",
                code = "CHK-505",
                category = "Confectionery",
                hsn = "1806",
                unit = "BOX",
                salePrice = 1200.0,
                purchasePrice = 980.0,
                taxRate = 18.0,
                stockQuantity = 15.0,
                minStockAlert = 5.0,
                notes = "Pack of 20 units"
            )

            itemDao.insertItem(item1)
            itemDao.insertItem(item2)
            itemDao.insertItem(item3)
            itemDao.insertItem(item4)
            itemDao.insertItem(item5)
            itemDao.insertItem(item6)

            // 4. Initial Sample Transactions
            val now = System.currentTimeMillis()
            val oneDay = 24 * 60 * 60 * 1000L

            // Sale 1 to Aakash Supermarket
            val itemsSale1 = listOf(
                InvoiceItem(itemName = "Basmati Premium Rice 25kg", hsn = "1006", quantity = 10.0, unit = "BAG", unitPrice = 2450.0, taxRate = 5.0, discountPercent = 2.0),
                InvoiceItem(itemName = "Sunflower Cooking Oil 15L Tin", hsn = "1512", quantity = 5.0, unit = "TIN", unitPrice = 1850.0, taxRate = 5.0, discountPercent = 0.0)
            )
            val subtotal1 = itemsSale1.sumOf { it.totalWithoutTax }
            val tax1 = itemsSale1.sumOf { it.taxAmount }
            val total1 = subtotal1 + tax1

            transDao.insertTransaction(
                TransactionEntity(
                    invoiceNumber = "INV-1001",
                    type = "SALE",
                    partyId = pCust1Id,
                    partyName = "Aakash Supermarket",
                    partyPhone = "+91 98190 55443",
                    partyGstin = "27AAACA1234A1Z1",
                    date = now - (2 * oneDay),
                    dueDate = now + (12 * oneDay),
                    subtotal = subtotal1,
                    taxAmount = tax1,
                    discountAmount = 490.0,
                    grandTotal = total1,
                    paidAmount = 20000.0,
                    balanceAmount = total1 - 20000.0,
                    paymentMode = "UPI",
                    status = "PARTIAL",
                    notes = "Delivered via tempo MH-04-AB-1234",
                    itemsJson = InvoiceItemJsonHelper.toJson(itemsSale1)
                )
            )

            // Sale 2 to Ramesh General Stores
            val itemsSale2 = listOf(
                InvoiceItem(itemName = "Cadbury Dairy Milk Silk 60g Pack", hsn = "1806", quantity = 5.0, unit = "BOX", unitPrice = 1200.0, taxRate = 18.0, discountPercent = 0.0),
                InvoiceItem(itemName = "Tata Salt Iodized 1kg", hsn = "2501", quantity = 50.0, unit = "PKT", unitPrice = 28.0, taxRate = 0.0, discountPercent = 0.0)
            )
            val subtotal2 = itemsSale2.sumOf { it.totalWithoutTax }
            val tax2 = itemsSale2.sumOf { it.taxAmount }
            val total2 = subtotal2 + tax2

            transDao.insertTransaction(
                TransactionEntity(
                    invoiceNumber = "INV-1002",
                    type = "SALE",
                    partyId = pCust2Id,
                    partyName = "Ramesh General Stores",
                    partyPhone = "+91 98670 11223",
                    partyGstin = "27BBBCA5678B1Z2",
                    date = now - (1 * oneDay),
                    dueDate = now + (6 * oneDay),
                    subtotal = subtotal2,
                    taxAmount = tax2,
                    discountAmount = 0.0,
                    grandTotal = total2,
                    paidAmount = 2000.0,
                    balanceAmount = total2 - 2000.0,
                    paymentMode = "CASH",
                    status = "PARTIAL",
                    notes = "Payment balance promised on Friday",
                    itemsJson = InvoiceItemJsonHelper.toJson(itemsSale2)
                )
            )

            // Purchase 1 from Hindustan FMCG
            val itemsPur1 = listOf(
                InvoiceItem(itemName = "Basmati Premium Rice 25kg", hsn = "1006", quantity = 50.0, unit = "BAG", unitPrice = 2100.0, taxRate = 5.0, discountPercent = 0.0),
                InvoiceItem(itemName = "Sunflower Cooking Oil 15L Tin", hsn = "1512", quantity = 30.0, unit = "TIN", unitPrice = 1620.0, taxRate = 5.0, discountPercent = 0.0)
            )
            val subtotalPur1 = itemsPur1.sumOf { it.totalWithoutTax }
            val taxPur1 = itemsPur1.sumOf { it.taxAmount }
            val totalPur1 = subtotalPur1 + taxPur1

            transDao.insertTransaction(
                TransactionEntity(
                    invoiceNumber = "PUR-2001",
                    type = "PURCHASE",
                    partyId = pSupp1Id,
                    partyName = "Hindustan FMCG Distributors",
                    partyPhone = "+91 98210 77889",
                    partyGstin = "27CCCDA9876C1Z3",
                    date = now - (5 * oneDay),
                    dueDate = now + (10 * oneDay),
                    subtotal = subtotalPur1,
                    taxAmount = taxPur1,
                    discountAmount = 0.0,
                    grandTotal = totalPur1,
                    paidAmount = 120000.0,
                    balanceAmount = totalPur1 - 120000.0,
                    paymentMode = "BANK_TRANSFER",
                    status = "PARTIAL",
                    notes = "PO Ref #HFMCG-882",
                    itemsJson = InvoiceItemJsonHelper.toJson(itemsPur1)
                )
            )

            // Expense 1: Shop Electricity & Rent
            transDao.insertTransaction(
                TransactionEntity(
                    invoiceNumber = "EXP-3001",
                    type = "EXPENSE",
                    partyId = null,
                    partyName = "MSEDCL Electricity Board",
                    partyPhone = "",
                    date = now - (3 * oneDay),
                    dueDate = now - (3 * oneDay),
                    subtotal = 3850.0,
                    taxAmount = 0.0,
                    discountAmount = 0.0,
                    grandTotal = 3850.0,
                    paidAmount = 3850.0,
                    balanceAmount = 0.0,
                    paymentMode = "UPI",
                    status = "PAID",
                    expenseCategory = "Electricity & Utilities",
                    notes = "Monthly bill for Meter Consumer #10293847"
                )
            )

            // Expense 2: Shop Staff Salary & Tea
            transDao.insertTransaction(
                TransactionEntity(
                    invoiceNumber = "EXP-3002",
                    type = "EXPENSE",
                    partyId = null,
                    partyName = "Staff Welfare & Logistics",
                    partyPhone = "",
                    date = now,
                    dueDate = now,
                    subtotal = 1500.0,
                    taxAmount = 0.0,
                    discountAmount = 0.0,
                    grandTotal = 1500.0,
                    paidAmount = 1500.0,
                    balanceAmount = 0.0,
                    paymentMode = "CASH",
                    status = "PAID",
                    expenseCategory = "Logistics & Transport",
                    notes = "Tempo unloading charges"
                )
            )
        }
    }
}
