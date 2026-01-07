package com.example.timetableapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimetableStorage(context: Context) {
    // Creating a private storage file named "timetable_prefs"
    private val sharedPreferences = context.getSharedPreferences("timetable_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * SAVE: Converts the list of TimetableEntry objects into a JSON string
     * and stores it in the phone's internal storage.
     */
    fun saveSchedule(schedule: List<TimetableEntry>) {
        val json = gson.toJson(schedule)
        sharedPreferences.edit().putString("user_schedule", json).apply()
    }

    /**
     * LOAD: Retrieves the JSON string from storage and converts it
     * back into a List of TimetableEntry objects.
     */
    fun loadSchedule(): List<TimetableEntry> {
        val json = sharedPreferences.getString("user_schedule", null)
        return if (json != null) {
            val type = object : TypeToken<List<TimetableEntry>>() {}.type
            gson.fromJson(json, type)
        } else {
            // Return an empty list if no data has been saved yet
            emptyList()
        }
    }

    /**
     * RESET: Deletes the saved timetable data from the phone's memory.
     * This is used for the "Reset for New Year" feature.
     */
    fun clearSchedule() {
        sharedPreferences.edit().remove("user_schedule").apply()
    }
}