package com.procalc.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class HistoryItem(
    val id: Long = System.currentTimeMillis(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HistoryManager(private val context: Context) {
    private val gson = Gson()
    private val fileName = "calc_history.json"

    fun saveHistory(item: HistoryItem) {
        val list = getHistory().toMutableList()
        list.add(0, item) // Add to top
        writeList(list.take(50)) // Keep last 50
    }

    fun getHistory(): List<HistoryItem> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()
        
        return try {
            val json = file.readText()
            val type = object : TypeToken<List<HistoryItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearHistory() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) file.delete()
    }

    private fun writeList(list: List<HistoryItem>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(list))
    }
}