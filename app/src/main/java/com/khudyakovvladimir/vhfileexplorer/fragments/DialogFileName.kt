package com.khudyakovvladimir.vhfileexplorer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.khudyakovvladimir.vhfileexplorer.R
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper

class DialogFileName: BottomSheetDialogFragment() {

    lateinit var editTextDialogFragment: EditText
    lateinit var buttonDialogFragment: Button

    var fileForRename = ""
    lateinit var fileHelper: FileHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_file_name_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextDialogFragment = view.findViewById(R.id.editTextDialogFile)
        buttonDialogFragment = view.findViewById(R.id.buttonDialogFile)

        fileHelper = FileHelper()

        buttonDialogFragment.setOnClickListener {
            val newName = fileHelper.parseStringFileRename(fileForRename, editTextDialogFragment.text.toString())

            setFragmentResult(
                "dialogFile",
                bundleOf("dialogFile" to newName, "fileForRename" to fileForRename)
            )
            dismiss()
        }

        setFragmentResultListener("dialogNewFileName") { requestKey: String, bundle: Bundle ->
            fileForRename = bundle.getString("dialogNewFileName").toString()
            editTextDialogFragment.setText(fileHelper.parseStringForFileEditText(fileForRename))
        }
    }
}