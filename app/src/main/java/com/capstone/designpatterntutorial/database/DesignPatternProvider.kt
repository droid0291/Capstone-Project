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
import android.view.ViewGroup
import android.view.LayoutInflater
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
import android.content.ContentProvider
import com.capstone.designpatterntutorial.database.DesignPatternProvider
import android.content.UriMatcher
import android.app.IntentService
import com.capstone.designpatterntutorial.services.MainScreenLoaderTask
import com.capstone.designpatterntutorial.services.FavoriteScreenLoaderTask
import com.capstone.designpatterntutorial.RobolectricGradleTestRunner
import android.content.ContentProviderClient
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import kotlin.Throws
import org.junit.runner.RunWith
import java.lang.RuntimeException
import java.lang.UnsupportedOperationException

/**
 * Created by gubbave on 4/17/2017.
 */
class DesignPatternProvider : ContentProvider() {
    private var mOpenHelper: DesignPatternDbHelper? = null
    override fun onCreate(): Boolean {
        mOpenHelper = DesignPatternDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String?>?, selection: String?, selectionArgs: Array<String?>?, sortOrder: String?): Cursor? {
        val cursor: Cursor?
        cursor = when (sUriMatcher.match(uri)) {
            CATEGORY -> mOpenHelper.getReadableDatabase().query(
                    CategoryEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            PATTERN -> mOpenHelper.getReadableDatabase().query(
                    DesignPatternContract.PatternEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            FAVORITE_PATTERN -> mOpenHelper.getReadableDatabase().query(
                    FavoritePatternEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            RECENT_PATTERN -> mOpenHelper.getReadableDatabase().query(
                    RecentPatternEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        cursor.setNotificationUri(context.getContentResolver(), uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        throw RuntimeException("We are not implementing getType in Design Pattern Tutorial.")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mOpenHelper.getWritableDatabase()
        val match = sUriMatcher.match(uri)
        val returnUri: Uri?
        returnUri = when (match) {
            FAVORITE_PATTERN -> {
                val _id = db.insert(FavoritePatternEntry.TABLE_NAME, null, values)
                if (_id > 0) FavoritePatternEntry.buildPatternUri(_id) else throw SQLException("Failed to insert row into $uri")
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        context.getContentResolver().notifyChange(uri, null)
        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String?>?): Int {
        var selection = selection
        val db = mOpenHelper.getWritableDatabase()
        val match = sUriMatcher.match(uri)
        val rowsDeleted: Int

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1"
        rowsDeleted = when (match) {
            FAVORITE_PATTERN -> db.delete(FavoritePatternEntry.TABLE_NAME, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (rowsDeleted != 0) {
            context.getContentResolver().notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String?>?): Int {
        return 0
    }

    companion object {
        private val sUriMatcher = buildUriMatcher()
        const val CATEGORY = 100
        const val PATTERN = 101
        const val FAVORITE_PATTERN = 102
        const val RECENT_PATTERN = 103
        private fun buildUriMatcher(): UriMatcher? {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = DesignPatternContract.CONTENT_AUTHORITY
            matcher.addURI(authority, DesignPatternContract.PATH_CATEGORY, CATEGORY)
            matcher.addURI(authority, DesignPatternContract.PATH_PATTERN, PATTERN)
            matcher.addURI(authority, DesignPatternContract.PATH_FAVORITE_PATTERN, FAVORITE_PATTERN)
            matcher.addURI(authority, DesignPatternContract.PATH_RECENT_PATTERN, RECENT_PATTERN)
            return matcher
        }
    }
}