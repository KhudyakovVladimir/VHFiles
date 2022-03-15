package com.khudyakovvladimir.vhfileexplorer.fragments

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.khudyakovvladimir.vhfileexplorer.R
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper
import java.io.File

class DialogAbout: BottomSheetDialogFragment() {

    lateinit var fileHelper: FileHelper

    var file: String = ""

    lateinit var imageViewAbout: ImageView

    lateinit var textViewFileName: TextView
    lateinit var textViewPath: TextView
    lateinit var textViewSize: TextView
    lateinit var textViewChange: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_about_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewAbout = view.findViewById(R.id.imageViewAbout)

        textViewFileName = view.findViewById(R.id.textViewFileNameAbout)
        textViewPath = view.findViewById(R.id.textViewPathAbout)
        textViewSize = view.findViewById(R.id.textViewSizeAbout)
        textViewChange = view.findViewById(R.id.textViewChanedAbout)

        fileHelper = FileHelper()

        setFragmentResultListener("dialogAbout") { requestKey: String, bundle: Bundle ->
            file = bundle.getString("fileAbout").toString()

            if(File(file).isFile) {
                if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "audio/mpeg") { imageViewAbout.setImageResource(R.drawable.audio) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "audio/x-wav") { imageViewAbout.setImageResource(R.drawable.audio) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "video/mp4") { imageViewAbout.setImageResource(R.drawable.video) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "video/mpeg") { imageViewAbout.setImageResource(R.drawable.video) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "video/x-ms-wmv") { imageViewAbout.setImageResource(R.drawable.video) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "image/jpeg") { imageViewAbout.setImageResource(R.drawable.image) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "image/png") { imageViewAbout.setImageResource(R.drawable.image) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "image/gif") { imageViewAbout.setImageResource(R.drawable.image) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "application/pdf") { imageViewAbout.setImageResource(R.drawable.pdf) }
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "text/plain") { imageViewAbout.setImageResource(R.drawable.text)}
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "text/rtf") { imageViewAbout.setImageResource(R.drawable.text)}
                else if(fileHelper.getMimeType(File(file).absolutePath.toString()) == "application/rar") { imageViewAbout.setImageResource(R.drawable.rar) }
                else { imageViewAbout.setImageResource(R.drawable.file2) }

                textViewFileName.text = fileHelper.parseStringFileName(file)
                textViewPath.text = file
                textViewSize.text = fileHelper.fileSize(file)
                textViewChange.text = fileHelper.getDateAndTime(file)
            }
            if(File(file).isDirectory) {
                imageViewAbout.setImageResource(R.drawable.folder)
                textViewFileName.text = fileHelper.parseStringForFolderEditText(file)
                textViewPath.text = file
                textViewSize.text = fileHelper.folderSizeInMegabytes(fileHelper.folderSize(File(file)))
                textViewChange.text = fileHelper.getDateAndTime(file)
            }
        }
    }

}