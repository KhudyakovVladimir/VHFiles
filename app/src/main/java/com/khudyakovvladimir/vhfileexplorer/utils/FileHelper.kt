package com.khudyakovvladimir.vhfileexplorer.utils

import android.annotation.SuppressLint
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.Spannable
import android.text.SpannableString
import android.text.format.Formatter.formatFileSize
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.khudyakovvladimir.vhfileexplorer.R
import com.khudyakovvladimir.vhfileexplorer.model.Model
import java.io.*
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.ZoneId
import javax.inject.Inject

class FileHelper @Inject constructor() {

    fun fileSize(pathName: String): String? {
        val file = File(pathName)
        val megaBytes = file.length().toDouble() / (1024 * 1024)
        val str = String.format("%.2f", megaBytes)
        return "$str mb"
    }

    fun folderSize(directory: File): Long {
        var length: Long = 0
        try {
            for (file in directory.listFiles()!!) {
                length += if (file.isFile) file.length() else folderSize(file)
            }
        }catch (e: Exception) { }
        return length
    }

    fun folderSizeInMegabytes(size: Long): String {
        val megaBytes = size.toDouble() / (1024 * 1024)
        val str = String.format("%.2f", megaBytes)
        var resultString = ""
        if(megaBytes < 1000) {
            resultString = "$str mb"
        }
        if(megaBytes >= 1000) {
            val gb = megaBytes/1000
            val size = gb.toString().substring(0, 4)
            resultString = "$size Gb"
        }
        return resultString
    }

