package com.khudyakovvladimir.vhfileexplorer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper
import com.khudyakovvladimir.vhfileexplorer.utils.SharedPreferenceHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException

class FileExplorerViewModelFactory @AssistedInject constructor(
    @Assisted("application")
    var application: Application,
    var fileHelper: FileHelper,
    var sharedPreferenceHelper: SharedPreferenceHelper
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileExplorerViewModel::class.java)) {
            return FileExplorerViewModel(
                application,
                fileHelper,
                sharedPreferenceHelper
            ) as T
        }
        throw IllegalArgumentException("Unnable to construct FileExplorerViewModel")
    }

    @AssistedFactory
    interface Factory {
        fun createFileExplorerViewModel(@Assisted("application") application: Application): FileExplorerViewModelFactory
    }
}