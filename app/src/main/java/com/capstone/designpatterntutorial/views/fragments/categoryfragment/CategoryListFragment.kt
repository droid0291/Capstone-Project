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
import android.view.*
import androidx.fragment.app.Fragment
import com.capstone.designpatterntutorial.model.mainscreen.Pattern
import kotlin.Throws
import org.junit.runner.RunWith
import java.util.ArrayList

/**
 * Created by gubbave on 5/8/2017.
 */
class CategoryListFragment : BaseFragment(), MainListAdapter.OnItemClickListener {
    private var mPatternList: ArrayList<Pattern?>? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: MainListAdapter? = null
    override fun loadFragmentArguments() {
        if (arguments != null) {
            mPatternList = arguments.getParcelableArrayList(MAIN_SCREEN_DETAILS_FRAGMENT_DATA)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.main_screen_details
    }

    override fun initFragment(rootView: View?) {
        mRecyclerView = rootView as RecyclerView?
        if (mPatternList == null) return
        mAdapter = MainListAdapter(mPatternList)
        mAdapter.setOnItemClickListener(this)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.setLayoutManager(linearLayoutManager)
        mRecyclerView.setAdapter(mAdapter)
    }

    override fun onItemClick(view: View?, position: Int) {
        val pattern = mPatternList.get(position)
        (activity as HomeActivity?).onItemClicked(pattern)
    }

    interface onListItemClicked {
        open fun onItemClicked(pattern: Pattern?)
    }

    companion object {
        private val TAG = CategoryListFragment::class.java.simpleName
        private val MAIN_SCREEN_DETAILS_FRAGMENT_DATA: String? = "MainScreenDetailsFragmentData"
        fun newInstance(patternList: ArrayList<Pattern?>?): Fragment? {
            requireNotNull(patternList) { "Pattern List cannot be NULL" }
            val args = Bundle()
            args.putParcelableArrayList(MAIN_SCREEN_DETAILS_FRAGMENT_DATA, patternList)
            val fragment: Fragment = CategoryListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}