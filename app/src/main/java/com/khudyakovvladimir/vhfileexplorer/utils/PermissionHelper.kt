package com.khudyakovvladimir.vhfileexplorer.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.khudyakovvladimir.vhfileexplorer.BuildConfig
import javax.inject.Inject

class PermissionHelper @Inject constructor(val context: Context) {

    lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermissions(): Boolean {

        if(Build.VERSION.SDK_INT >= 30) {
            getAllFileAccess()
        }else{

        }

        if (!isExternalStorageReadable() || !isExternalStorageWriteable()) {
            return false
        }
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.CHANGE_CONFIGURATION,
                    Manifest.permission.DELETE_CACHE_FILES,
                    Manifest.permission.DELETE_PACKAGES,
                    Manifest.permission.GET_PACKAGE_SIZE,
                    Manifest.permission.INSTALL_LOCATION_PROVIDER,
                    Manifest.permission.INSTALL_PACKAGES,
                    Manifest.permission.MANAGE_DOCUMENTS,
                    Manifest.permission.MANAGE_MEDIA,
                    Manifest.permission.MEDIA_CONTENT_CONTROL,
                    Manifest.permission.REQUEST_DELETE_PACKAGES,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CAMERA,
                    Manifest.permission.VIBRATE
                ),1111)
            savePreference()
            return false
        }
        return true
    }

    private fun isExternalStorageWriteable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    private fun savePreference() {
        sharedPreferences = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("permissions", true)
        editor.apply()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getAllFileAccess() {
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")

        context.startActivity(
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                uri
            )
        )
    }
}