package com.capstone.designpatterntutorial.views.activities;

import android.content.ContentResolver;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;

import com.capstone.designpatterntutorial.R;
import com.capstone.designpatterntutorial.di.MyApplication;
import com.capstone.designpatterntutorial.model.events.MainScreenEvent;
import com.capstone.designpatterntutorial.model.mainscreen.MainScreenData;
import com.capstone.designpatterntutorial.model.mainscreen.Pattern;
import com.capstone.designpatterntutorial.presenters.HomePresenter;
import com.capstone.designpatterntutorial.views.fragments.FavoriteListFragment;
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryFragment;
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryListFragment;
import com.capstone.designpatterntutorial.views.fragments.patternfragment.PatternFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import timber.log.Timber;

public class HomeActivity extends AppCompatActivity implements CategoryListFragment.onListItemClicked, DrawerLayoutEvent {

    private String TAG = HomeActivity.class.getSimpleName();

    @Inject
    ContentResolver mContentResolver;

    @Inject
    HomePresenter mHomePresenter;

    @Inject
    EventBus mEventBus;

    @Inject
    FirebaseAnalytics mFirebaseAnalytics;

    private DrawerLayout drawer;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent(getApplicationContext()).inject(this);
        mEventBus.register(this);
        setContentView(R.layout.activity_main);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (savedInstanceState == null) {
            mHomePresenter.getPattern(getLoaderManager());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventBus.isRegistered(this))
            mEventBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainScreenEvent(MainScreenEvent event) {
        Timber.tag(TAG).d("Received Data from Database");

        //log Firebase Analytic for tracking MainScreen Launch
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MainScreen");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Launch MainScreen");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        MainScreenData mainScreenData = event.getMainScreenData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, CategoryFragment.newInstance(mainScreenData), CategoryFragment.class.getSimpleName())
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commitAllowingStateLoss();
    }

    public void launchFavoriteFragment() {
        Timber.tag(TAG).d("Received Data from Database");

        //log Firebase Analytic for tracking Favorite Screen Launch
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Favorite");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Launch Favorite Screen");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, FavoriteListFragment.newInstance(), FavoriteListFragment.class.getSimpleName())
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commitAllowingStateLoss();
    }

    @Override
    public void onItemClicked(Pattern pattern) {

        //log Firebase Analytic for tracking Pattern Details Screen Launch
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Pattern");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Launch Pattern Detail Screen");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, pattern.getName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, PatternFragment.newInstance(pattern), PatternFragment.class.getSimpleName())
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void closeDrawerLayout() {
        drawer.closeDrawers();
    }

    @Override
    public void openDrawerLayout() {
        drawer.openDrawer(Gravity.START);
    }
}