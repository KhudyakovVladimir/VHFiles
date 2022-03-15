package com.khudyakovvladimir.vhfileexplorer.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.khudyakovvladimir.vhfileexplorer.R
import com.khudyakovvladimir.vhfileexplorer.appComponent
import com.khudyakovvladimir.vhfileexplorer.model.Model
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper
import com.khudyakovvladimir.vhfileexplorer.viewmodel.FileExplorerViewModel
import com.khudyakovvladimir.vhfileexplorer.viewmodel.FileExplorerViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject

class DialogCopy: BottomSheetDialogFragment() {

    lateinit var progressBar: ProgressBar
    lateinit var progressBarHorizontal: ProgressBar
    lateinit var textView: TextView
    lateinit var texViewFile: TextView

    lateinit var fileHelper: FileHelper

    private lateinit var fileExplorerViewModel: FileExplorerViewModel
    private lateinit var fileExplorerViewModelFactory: FileExplorerViewModelFactory

    @Inject
    lateinit var factory: FileExplorerViewModelFactory.Factory

    override fun onAttach(context: Context) {
        context.appComponent.injectDialogCopy(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_copy_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBarDialogCopy)
        progressBarHorizontal= view.findViewById(R.id.progressBarHorizontalDialogCopy)
        textView = view.findViewById(R.id.textViewDialogCopy)
        texViewFile = view.findViewById(R.id.textViewDialogCopyFile)

        fileHelper = FileHelper()

        fileExplorerViewModelFactory = factory.createFileExplorerViewModel(activity!!.application)
        fileExplorerViewModel = ViewModelProvider(this, fileExplorerViewModelFactory).get(FileExplorerViewModel::class.java)

        setFragmentResultListener("copyList") { requestKey: String, bundle: Bundle ->
            val remove = bundle.getBoolean("remove", false)

            if(remove) textView.text = "Moving..."

            CoroutineScope(Dispatchers.IO).launch {
                val copyAll = launch {
                    for (i in 0 until Model.listOfChecked.size) {
                        if(File(Model.listOfChecked[i]).isFile) {
                            fileHelper.copyFile(File(Model.listOfChecked[i]), File(Model.listForCopyTo[i]), progressBarHorizontal, texViewFile)
                        }
                        if(File(Model.listOfChecked[i]).isDirectory) {
                            fileHelper.copyFolder(File(Model.listOfChecked[i]), File(Model.listForCopyTo[i]), progressBarHorizontal, texViewFile)
                        }
                    }
                }
                copyAll.join()
                setFragmentResult("operationCompleted", bundleOf("remove" to remove))
                dismiss()
            }
        }

        setFragmentResultListener("delete") { requestKey: String, bundle: Bundle ->
            val remove = bundle.getBoolean("remove", false)
            textView.text = "Deleting..."

            CoroutineScope(Dispatchers.IO).launch {
                val deleteAll = launch {
                    if (Model.listOfChecked.size != 0) {
                        for (i in 0 until Model.listOfChecked.size) {
                            fileHelper.delete(Model.listOfChecked[i])
                        }
                    }
                }
                deleteAll.join()
                setFragmentResult("operationCompleted", bundleOf("remove" to remove))
                dismiss()
            }
        }
    }
}