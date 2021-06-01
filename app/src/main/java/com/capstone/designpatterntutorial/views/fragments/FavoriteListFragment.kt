package com.capstone.designpatterntutorial.views.fragments

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
import kotlin.Throws
import org.junit.runner.RunWith

/**
 * Created by gubbave on 7/12/2017.
 */
class FavoriteListFragment : BaseFragment(), MainListAdapter.OnItemClickListener {
    private var favoriteScreenData: FavoriteScreenData? = null
    private var mAdapter: MainListAdapter? = null
    var mRecyclerView: RecyclerView? = null
    var mMessage: TextView? = null

    @Inject
    var favoritePresenter: FavoritePresenter? = null
    override fun injectFragment() {
        MyApplication.Companion.getAppComponent(MyApplication.Companion.getAppContext()).inject(this)
    }

    override fun loadFragmentArguments() {
        if (arguments != null) {
            favoriteScreenData = arguments.getParcelable(FAVORITE_FRAGMENT_DATA)
        } else {
            favoritePresenter.getFavoriteScreenData(activity.getLoaderManager())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.favorite_fragment
    }

    override fun initFragment(rootView: View?) {
        if (favoriteScreenData == null) return
        loadScreenData()
    }

    private fun loadScreenData() {
        setToolbarTitle(R.string.favorite_title)
        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        toolbar.setNavigationIcon(R.drawable.icon_navigation_menu)
        mAdapter = MainListAdapter(favoriteScreenData.getPatternList())
        mAdapter.setOnItemClickListener(this)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.setLayoutManager(linearLayoutManager)
        mRecyclerView.setAdapter(mAdapter)
        if (favoriteScreenData.getPatternList().size == 0) {
            mRecyclerView.setVisibility(View.GONE)
            mMessage.setVisibility(View.VISIBLE)
            mMessage.setText(R.string.no_favorite_msg)
        }
    }

    override fun onResume() {
        super.onResume()
        if (favoriteScreenData != null) favoritePresenter.getFavoriteScreenData(activity.getLoaderManager())
    }

    override fun onItemClick(view: View?, position: Int) {
        val pattern = favoriteScreenData.getPatternList()[position]
        pattern.isFavorite = true
        (activity as HomeActivity?).onItemClicked(pattern)
    }

    fun onFavoriteScreenEvent(event: FavoriteScreenEvent?) {
        favoriteScreenData = event.getFavoriteScreenData()
        loadScreenData()
    }

    companion object {
        private val TAG = FavoriteListFragment::class.java.simpleName
        private val FAVORITE_FRAGMENT_DATA: String? = "FavoriteFragmentData"
        fun newInstance(): Fragment? {
            return FavoriteListFragment()
        }

        fun newInstance(favoriteScreenData: FavoriteScreenData?): Fragment? {
            requireNotNull(favoriteScreenData) { "Pattern List cannot be NULL" }
            val args = Bundle()
            args.putParcelable(FAVORITE_FRAGMENT_DATA, favoriteScreenData)
            val fragment: Fragment = FavoriteListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}