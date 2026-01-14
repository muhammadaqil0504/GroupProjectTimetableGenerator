package com.example.timetableapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

class TimetableStorage(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("timetable_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * SAVE: Converts the List of TimetableEntry into a JSON string and saves it.
     * Because we added 'id' to the data class, GSON will automatically include
     * it in the saved string.
     */
    fun saveSchedule(schedule: List<TimetableEntry>) {
        try {
            val json = gson.toJson(schedule)
            sharedPreferences.edit().putString("user_schedule", json).apply()
            Log.d("TimetableStorage", "Successfully saved ${schedule.size} items.")
        } catch (e: Exception) {
            Log.e("TimetableStorage", "Error saving schedule: ${e.message}")
        }
    }

    /**
     * LOAD: Retrieves the JSON string and converts it back into a List.
     * Includes logic to handle the transition from the old data format to the new one.
     */
    fun loadSchedule(): List<TimetableEntry> {
        val json = sharedPreferences.getString("user_schedule", null)

        // Return empty list if no data exists yet
        if (json.isNullOrEmpty() || json == "[]") return emptyList()

        return try {
            val type = object : TypeToken<List<TimetableEntry>>() {}.type
            val list: List<TimetableEntry> = gson.fromJson(json, type) ?: emptyList()

            // Check if loaded items are missing IDs (Old version data)
            // If the first item has a null or empty ID, it's old data.
            if (list.isNotEmpty() && (list[0].id.isNullOrEmpty())) {
                Log.w("TimetableStorage", "Old data format detected. Clearing to prevent bugs.")
                clearSchedule()
                return emptyList()
            }

            list
        } catch (e: Exception) {
            Log.e("TimetableStorage", "Data corruption or format mismatch: ${e.message}")
            // Return empty list if parsing fails
            emptyList()
        }
    }

    /**
     * RESET: Completely wipes the saved timetable from the phone's memory.
     */
    fun clearSchedule() {
        sharedPreferences.edit().remove("user_schedule").apply()
        Log.d("TimetableStorage", "Storage cleared successfully.")
    }
}