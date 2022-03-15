package com.khudyakovvladimir.vhfileexplorer.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.khudyakovvladimir.vhfileexplorer.model.Model
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper
import com.khudyakovvladimir.vhfileexplorer.utils.SharedPreferenceHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class FileExplorerViewModel @Inject constructor(
    application: Application,
    val fileHelper: FileHelper,
    val sharedPreferenceHelper: SharedPreferenceHelper,
): AndroidViewModel(application) {

    private var listForAdapter: MutableLiveData<List<File>> = MutableLiveData()
    var progressForTextView: MutableLiveData<String> = MutableLiveData()
    var internalSpace: MutableLiveData<String> = MutableLiveData()
    var sdCardSpace: MutableLiveData<String> = MutableLiveData()

    fun getListForAdapter(): MutableLiveData<List<File>> {
        return listForAdapter
    }

    fun getProgressForTextViewNow(): MutableLiveData<String> {
        return progressForTextView
    }

    fun getInternalSpaceForTextView(): MutableLiveData<String> {
        return internalSpace
    }

    fun getSdCardSpaceForTextView(): MutableLiveData<String> {
        return sdCardSpace
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setValueToSpaces() {
        val list = fileHelper.getStorageVolumesAccessState(getApplication<Application>().applicationContext)

        createDisposable(list) {
            if(it.size > 1) {
                internalSpace.value = it[0]
                sdCardSpace.value = it[1]
            }else {
                internalSpace.value = it[0]
            }
        }

    }

    fun <T> createDisposable(item: T, func:(item: T) -> Unit): Disposable {
        return createObservableT(item)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { func(it) }
    }


    private fun <T> createObservableT(item: T): Observable<T> {
        return Observable.create { subscriber ->
            subscriber.onNext(item)
        }
    }


    fun setValueToListForAdapter (fileName: String): List<File> {
        Model.map.clear()
        val file = File(fileName)
        val list = file.listFiles()
        var result = emptyList<File>()
        val v = result.toMutableList()
        if(list != null) {
            for(f in list) {
                v.add(f)
                Model.map[f] = false
            }
        }
        result = v

        createDisposable(result) {
            listForAdapter.value = it
        }

        return result
    }
}