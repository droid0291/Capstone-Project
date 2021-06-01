package com.capstone.designpatterntutorial.presenters

import android.app.LoaderManager
import android.content.ContentResolver
import android.content.Loader
import android.os.Bundle
import com.capstone.designpatterntutorial.di.MyApplication
import com.capstone.designpatterntutorial.model.mainscreen.MainScreenData
import com.capstone.designpatterntutorial.services.MainScreenLoaderTask
import javax.inject.Inject

/**
 * Created by venugopalraog on 4/23/17.
 */
class HomePresenter @Inject constructor(var mMyApplication: MyApplication?) : LoaderManager.LoaderCallbacks<MainScreenData?> {
    @Inject
    var mContentResolver: ContentResolver? = null
    fun getPattern(loaderManager: LoaderManager?) {
        loaderManager?.initLoader(MAIN_SCREEN_DATA_LOADER_ID, null, this)?.forceLoad()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<MainScreenData?>? {
        return MainScreenLoaderTask(mMyApplication, mContentResolver)
    }

    override fun onLoadFinished(loader: Loader<MainScreenData?>?, data: MainScreenData?) {}
    override fun onLoaderReset(loader: Loader<MainScreenData?>?) {}

    companion object {
        const val MAIN_SCREEN_DATA_LOADER_ID = 100
    }

    init {
        mMyApplication?.getAppComponent()?.inject(this)
    }
}