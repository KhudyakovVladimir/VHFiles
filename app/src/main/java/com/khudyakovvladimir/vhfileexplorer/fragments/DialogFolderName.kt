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

class DialogFolderName: BottomSheetDialogFragment() {

    lateinit var editTextDialogFragment: EditText
    lateinit var buttonDialogFragment: Button

    var folderForRename = ""

    lateinit var fileHelper: FileHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_folder_name_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileHelper = FileHelper()

        editTextDialogFragment = view.findViewById(R.id.editTextDialogFolder)
        buttonDialogFragment = view.findViewById(R.id.buttonDialogFolder)

        buttonDialogFragment.setOnClickListener {
            val newName = fileHelper.parseStringFolderRename(folderForRename, editTextDialogFragment.text.toString())

            setFragmentResult(
                "dialogFolder",
                bundleOf("dialogFolder" to newName)
            )
            setFragmentResult(
                "dialogRenameFolder",
                bundleOf("dialogRenameFolder" to newName)
            )
            dismiss()
        }

        setFragmentResultListener("dialogNewFolderName") { requestKey: String, bundle: Bundle ->
            folderForRename = bundle.getString("dialogNewFolderName").toString()
            editTextDialogFragment.setText(fileHelper.parseStringForFolderEditText(folderForRename))
        }
    }
}