package com.khudyakovvladimir.vhfileexplorer.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.khudyakovvladimir.vhfileexplorer.R
import com.khudyakovvladimir.vhfileexplorer.appComponent
import com.khudyakovvladimir.vhfileexplorer.model.Model
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper
import com.khudyakovvladimir.vhfileexplorer.viewmodel.FileExplorerViewModel
import com.khudyakovvladimir.vhfileexplorer.viewmodel.FileExplorerViewModelFactory
import com.khudyakovvladimir.vhfileexplorer.widgets.ExplorerRecyclerViewAdapterGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject

class FileListGridFragment: Fragment() {

    var codeForMenu = 0
    var adapterPosition: Int = 0

    var currentFile = ""
    var fileToCopy = ""
    var newFolderName = ""
    var newFileName = ""
    private var codeForLayoutManager: Int = 1

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var fileExplorerViewModel: FileExplorerViewModel
    private lateinit var fileExplorerViewModelFactory: FileExplorerViewModelFactory

    private lateinit var recyclerView: RecyclerView
    private lateinit var explorerRecyclerViewAdapter: ExplorerRecyclerViewAdapterGrid

    lateinit var textViewListFragment: TextView
    lateinit var textViewInternalStorage: TextView
    lateinit var textViewSdCard: TextView

    lateinit var imageViewInternal: ImageView
    lateinit var imageViewSdCard: ImageView

    lateinit var linearLayoutInternal: LinearLayout
    lateinit var linearLayoutSdCard: LinearLayout
    lateinit var linearLayoutSpace: LinearLayout

    @Inject
    lateinit var fileHelper: FileHelper

    @Inject
    lateinit var factory: FileExplorerViewModelFactory.Factory

