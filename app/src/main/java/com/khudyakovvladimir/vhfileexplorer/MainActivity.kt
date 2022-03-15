package com.khudyakovvladimir.vhfileexplorer

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.khudyakovvladimir.vhfileexplorer.model.Model

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var sharedPreferences: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(externalMemoryAvailable(this)) Model.sdCardIsAvailable = true

        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))

        var startDestination = R.id.permissionsFragment

        sharedPreferences = applicationContext!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if(sharedPreferences.contains("startDestinationChanged")) {
            if(sharedPreferences.getInt("startDestinationChanged", 0) == 1) {
                startDestination = R.id.listFragment
            }
            if(sharedPreferences.getInt("startDestinationChanged", 0) == 0) {
                startDestination = R.id.fileListGridFragment
            }
        }

        if(sharedPreferences.contains("stack")) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("stack", "")
            editor.apply()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.graph)
        graph.setStartDestination(startDestination)

        navHostFragment.navController.graph = graph

        navController = Navigation.findNavController(this, R.id.fragment_container)
    }

    private fun externalMemoryAvailable(context: Activity?): Boolean {
        val storages = ContextCompat.getExternalFilesDirs(context!!, null)
        return storages.size > 1 && storages[0] != null && storages[1] != null
    }
}