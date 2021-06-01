package com.capstone.designpatterntutorial.views.activities

import android.content.ContentResolver
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.capstone.designpatterntutorial.R
import com.capstone.designpatterntutorial.di.MyApplication
import com.capstone.designpatterntutorial.model.mainscreen.Pattern
import com.capstone.designpatterntutorial.presenters.HomePresenter
import com.capstone.designpatterntutorial.views.fragments.FavoriteListFragment
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryListFragment.onListItemClicked
import com.capstone.designpatterntutorial.views.fragments.patternfragment.PatternFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), onListItemClicked, DrawerLayoutEvent {
    private val TAG = HomeActivity::class.java.simpleName

    @Inject
    var mContentResolver: ContentResolver? = null

    @Inject
    var mHomePresenter: HomePresenter? = null

    @Inject
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var drawer: DrawerLayout? = null
    private var mAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.Companion.getAppComponent(applicationContext).inject(this)
        setContentView(R.layout.activity_main)
        drawer = findViewById<View?>(R.id.drawer_layout) as DrawerLayout
        mAdView = findViewById<View?>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)
        if (savedInstanceState == null) {
            mHomePresenter?.getPattern(loaderManager)
        }
    }

    override fun onBackPressed() {
        if (drawer?.isDrawerOpen(GravityCompat.START) == true) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /*    @Subscribe(threadMode = ThreadMode.MAIN)
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
    }*/
    fun launchFavoriteFragment() {
        Log.d(TAG, "Received Data from Database")

        //log Firebase Analytic for tracking Favorite Screen Launch
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Favorite")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Launch Favorite Screen")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStackImmediate()
        FavoriteListFragment.Companion.newInstance()?.let {
            fragmentManager.beginTransaction()
                .replace(R.id.main_content, it, FavoriteListFragment::class.java.simpleName)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commitAllowingStateLoss()
        }
    }

    override fun onItemClicked(pattern: Pattern?) {

        //log Firebase Analytic for tracking Pattern Details Screen Launch
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Pattern")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Launch Pattern Detail Screen")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, pattern.getName())
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_content, PatternFragment.Companion.newInstance(pattern), PatternFragment::class.java.simpleName)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    override fun closeDrawerLayout() {
        drawer.closeDrawers()
    }

    override fun openDrawerLayout() {
        drawer.openDrawer(Gravity.START)
    }
}