    override fun onAttach(context: Context) {
        context.appComponent.injectFileListGridFragment(this)
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("codeForLayoutManager")) {
            codeForLayoutManager = sharedPreferences.getInt("codeForLayoutManager", 0)
        }
        if(sharedPreferences.contains("adapterPosition")) {
            adapterPosition = sharedPreferences.getInt("adapterPosition", 0)
        }
        if(sharedPreferences.contains("stack")) {
            Model.stack.clear()
            val stack = sharedPreferences.getString("stack", "")
            if(stack == "") {
                Model.ROOT_PATH = "/storage/emulated/0"
            }
            fileHelper.parseStack(stack!!)
        }
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var layout = R.layout.list_fragment_layout
        if(!Model.sdCardIsAvailable) {
            layout = R.layout.list_fragment_layout_two
        }
        return inflater.inflate(layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewListFragment = view.findViewById(R.id.textViewListFragment)
        textViewInternalStorage = view.findViewById(R.id.textViewListFragmentInternalStorage)
        textViewSdCard = view.findViewById(R.id.textViewListFragmentSdCard)

        imageViewInternal = view.findViewById(R.id.imageViewInternal)
        imageViewSdCard = view.findViewById(R.id.imageViewSdCard)

        linearLayoutInternal = view.findViewById(R.id.linearLayoutInternal)
        linearLayoutSdCard = view.findViewById(R.id.linearLayoutSdCard)
        linearLayoutSpace = view.findViewById(R.id.linearLayoutSpace)

        fileExplorerViewModelFactory = factory.createFileExplorerViewModel(activity!!.application)
        fileExplorerViewModel = ViewModelProvider(this, fileExplorerViewModelFactory).get(FileExplorerViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewListFragment)
        recyclerView.layoutManager = GridLayoutManager(activity!!.applicationContext, 4)

        fileHelper.fadeInView(imageViewInternal)
        fileHelper.fadeInView(textViewInternalStorage)
        fileHelper.fadeInView(textViewListFragment)
        fileHelper.fadeInView(imageViewSdCard)
        fileHelper.fadeInView(textViewSdCard)
        fileHelper.fadeInView(recyclerView)

        val itemClick = { file: File -> click(file)}
        val itemLongClick = { file: File -> longClick(file) }

        val listForAdapter = fileExplorerViewModel.setValueToListForAdapter(Model.ROOT_PATH)

        if(!Model.stack.contains(Model.ROOT_PATH)) {
            Model.stack.push(Model.ROOT_PATH)
        }

        textViewListFragment.text = Model.ROOT_PATH

        explorerRecyclerViewAdapter = ExplorerRecyclerViewAdapterGrid(
            activity!!.applicationContext,
            listForAdapter,
            FileHelper(),
            itemClick,
            itemLongClick
        )

        recyclerView.adapter = explorerRecyclerViewAdapter

        lifecycleScope.launch {
            delay(100)
            recyclerView.scrollToPosition(adapterPosition)
        }

        fileExplorerViewModel.setValueToSpaces()

        fileExplorerViewModel.getListForAdapter().observe(this) {
            explorerRecyclerViewAdapter.updateAdapter(it)
        }

        fileExplorerViewModel.getProgressForTextViewNow().observe(this) {
            textViewListFragment.text = it
        }

        fileExplorerViewModel.getInternalSpaceForTextView().observe(this) {
            fileHelper.setTextForTextView(context!!, it, textViewInternalStorage)
        }
        fileExplorerViewModel.getSdCardSpaceForTextView().observe(this) {
            fileHelper.setTextForTextView(context!!, it, textViewSdCard)
        }

        linearLayoutInternal.setOnClickListener {
            Model.stack.clear()
            Model.ROOT_PATH = Environment.getExternalStorageDirectory().path
            fileExplorerViewModel.setValueToListForAdapter(Model.ROOT_PATH)
            Model.stack.push(Model.ROOT_PATH)

            explorerRecyclerViewAdapter.notifyDataSetChanged()

            textViewListFragment.text = Model.ROOT_PATH
        }

        linearLayoutInternal.setOnLongClickListener {
            val v = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK))
            linearLayoutSpace.layoutParams.height = 300
            linearLayoutSpace.requestLayout()
            return@setOnLongClickListener true
        }

        linearLayoutSdCard.setOnClickListener {
            Model.stack.clear()
            Model.ROOT_PATH = fileHelper.getExternalCardDirectory(context!!)
            fileExplorerViewModel.setValueToListForAdapter(Model.ROOT_PATH)
            Model.stack.push(Model.ROOT_PATH)

            explorerRecyclerViewAdapter.notifyDataSetChanged()

            textViewListFragment.text = Model.ROOT_PATH
        }

        linearLayoutSdCard.setOnLongClickListener {
            val v = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK))
            linearLayoutSpace.layoutParams.height = 300
            linearLayoutSpace.requestLayout()
            return@setOnLongClickListener true
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(Model.stack.size > 1) {
                    Model.stack.pop()
                    fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
                    textViewListFragment.text = Model.stack.last()
                    currentFile = Model.stack.last()

                    if(sharedPreferences.contains("adapterPosition")) {
                        adapterPosition = sharedPreferences.getInt("adapterPosition", 0)
                    }

                    lifecycleScope.launch {
                        delay(100)
                        recyclerView.scrollToPosition(adapterPosition)
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        setFragmentResultListener("dialogFolder") { requestKey: String, bundle: Bundle ->
            val folderName = bundle.getString("dialogFolder")
            newFolderName = folderName!!
            val file = File("${Model.stack.last()}/$newFolderName")
            file.mkdir()
            Model.listOfChecked.clear()
            fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
        }

        setFragmentResultListener("dialogRenameFolder") { requestKey: String, bundle: Bundle ->
            val folderName = bundle.getString("dialogRenameFolder")
            newFolderName = folderName!!
            File(currentFile).renameTo(File(folderName))
            Model.listOfChecked.clear()
            fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
        }

        setFragmentResultListener("dialogFile") { requestKey: String, bundle: Bundle ->
            val fileName = bundle.getString("dialogFile")
            newFileName = fileName!!
            val file = File(currentFile)
            file.renameTo(File(newFileName))
            Model.listOfChecked.clear()
            fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
        }

        setFragmentResultListener("operationCompleted") { requestKey: String, bundle: Bundle ->
            val remove = bundle.getBoolean("remove", false)

            if(remove) {
                for (file in Model.listOfChecked) {
                    fileHelper.delete(file)
                }
            }

            fileExplorerViewModel.setValueToSpaces()

            Model.listOfChecked.clear()
            Model.listForCopyTo.clear()

            fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        when(codeForMenu) {
            0 -> { inflater.inflate(R.menu.action_bar_menu_two, menu) }
            1 -> { inflater.inflate(R.menu.action_bar_menu_paste, menu) }
            2 -> { inflater.inflate(R.menu.action_menu_remove_paste, menu) }
            3 -> { inflater.inflate(R.menu.action_bar_menu_work, menu) }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.about -> {
                setFragmentResult(
                    "dialogAbout",
                    bundleOf("fileAbout" to currentFile)
                )
                codeForMenu = 0
                Model.listOfChecked.clear()
                fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
                activity!!.invalidateOptionsMenu()
                findNavController().navigate(R.id.dialogAbout)
            }

            R.id.back -> {
                codeForMenu = 0
                Model.listOfChecked.clear()
                fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
                activity!!.invalidateOptionsMenu()
            }

            R.id.grid -> {
                val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("startDestinationChanged", 0)
                editor.putString("stack", Model.stack.toString())
                editor.apply()
                findNavController().navigate(R.id.fileListGridFragment)
            }

            R.id.list -> {
                val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("startDestinationChanged", 1)
                editor.putString("stack", Model.stack.toString())
                editor.apply()
                findNavController().navigate(R.id.listFragment)
            }

            R.id.copy -> {
                codeForMenu = 1
                fileToCopy = currentFile
                activity!!.invalidateOptionsMenu()
            }
            R.id.delete -> {
                codeForMenu = 0
                fileHelper.startDialogCopy(this, false, "delete")
                activity!!.invalidateOptionsMenu()
            }
            R.id.remove -> {
                codeForMenu = 2
                fileToCopy = currentFile
                activity!!.invalidateOptionsMenu()
            }
            R.id.rename -> {
                if(File(currentFile).isFile) {
                    Log.d("TAG", "FILE_LIST_FRAGMENT - RENAME - currentFile isFile()")
                    setFragmentResult(
                        "dialogNewFileName",
                        bundleOf("dialogNewFileName" to currentFile)
                    )
                    findNavController().navigate(R.id.dialogFileName)
                }
                if(File(currentFile).isDirectory) {
                    setFragmentResult(
                        "dialogNewFolderName",
                        bundleOf("dialogNewFolderName" to currentFile)
                    )
                    findNavController().navigate(R.id.dialogFolderName)
                }
                codeForMenu = 0
                activity!!.invalidateOptionsMenu()
            }

            R.id.paste -> {
                codeForMenu = 0

                if(Model.listOfChecked.size != 0) {
                    for (filePath in Model.listOfChecked) {
                        if(File(filePath).isFile) {
                            Model.listForCopyTo.add(fileHelper.parseNewFileName(filePath, currentFile))
                        }
                        if(File(filePath).isDirectory) {
                            Model.listForCopyTo.add(fileHelper.parseNewFolderName(filePath, currentFile))
                        }
                    }
                    fileHelper.startDialogCopy(this, false, "copyList")
                }else { }

                fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
                activity!!.invalidateOptionsMenu()
            }

            R.id.removePaste -> {
                codeForMenu = 0

                if(Model.listOfChecked.size != 0) {
                    for (filePath in Model.listOfChecked) {
                        if(File(filePath).isFile) {
                            Model.listForCopyTo.add(fileHelper.parseNewFileName(filePath, currentFile))
                        }
                        if(File(filePath).isDirectory) {
                            Model.listForCopyTo.add(fileHelper.parseNewFolderName(filePath, currentFile))
                        }
                    }
                    fileHelper.startDialogCopy(this, true, "copyList")
                }else { }

                fileExplorerViewModel.setValueToListForAdapter(Model.stack.last())
                activity!!.invalidateOptionsMenu()
            }
            R.id.createNewFolder -> {
                findNavController().navigate(R.id.dialogFolderName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun click(file: File) {
        currentFile = file.absolutePath.toString()
        textViewListFragment.text = currentFile

        if(File(currentFile).isDirectory) {
            fileExplorerViewModel.setValueToListForAdapter(currentFile)
            Model.stack.push(currentFile)
        }

        if(File(currentFile).isFile) {
            if(fileHelper.openFile(this, currentFile)) {

            }else {
                makeToast("This file does not open")
            }
        }

        if(flagSaveAdapterPosition) {
            if(recyclerView.layoutManager is LinearLayoutManager) {
                val position = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position)
                editor.apply()
            }
            if(recyclerView.layoutManager is StaggeredGridLayoutManager) {
                val array = IntArray((recyclerView.layoutManager as StaggeredGridLayoutManager).spanCount)
                val position = (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(array)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position[0])
                editor.apply()
            }
        }
    }

    private fun longClick(file: File): Boolean {
        currentFile = file.absolutePath.toString()
        if(file.isFile) {
            Model.listOfChecked.add(file.absolutePath)
            Model.map[file] = true
            explorerRecyclerViewAdapter.notifyDataSetChanged()
        }
        if(file.isDirectory) {
            Model.listOfChecked.add(file.absolutePath)
            Model.map[file] = true
            explorerRecyclerViewAdapter.notifyDataSetChanged()
        }

        codeForMenu = 3
        activity!!.invalidateOptionsMenu()
        return true
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        activity?.let {
            val toast = Toast.makeText(it, text, duration)
            toast.setGravity(Gravity.TOP, 0, 150)
            toast.show()
        }
    }

    override fun onPause() {
        super.onPause()
        if(flagSaveAdapterPosition) {
            if(recyclerView.layoutManager is LinearLayoutManager) {
                val position = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position)
                editor.apply()
            }
            if(recyclerView.layoutManager is StaggeredGridLayoutManager) {
                val array = IntArray((recyclerView.layoutManager as StaggeredGridLayoutManager).spanCount)
                val position = (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(array)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position[0])
                editor.apply()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(flagSaveAdapterPosition) {
            if(recyclerView.layoutManager is LinearLayoutManager) {
                val position = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position)
                editor.apply()
            }
        }
        flagSaveAdapterPosition = true
    }

    companion object {
        var flagSaveAdapterPosition: Boolean = true
    }
}