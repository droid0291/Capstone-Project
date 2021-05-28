package com.capstone.designpatterntutorial.di;

import android.app.Application;
import android.content.Context;

/**
 * Created by venugopalraog on 4/27/17.
 */

public class MyApplication extends Application {

    private static Context appContext;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        appComponent = DaggerAppComponent.builder()
                .baseModuleApplication(new BaseModuleApplication(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static AppComponent getAppComponent(Context context) {
        return ((MyApplication) context).getAppComponent();
    }

    public static Context getAppContext() {
        return appContext;
    }

}
