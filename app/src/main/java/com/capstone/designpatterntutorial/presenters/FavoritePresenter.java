package com.capstone.designpatterntutorial.presenters;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Loader;
import android.os.Bundle;

import com.capstone.designpatterntutorial.di.MyApplication;
import com.capstone.designpatterntutorial.model.events.FavoriteScreenEvent;
import com.capstone.designpatterntutorial.model.favorite.FavoriteScreenData;
import com.capstone.designpatterntutorial.services.FavoriteScreenLoaderTask;

import javax.inject.Inject;

/**
 * Created by gubbave on 7/12/2017.
 */

public class FavoritePresenter implements LoaderManager.LoaderCallbacks<FavoriteScreenData> {

    private static final int FAVOTIRE_SCREEN_DATA_LOADER_ID = 200;

    MyApplication myApplication;

    @Inject
    ContentResolver contentResolver;


    public FavoritePresenter(MyApplication application) {
        myApplication = application;
        myApplication.getAppComponent().inject(this);
    }

    public void getFavoriteScreenData(LoaderManager loaderManager) {
        loaderManager.initLoader(FAVOTIRE_SCREEN_DATA_LOADER_ID, null, this).forceLoad();
    }

    public void refreshFavoriteScreenData(LoaderManager loaderManager) {
        loaderManager.restartLoader(FAVOTIRE_SCREEN_DATA_LOADER_ID, null, this);
    }

    @Override
    public Loader<FavoriteScreenData> onCreateLoader(int id, Bundle args) {
        return new FavoriteScreenLoaderTask(myApplication, contentResolver);
    }

    @Override
    public void onLoadFinished(Loader<FavoriteScreenData> loader, FavoriteScreenData data) {
    }

    @Override
    public void onLoaderReset(Loader<FavoriteScreenData> loader) {

    }
}
