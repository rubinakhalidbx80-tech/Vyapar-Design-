package com.example.data.local

import com.example.data.model.InvoiceItem
import org.json.JSONArray
import org.json.JSONObject

object InvoiceItemJsonHelper {
    fun toJson(items: List<InvoiceItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("itemId", item.itemId)
            obj.put("itemName", item.itemName)
            obj.put("hsn", item.hsn)
            obj.put("quantity", item.quantity)
            obj.put("unit", item.unit)
            obj.put("unitPrice", item.unitPrice)
            obj.put("taxRate", item.taxRate)
            obj.put("discountPercent", item.discountPercent)
            array.put(obj)
        }
        return array.toString()
    }

    fun fromJson(json: String?): List<InvoiceItem> {
        if (json.isNullOrBlank()) return emptyList()
        val result = mutableListOf<InvoiceItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    InvoiceItem(
                        itemId = obj.optLong("itemId", 0L),
                        itemName = obj.optString("itemName", ""),
                        hsn = obj.optString("hsn", ""),
                        quantity = obj.optDouble("quantity", 1.0),
                        unit = obj.optString("unit", "PCS"),
                        unitPrice = obj.optDouble("unitPrice", 0.0),
                        taxRate = obj.optDouble("taxRate", 0.0),
                        discountPercent = obj.optDouble("discountPercent", 0.0)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
