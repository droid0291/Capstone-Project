package com.capstone.designpatterntutorial.di;

import android.app.Application;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.capstone.designpatterntutorial.model.converter.MainScreenConverter;
import com.capstone.designpatterntutorial.presenters.FavoritePresenter;
import com.capstone.designpatterntutorial.presenters.HomePresenter;
import com.google.firebase.analytics.FirebaseAnalytics;


import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by venugopalraog on 4/27/17.
 */
@Module
public class BaseModuleApplication {

    MyApplication mMyApplication;

    public BaseModuleApplication(MyApplication myApplication) {
        mMyApplication = myApplication;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mMyApplication;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    ContentResolver providesContentResolver(Application application) {
        return application.getContentResolver();
    }

    @Provides
    @Singleton
    MainScreenConverter providesMainScreenConverter(Application application) {
        return new MainScreenConverter();
    }

    @Provides
    @Singleton
    HomePresenter providesHomePresenter() {
        return new HomePresenter(mMyApplication);
    }

    @Provides
    @Singleton
    FavoritePresenter providesFavoritePresenter() {
        return new FavoritePresenter(mMyApplication);
    }

    @Provides
    FirebaseAnalytics provideFirebaseAnalytics() {
        return FirebaseAnalytics.getInstance(mMyApplication);
    }
}