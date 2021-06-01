package com.capstone.designpatterntutorial.presenters

import android.app.LoaderManager
import android.content.ContentResolver
import android.content.Loader
import android.os.Bundle
import com.capstone.designpatterntutorial.di.MyApplication
import com.capstone.designpatterntutorial.model.favorite.FavoriteScreenData
import com.capstone.designpatterntutorial.services.FavoriteScreenLoaderTask
import javax.inject.Inject

/**
 * Created by gubbave on 7/12/2017.
 */
class FavoritePresenter(var myApplication: MyApplication?) : LoaderManager.LoaderCallbacks<FavoriteScreenData?> {
    @Inject
    var contentResolver: ContentResolver? = null
    fun getFavoriteScreenData(loaderManager: LoaderManager?) {
        loaderManager?.initLoader(FAVOTIRE_SCREEN_DATA_LOADER_ID, null, this)?.forceLoad()
    }

    fun refreshFavoriteScreenData(loaderManager: LoaderManager?) {
        loaderManager?.restartLoader(FAVOTIRE_SCREEN_DATA_LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<FavoriteScreenData?>? {
        return FavoriteScreenLoaderTask(myApplication, contentResolver)
    }

    override fun onLoadFinished(loader: Loader<FavoriteScreenData?>?, data: FavoriteScreenData?) {}
    override fun onLoaderReset(loader: Loader<FavoriteScreenData?>?) {}

    companion object {
        private const val FAVOTIRE_SCREEN_DATA_LOADER_ID = 200
    }

    init {
        myApplication?.getAppComponent()?.inject(this)
    }
}