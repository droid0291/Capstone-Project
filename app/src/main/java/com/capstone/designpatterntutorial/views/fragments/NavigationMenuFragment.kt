package com.capstone.designpatterntutorial.views.fragments

import android.Manifest
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
import android.location.Address
import android.location.Location
import android.util.Log
import android.view.*
import com.capstone.designpatterntutorial.views.adapters.NavigationMenuAdapter
import kotlin.Throws
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 * Created by gubbave on 5/19/2017.
 */
class NavigationMenuFragment : BaseFragment(), NavigationMenuAdapter.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    var mRecyclerView: RecyclerView? = null
    var mCurrentLocation: TextView? = null
    var title: TextView? = null

    @Inject
    var homePresenter: HomePresenter? = null

    @Inject
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mAdapter: NavigationMenuAdapter? = null
    private var menuList: Array<String?>?
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var mSelectedIndex = DEFAULT_SELECTED_INDEX
    override fun injectFragment() {
        MyApplication.Companion.getAppComponent(MyApplication.Companion.getAppContext()).inject(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.navigation_menu_fragment
    }

    override fun initFragmentCreation(savedInstanceState: Bundle?) {
        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        if (savedInstanceState != null) {
            mSelectedIndex = savedInstanceState.getInt(SELECTED_INDEX)
        }
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState.putInt(SELECTED_INDEX, mSelectedIndex)
        super.onSaveInstanceState(outState)
    }

    override fun initFragment(rootView: View?) {
        super.initFragment(rootView)
        rootView.setOnTouchListener(OnTouchListener { v, event -> true })
        menuList = resources.getStringArray(R.array.navigation_menu_list)
        mAdapter = NavigationMenuAdapter(menuList)
        mAdapter.setSelectedItem(mSelectedIndex)
        mAdapter.setOnItemClickListener(this)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.setLayoutManager(linearLayoutManager)
        mRecyclerView.setAdapter(mAdapter)
    }

    override fun onItemClick(view: View?, position: Int) {
        mSelectedIndex = position
        val prevSelectedIndex = mAdapter.getSelectedItem()
        if (prevSelectedIndex != position) {
            mAdapter.setSelectedItem(position)
            mAdapter.notifyItemChanged(prevSelectedIndex)
            mAdapter.notifyItemChanged(position)
        }
        (activity as HomeActivity?).closeDrawerLayout()
        if (resources.getString(R.string.design_pattern).equals(menuList.get(position), ignoreCase = true)) {
            homePresenter.getPattern(activity.getLoaderManager())
        } else if (resources.getString(R.string.favorites).equals(menuList.get(position), ignoreCase = true)) {
            (activity as HomeActivity?).launchFavoriteFragment()
        } else if (resources.getString(R.string.recents).equals(menuList.get(position), ignoreCase = true)) {
            //Todo: Implement Recent visited pattern screen.
        }
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, " onConnected ")
        //Check about the Last Known location
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission Not Granted")
            ActivityCompat.requestPermissions(activity, arrayOf<String?>(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_ACCESS_FINE_LOCATION)
            return
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        val gcd = Geocoder(activity, Locale.getDefault())
        val addresses: MutableList<Address?>?
        try {
            addresses = gcd.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1)
            if (addresses.size > 0) {
                val addr = addresses[0]
                Log.d(TAG, "Locality :: " + addr.getLocality() + " Country Name :: " + addr.getCountryName())
                val location = addr.getLocality() + ", " + addr.getCountryName()
                mCurrentLocation.setText(location)

                //log Firebase Analytic for tracking Favorite Screen Launch
                val firebaseBundle = Bundle()
                firebaseBundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NavigationMenu")
                firebaseBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment")
                firebaseBundle.putString(FirebaseAnalytics.Param.LOCATION, location)
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mGoogleApiClient.disconnect()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_ACCESS_FINE_LOCATION -> {
                Log.d(TAG, "Permission granted")
                mGoogleApiClient.reconnect()
            }
            else -> {
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, " onConnectionSuspended ")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, " onConnectionFailed ")
    }

    companion object {
        val SELECTED_INDEX: String? = "selectedIndex"
        private val TAG = NavigationMenuFragment::class.java.simpleName
        private const val DEFAULT_SELECTED_INDEX = 0
        private const val PERMISSION_ACCESS_FINE_LOCATION = 1
        fun newInstance(): NavigationMenuFragment? {
            val args = Bundle()
            val fragment = NavigationMenuFragment()
            fragment.arguments = args
            return fragment
        }
    }
}