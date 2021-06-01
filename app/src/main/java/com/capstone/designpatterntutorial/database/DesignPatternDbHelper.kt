package com.capstone.designpatterntutorial.database

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
import android.preference.PreferenceManager
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
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.capstone.designpatterntutorial.R
import android.widget.TextView
import com.capstone.designpatterntutorial.views.fragments.BaseFragment
import android.webkit.WebView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.capstone.designpatterntutorial.views.fragments.patternfragment.PatternFragment
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
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Geocoder
import com.google.android.gms.common.ConnectionResult
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryListFragment.onListItemClicked
import com.capstone.designpatterntutorial.views.activities.DrawerLayoutEvent
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.AdView
import androidx.core.view.GravityCompat
import android.view.Gravity
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
import com.capstone.designpatterntutorial.database.DesignPatternProvider
import android.app.IntentService
import android.content.*
import android.util.Log
import com.capstone.designpatterntutorial.services.MainScreenLoaderTask
import com.capstone.designpatterntutorial.services.FavoriteScreenLoaderTask
import com.capstone.designpatterntutorial.RobolectricGradleTestRunner
import com.google.common.base.Charsets
import org.apache.commons.lang3.StringUtils
import kotlin.Throws
import org.junit.runner.RunWith
import java.io.InputStreamReader
import java.lang.Exception

/**
 * Created by gubbave on 4/17/2017.
 */
class DesignPatternDbHelper(private val mContext: Context?) : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {
    private val TAG = DesignPatternDbHelper::class.java.simpleName
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "Create SQLiteDatabase ")
        fillData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Upgrading sample.com.sqlitesample.database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data")
        db.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DesignPatternContract.PatternEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + FavoritePatternEntry.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + RecentPatternEntry.TABLE_NAME)
        onCreate(db)
    }

    /* Create CategoryList, CategoryDetails table and update the data from design_pattern.sql file */
    private fun fillData(d: SQLiteDatabase?) {
        try {
            val resourceId = mContext.getResources().getIdentifier("design_pattern", "raw", mContext.getPackageName())
            val stream = mContext.getResources().openRawResource(resourceId)
            val streamReader = InputStreamReader(stream, Charsets.UTF_8)
            val sqliteString = CharStreams.toString(streamReader)
            val sqlQueryList: Array<String?> = sqliteString.split(";").toTypedArray()
            for (sqlQuery in sqlQueryList) {
                if (StringUtils.isNotEmpty(sqlQuery)) {
                    Log.d(TAG, "Sql Query :: $sqlQuery")
                    d.execSQL("$sqlQuery;")
                }
            }
            stream.close()
        } catch (e: Exception) {
            Log.d(TAG, "Exception :: " + e.message)
        }
    }

    companion object {
        /*
    * This is the name of our database. Database names should be descriptive and end with the
    * .db extension.
    */
        val DATABASE_NAME: String? = "design_pattern.db"
        private const val DATABASE_VERSION = 1
    }
}