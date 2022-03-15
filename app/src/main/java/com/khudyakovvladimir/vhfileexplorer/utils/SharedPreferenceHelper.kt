package com.khudyakovvladimir.vhfileexplorer.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject

class SharedPreferenceHelper @Inject constructor(val application: Application) {

    private fun <T> savePreference(context: Context, key: String, value: T) {
        val sharedPreferences = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        when (value) {
            is String -> { editor.putString(key , value) }
            is Int -> { editor.putInt(key , value) }
            is Float -> { editor.putFloat(key , value) }
            is Long -> { editor.putLong(key , value) }
            is Boolean -> { editor.putBoolean(key , value) }
        }

        editor.apply()
    }

    fun <T> readPreference(name: String, key: String, value: T ): T?  {
        var result: T? = null
        val sharedPreferences = application.getSharedPreferences(name, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(key)) {
            when (value) {
                is String -> { result = (sharedPreferences.getString(key , value) as T) }
                is Int -> { result = (sharedPreferences.getInt(key , value) as T) }
                is Float -> { result = (sharedPreferences.getFloat(key , value) as T) }
                is Long -> { result = (sharedPreferences.getLong(key , value) as T) }
                is Boolean -> { result = (sharedPreferences.getBoolean(key , value) as T) }
            }
        }
        return result
    }
}