package com.khudyakovvladimir.vhfileexplorer.widgets

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khudyakovvladimir.vhfileexplorer.R
import com.khudyakovvladimir.vhfileexplorer.model.Model
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper
import java.io.File
import javax.inject.Inject

class ExplorerRecyclerViewAdapterGrid @Inject constructor(
    val context: Context,
    private var listFiles: List<File>,
    val fileHelper: FileHelper,
    private val itemClick: (file: File) -> Unit,
    private val itemLongClick: (file: File) -> Boolean,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ExplorerHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var imageViewItem: ImageView
        lateinit var textViewItem: TextView
        lateinit var checkBoxItem: CheckBox

        fun bind(file: File, position: Int) {
            imageViewItem = itemView.findViewById(R.id.imageViewItem)
            textViewItem = itemView.findViewById(R.id.textViewItem)
            checkBoxItem = itemView.findViewById(R.id.checkBoxItem)

            imageViewItem.setOnClickListener {
                itemClick(file)
            }

            imageViewItem.setOnLongClickListener {
                itemLongClick(file)
            }

            textViewItem.setOnClickListener {
                itemClick(file)
            }

            textViewItem.setOnLongClickListener {
                itemLongClick(file)
            }

            checkBoxItem.setOnClickListener {
            }

            checkBoxItem.setOnCheckedChangeListener { compoundButton, b: Boolean ->
                Model.map[file] = b
                if(b && !Model.listOfChecked.contains(file.absolutePath)) {
                    Model.listOfChecked.add(file.absolutePath)
                }
                if(!b && Model.listOfChecked.contains(file.absolutePath)) {
                    Model.listOfChecked.remove(file.absolutePath)
                }
            }

            checkBoxItem.isChecked = Model.map[file]!!

            if(file.isFile) {

                if(fileHelper.getMimeType(file.absolutePath.toString()) == "audio/mpeg") { imageViewItem.setImageResource(R.drawable.audio) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "audio/x-wav") { imageViewItem.setImageResource(R.drawable.audio) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "video/mp4") { imageViewItem.setImageResource(R.drawable.video) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "video/mpeg") { imageViewItem.setImageResource(R.drawable.video) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "video/x-ms-wmv") { imageViewItem.setImageResource(R.drawable.video) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "image/jpeg") { imageViewItem.setImageResource(R.drawable.image) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "image/png") { imageViewItem.setImageResource(R.drawable.image) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "image/gif") { imageViewItem.setImageResource(R.drawable.image) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "application/pdf") { imageViewItem.setImageResource(R.drawable.pdf) }
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "text/plain") { imageViewItem.setImageResource(R.drawable.text)}
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "text/rtf") { imageViewItem.setImageResource(R.drawable.text)}
                else if(fileHelper.getMimeType(file.absolutePath.toString()) == "application/rar") { imageViewItem.setImageResource(R.drawable.rar) }
                else { imageViewItem.setImageResource(R.drawable.file2) }
            }
            if(file.isDirectory) {
                imageViewItem.setImageResource(R.drawable.folder)

                if(file.absolutePath != "/storage/emulated/0/Android") {
                }
            }

            val str = file.absolutePath.toString()
            val textForTextView = fileHelper.parseFolderPath(str)
            textViewItem.text = textForTextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder?
        val view: View? = LayoutInflater.from(parent.context).inflate(R.layout.list_item_grid, parent, false)
        viewHolder = ExplorerHolder(view!!)

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val explorerHolder = holder as ExplorerHolder
        explorerHolder.bind(listFiles[position], position)
    }

    override fun getItemCount(): Int {
        return listFiles.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateAdapter(_listFiles: List<File>) {
        this.listFiles.apply {
            listFiles = emptyList()
            listFiles = _listFiles.toList().sorted()
            notifyDataSetChanged()
        }
    }
}