package com.khudyakovvladimir.vhfileexplorer.dependencyinjection

import android.app.Application
import android.content.Context
import com.khudyakovvladimir.vhfileexplorer.fragments.DialogCopy
import com.khudyakovvladimir.vhfileexplorer.fragments.FileListFragment
import com.khudyakovvladimir.vhfileexplorer.fragments.FileListGridFragment
import com.khudyakovvladimir.vhfileexplorer.fragments.PermissionsFragment
import com.khudyakovvladimir.vhfileexplorer.utils.FileHelper
import com.khudyakovvladimir.vhfileexplorer.utils.SharedPreferenceHelper
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [MainModule::class])
interface AppComponent {
    fun injectFileListFragment(fileListFragment: FileListFragment)
    fun injectFileListGridFragment(fileListGridFragment: FileListGridFragment)
    fun injectPermissionFragment(permissionsFragment: PermissionsFragment)
    fun injectDialogCopy(dialogCopy: DialogCopy)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

@Module
class MainModule {

    @Provides
    fun provideFileHelper(): FileHelper {
        return FileHelper()
    }

    @Provides
    fun provideSharedPreferenceHelper(application: Application): SharedPreferenceHelper {
        return SharedPreferenceHelper(application)
    }
}