    fun openFile(fragment: Fragment, localUri: String): Boolean {
        val contentUri = Uri.parse(
            "content://com.khudyakovvladimir.vhfileexplorer.provider/root/storage/"
                    + parseUri(localUri)
        )
        val openFileIntent = Intent(Intent.ACTION_VIEW)
        openFileIntent.setDataAndTypeAndNormalize(contentUri, getMimeType(localUri))
        openFileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            fragment.startActivity(openFileIntent)
        }catch (e: Exception) {
            return false
        }
        return true
    }

    fun getMimeType(url: String): String? {
        var mimeType = ""
        try{
            val extension = url.substring(url.lastIndexOf("."))
            val mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension)
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap)!!
        }catch (e: Exception) { }

        return mimeType
    }

    fun parseFolderPath(path: String): String {
        val str: String
        val arrayList = ArrayList<String>()
        for (s in path.split("/").toTypedArray()) {
            arrayList.add(s)
        }
        str = arrayList[arrayList.size - 1]
        return str
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseNewFolderName(folderToRemove: String, currentFolder: String): String {
        val endName = parseStringForFolderEditText(folderToRemove)
        return "$currentFolder/$endName"
    }

    private fun parseUri(name: String): String {
        val arrayList = ArrayList<String>()
        val list = name.split("\\.".toRegex(), 2).toTypedArray()
        for (str in list) {
            arrayList.add(str)
        }
        val tmp = arrayList[0]
        val arrayList2 = ArrayList<String>()
        for (s in tmp.split("/").toTypedArray()) {
            arrayList2.add(s)
        }
        arrayList2.removeAt(0)
        arrayList2.removeAt(0)

        val stringBuilder = StringBuilder()
        for (i in arrayList2.indices) {
            stringBuilder.append(arrayList2[i])
            stringBuilder.append("/")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)

        var resultString = ""

        try {
            resultString = stringBuilder.toString() + "." + arrayList[1]
        }catch (e: Exception) {
            resultString = stringBuilder.toString() + "." + arrayList[0]
        }

        return resultString
    }

    fun parseNewFileName(fileToCopy: String, currentFile: String): String {
        var newFileName = ""

        if(File(fileToCopy).isFile) {
            val arrayList = ArrayList<String>()
            val list = fileToCopy.split("/").toTypedArray()
            for (str in list) {
                arrayList.add(str)
            }

            val tmp = arrayList[arrayList.size - 1]
            val arrayList2 = ArrayList<String>()
            for (s in tmp.split(".").toTypedArray()) {
                arrayList2.add(s)
            }

            newFileName = currentFile + "/" + arrayList[arrayList.size - 1]
        }
        if(File(fileToCopy).isDirectory) {}

        return newFileName
    }

    fun parseStringFileName(fileName: String): String {
        var resultFileName = ""

        if(File(fileName).isFile) {
            val arrayList = ArrayList<String>()
            val list = fileName.split("/").toTypedArray()
            for (str in list) {
                arrayList.add(str)
            }

            val tmp = arrayList[arrayList.size - 1]
            val arrayList2 = ArrayList<String>()
            for (s in tmp.split(".").toTypedArray()) {
                arrayList2.add(s)
            }
            resultFileName = arrayList[arrayList.size - 1]
        }
        if(File(fileName).isDirectory) {}

        return resultFileName
    }

    fun parseStringFileRename(fileToCopy: String, newFileName: String): String {
        val file = File(fileToCopy)
        var resultString = ""

        if(file.isFile) {
            val arrayList = ArrayList<String>()
            val list = fileToCopy.split("\\.".toRegex(), 2).toTypedArray()
            for (str in list) {
                arrayList.add(str)
            }
            val tmp = arrayList[0].split("/").toTypedArray()

            var stringBuilder = StringBuilder()

            for (s in tmp.indices) {
                if(s < tmp.size - 1) {
                    stringBuilder.append(tmp[s])
                    stringBuilder.append("/")
                }
            }

            var listMimeType = arrayList[1].split("\\.".toRegex(), 10).toTypedArray()
            for (str2 in listMimeType.indices) {
            }

            val mimeTypeStr = "." + listMimeType[listMimeType.size - 1]
            resultString = "$stringBuilder$newFileName$mimeTypeStr"
        }

        if(file.isDirectory) {
            resultString = fileToCopy
        }
        return resultString
    }

    fun parseStringForFileEditText(fileToRename: String): String {
        val arrayList = ArrayList<String>()
        val arrayList2 = ArrayList<String>()

        val list = fileToRename.split("/").toTypedArray()
        for (str in list) {
            arrayList.add(str)
        }

        val fullFileName = arrayList[arrayList.size - 1]
        val list2 = fullFileName.split("\\.".toRegex(), 10).toTypedArray()

        var stringBuilder = StringBuilder()

        for (str in list2) {
            arrayList2.add(str)
        }

        for (str in arrayList2) {
            if (str == arrayList2[arrayList2.size - 1]) {
                break
            }
            stringBuilder.append(str)
            stringBuilder.append(".")

        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)

        return stringBuilder.toString()
    }

    fun parseStringForFolderEditText(folderToRename: String): String {
        val arrayList = ArrayList<String>()
        val list = folderToRename.split("/").toTypedArray()
        for (str in list) {
            arrayList.add(str)
        }
        return arrayList[arrayList.size - 1]
    }

    fun parseStringFolderRename(folderToRename: String, newFolderName: String): String {
        val arrayList = ArrayList<String>()
        val list = folderToRename.split("/").toTypedArray()
        for (str in list) {
            arrayList.add(str)
        }

        var stringBuilder = StringBuilder()

        for (s in arrayList.indices) {
            if (s < arrayList.size - 1) {
                stringBuilder.append(arrayList[s])
                stringBuilder.append("/")
            }
        }

        return stringBuilder.toString() + newFolderName
    }

    fun delete(fileName: String) {
        val file = File(fileName)
        if (file.isFile) {
            file.delete()
        }
        if (file.isDirectory) {
            for (c in file.listFiles()) {
                delete(c.toString())
            }
            file.delete()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startDialogCopy(fragment: Fragment, remove: Boolean, operation: String) {
        val bundle = Bundle()
        bundle.putStringArrayList("listOfChecked", Model.listOfChecked as ArrayList<String>)
        bundle.putStringArrayList("listForCopyTo", Model.listForCopyTo as ArrayList<String>)
        bundle.putBoolean("remove", remove)
        fragment.setFragmentResult(operation, bundle)
        fragment.findNavController().navigate(R.id.dialogCopy)

    }

    fun copyFile(fileSource: File?, fileDestination: File, progressBar: ProgressBar, textView: TextView) {
        val fis = FileInputStream(fileSource)
        val fos = FileOutputStream(fileDestination)

        val lengthOfFile: Long = fileSource!!.length()

        val buffer = ByteArray(4096)
        var length: Int
        var total: Long = 0
        var progress = 0

        textView.text = parseStringFileName(fileSource.absolutePath)

        while (fis.read(buffer).also { length = it } > 0) {
            fos.write(buffer, 0, length)
            total += length.toLong()

            if(lengthOfFile != 0L) {
                progress = (total * 100 / lengthOfFile).toInt()
            }

            progressBar.progress = progress

            if(progress == 100) {
                progressBar.progress = 0
            }
        }
        fis.close()
        fos.close()
    }

    fun copyFolder(fileSource: File, fileDestination: File, progressBar: ProgressBar, textView: TextView) {
        if (fileSource.isDirectory) {
            if (!fileDestination.exists()) {
                fileDestination.mkdir()
            }
            val files = fileSource.list()
            for (file in files!!) {
                val srcFile = File(fileSource, file)
                val destFile = File(fileDestination, file)
                copyFolder(srcFile, destFile, progressBar, textView)
            }
        } else {
            val fis = FileInputStream(fileSource)
            val fos = FileOutputStream(fileDestination)

            val lengthOfFile: Long = fileSource.length()

            val buffer = ByteArray(4096)
            var length: Int
            var total: Long = 0
            var progress = 0

            textView.text = parseStringFileName(fileSource.absolutePath)

            while (fis.read(buffer).also { length = it } > 0) {
                fos.write(buffer, 0, length)
                total += length.toLong()

                if(lengthOfFile != 0L) {
                    progress = (total * 100 / lengthOfFile).toInt()
                }

                progressBar.progress = progress

                if(progress == 100) {
                    progressBar.progress = 0
                }
            }
            fis.close()
            fos.close()
        }
    }

    fun getExternalCardDirectory(context: Context): String {
        val resultValue = "SD Card is not installed"
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE)
        try {
            val storageVolumeClassReflection = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = storageManager.javaClass.getMethod("getVolumeList")
            val getPath = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                storageVolumeClassReflection.getMethod("getDirectory")
            } else {
                storageVolumeClassReflection.getMethod("getPath")
            }
            val isRemovable = storageVolumeClassReflection.getMethod("isRemovable")
            val result = getVolumeList.invoke(storageManager) as Array<StorageVolume>
            result.forEach {
                if (isRemovable.invoke(it) as Boolean) {
                    return when(val invokeResult = getPath.invoke(it)) {
                        is File -> invokeResult.absolutePath

                        is String -> invokeResult

                        else -> resultValue.also {
                        }
                    }
                }
            }
        } catch (e: Throwable) { }
        return resultValue
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStorageVolumesAccessState(context: Context): List<String> {
        var listOfStorageVolumes = mutableListOf<String>()
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumes = storageManager.storageVolumes
        val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        for (storageVolume in storageVolumes) {
            var freeSpace = 0L
            var totalSpace = 0L
            val path = getPath(context, storageVolume)
            if (storageVolume.isPrimary) {
                totalSpace = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
                freeSpace = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
            } else if (path != null) {
                val file = File(path)
                freeSpace = file.freeSpace
                totalSpace = file.totalSpace
            }
            val usedSpace = totalSpace - freeSpace
            val freeSpaceStr = formatFileSize(context, freeSpace)
            val totalSpaceStr = formatFileSize(context, totalSpace)
            val usedSpaceStr = formatFileSize(context, usedSpace)
            listOfStorageVolumes.add("$totalSpaceStr/$usedSpaceStr/$freeSpaceStr")
        }
        return listOfStorageVolumes
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ServiceCast")
    fun getPath(context: Context, storageVolume: StorageVolume): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            storageVolume.directory?.absolutePath?.let { return it }
        try {
            return storageVolume.javaClass.getMethod("getPath").invoke(storageVolume) as String
        } catch (e: Exception) {
        }
        try {
            return (storageVolume.javaClass.getMethod("getPathFile").invoke(storageVolume) as File).absolutePath
        } catch (e: Exception) {
        }
        val extDirs = context.getExternalFilesDirs(null)
        for (extDir in extDirs) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val fileStorageVolume: StorageVolume = storageManager.getStorageVolume(extDir)
                ?: continue
            if (fileStorageVolume == storageVolume) {
                var file = extDir
                while (true) {
                    val parent = file.parentFile ?: return file.absolutePath
                    val parentStorageVolume = storageManager.getStorageVolume(parent)
                        ?: return file.absolutePath
                    if (parentStorageVolume != storageVolume)
                        return file.absolutePath
                    file = parent
                }
            }
        }
        try {
            val parcel = Parcel.obtain()
            storageVolume.writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            parcel.readString()
            return parcel.readString()
        } catch (e: Exception) {
        }
        return null
    }

    fun setTextForTextView(context: Context, it: String, textView: TextView) {
        if (checkTheme(context)) {
            val list = it.split("/").toTypedArray()

            val word: Spannable = SpannableString(list[1])
            word.setSpan(ForegroundColorSpan(Color.BLACK), 0, word.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

            val word2: Spannable = SpannableString("/${list[0]}")
            word.setSpan(ForegroundColorSpan(Color.parseColor("#FFBB86FC")), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            textView.setTextColor(Color.BLACK)
            textView.text = word
            textView.append(word2)
        }else {
            val list = it.split("/").toTypedArray()

            val word: Spannable = SpannableString(list[1])
            word.setSpan(ForegroundColorSpan(Color.BLACK), 0, word.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

            val word2: Spannable = SpannableString("/${list[0]}")
            word.setSpan(ForegroundColorSpan(Color.parseColor("#FFBB86FC")), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            textView.text = word
            textView.append(word2)
        }
    }

    private fun checkTheme(context: Context): Boolean {
        when(context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                return true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                return false
            }
        }
        return true
    }

    fun parseStack(_stack: String) {
        if (_stack != "") {
            val stack = _stack.subSequence(1, _stack.length - 1)
            val list = stack.split(", ").toTypedArray()
            for (i in list) {
                Model.stack.push(i)
            }
            Model.ROOT_PATH = list[list.size - 1]
        }
    }

   fun fadeInView(view: View) {
        val fadeIn: Animation = AlphaAnimation(0F, 1F)
        fadeIn.interpolator = AccelerateInterpolator()
        fadeIn.duration = 500
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
        })
        view.startAnimation(fadeIn)
    }

    fun getDateAndTime(file: String): String {
        val attr = Files.readAttributes(File(file).toPath(), BasicFileAttributes::class.java)
        val instant = attr.lastModifiedTime().toInstant()
        val zoneId = ZoneId.of(ZoneId.systemDefault().toString())
        val date = instant.atZone(zoneId).toLocalDate().toString()
        val time = instant.atZone(zoneId).toLocalTime().toString()

        return "$date  |  $time"
    }
}
