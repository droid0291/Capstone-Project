package com.capstone.designpatterntutorial.views.fragments.categoryfragment

import javax.inject.Singleton
import com.capstone.designpatterntutorial.di.BaseModuleApplication
import com.capstone.designpatterntutorial.views.activities.HomeActivity
import com.capstone.designpatterntutorial.presenters.HomePresenter
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryFragment
import com.capstone.designpatterntutorial.views.fragments.NavigationMenuFragment
import com.capstone.designpatterntutorial.presenters.FavoritePresenter
import com.capstone.designpatterntutorial.views.fragments.FavoriteListFragment
import com.capstone.designpatterntutorial.di.AppComponent
import com.capstone.designpatterntutorial.di.MyApplication
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.content.ContentResolver
import com.google.firebase.analytics.FirebaseAnalytics
import com.capstone.designpatterntutorial.model.mainscreen.MainScreenData
import com.capstone.designpatterntutorial.model.favorite.FavoriteScreenData
import android.os.Parcelable
import android.os.Parcel
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import android.os.Parcelable.Creator
import com.capstone.designpatterntutorial.database.DesignPatternContract.CategoryEntry
import com.capstone.designpatterntutorial.model.converter.MainScreenConverter
import com.capstone.designpatterntutorial.database.DesignPatternContract.FavoritePatternEntry
import android.content.ContentValues
import androidx.recyclerview.widget.RecyclerView
import com.capstone.designpatterntutorial.R
import android.widget.TextView
import com.capstone.designpatterntutorial.views.fragments.BaseFragment
import android.webkit.WebView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.capstone.designpatterntutorial.views.fragments.patternfragment.PatternFragment
import android.content.Intent
import com.capstone.designpatterntutorial.services.FavoriteDbService
import android.os.Bundle
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryFragment.MainScreenAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryListFragment
import com.capstone.designpatterntutorial.views.adapters.MainListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject
import com.capstone.designpatterntutorial.model.events.FavoriteScreenEvent
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import android.view.View.OnTouchListener
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Geocoder
import com.google.android.gms.common.ConnectionResult
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryListFragment.onListItemClicked
import com.capstone.designpatterntutorial.views.activities.DrawerLayoutEvent
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.AdView
import androidx.core.view.GravityCompat
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.capstone.designpatterntutorial.widgets.PatternWidgetViewsFactory
import android.appwidget.AppWidgetProvider
import com.capstone.designpatterntutorial.widgets.PatternWidgetProvider
import android.appwidget.AppWidgetManager
import com.capstone.designpatterntutorial.widgets.PatternWidgetService
import android.widget.RemoteViews
import android.app.PendingIntent
import com.capstone.designpatterntutorial.database.DesignPatternContract
import android.provider.BaseColumns
import com.capstone.designpatterntutorial.database.DesignPatternContract.RecentPatternEntry
import android.database.sqlite.SQLiteOpenHelper
import com.capstone.designpatterntutorial.database.DesignPatternDbHelper
import android.database.sqlite.SQLiteDatabase
import com.google.common.io.CharStreams
import android.content.ContentProvider
import com.capstone.designpatterntutorial.database.DesignPatternProvider
import android.content.UriMatcher
import android.app.IntentService
import com.capstone.designpatterntutorial.services.MainScreenLoaderTask
import com.capstone.designpatterntutorial.services.FavoriteScreenLoaderTask
import com.capstone.designpatterntutorial.RobolectricGradleTestRunner
import android.content.ContentProviderClient
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.capstone.designpatterntutorial.commons.Constants
import com.capstone.designpatterntutorial.model.mainscreen.Category
import kotlin.Throws
import org.junit.runner.RunWith

/**
 * Created by gubbave on 5/2/2017.
 */
class CategoryFragment : BaseFragment(), OnTabSelectedListener, View.OnClickListener {
    private var mainScreenData: MainScreenData? = null
    var mTabLayout: TabLayout? = null
    var mViewPager: ViewPager? = null
    private var mMainScreenAdapter: MainScreenAdapter? = null
    override fun loadFragmentArguments() {
        if (arguments != null) {
            mainScreenData = arguments.getParcelable(MAIN_SCREEN_DATA)
        }
    }

    override fun injectFragment() {
        MyApplication.Companion.getAppComponent(MyApplication.Companion.getAppContext()).inject(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.main_fragment
    }

    override fun initFragment(rootView: View?) {
        toolbar.setNavigationIcon(R.drawable.icon_navigation_menu)
        mMainScreenAdapter = MainScreenAdapter(childFragmentManager, mainScreenData.getCategoryList())
        mViewPager.setAdapter(mMainScreenAdapter)
        mTabLayout.setupWithViewPager(mViewPager)
        setToolbarTitle(R.string.category_title)
    }

    fun setupTabs() {
        for (tabDetails in mainScreenData.getCategoryList()) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabDetails.name))
        }
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL)
        mTabLayout.setOnTabSelectedListener(this)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        mViewPager.setCurrentItem(tab.getPosition())
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onClick(v: View?) {}
    private inner class MainScreenAdapter(fm: FragmentManager?, var mCategoryList: MutableList<Category?>?) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int {
            return mCategoryList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return getItemAtPosition(position).getName()
        }

        fun getItemAtPosition(position: Int): Category? {
            return mCategoryList.get(position)
        }

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            val category = mCategoryList.get(position)
            when (category.getName()) {
                Constants.CREATIONAL_CATEGORY_NAME, Constants.STRUCTURAL_CATEGORY_NAME, Constants.BEHAVIORAL_CATEGORY_NAME -> fragment = CategoryListFragment.Companion.newInstance(category.getPatternList())
                else -> Log.d(TAG, " Invalid Category Name Received... " + category.getName())
            }
            return fragment
        }
    }

    companion object {
        private val TAG = CategoryFragment::class.java.simpleName
        private val MAIN_SCREEN_DATA: String? = "MainScreenData"
        fun newInstance(mainScreenData: MainScreenData?): Fragment? {
            requireNotNull(mainScreenData) { "MainScreenData cannot be NULL" }
            val args = Bundle()
            args.putParcelable(MAIN_SCREEN_DATA, mainScreenData)
            val fragment: Fragment = CategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}