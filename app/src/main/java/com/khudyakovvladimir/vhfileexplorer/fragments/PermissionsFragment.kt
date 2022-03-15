package com.khudyakovvladimir.vhfileexplorer.fragments

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khudyakovvladimir.vhfileexplorer.R
import com.khudyakovvladimir.vhfileexplorer.utils.PermissionHelper

class PermissionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.permissions_fragment_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonOk = view.findViewById<Button>(R.id.button)
        val permissionHelper = activity?.let { PermissionHelper(it) }


        buttonOk.setOnClickListener {
            permissionHelper?.checkPermissions()

            val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("startDestinationChanged", 1)
            editor.apply()

            findNavController().navigate(R.id.listFragment)
        }
    }
}