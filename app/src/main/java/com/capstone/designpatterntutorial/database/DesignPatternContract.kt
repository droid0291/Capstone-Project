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
import android.net.Uri
import kotlin.Throws
import org.junit.runner.RunWith

/**
 * Created by gubbave on 4/17/2017.
 */
object DesignPatternContract {
    val CONTENT_AUTHORITY: String? = "com.capstone.designpatterntutorial"
    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
    val PATH_CATEGORY: String? = "category"
    val PATH_PATTERN: String? = "pattern"
    val PATH_FAVORITE_PATTERN: String? = "favorite_pattern"
    val PATH_RECENT_PATTERN: String? = "recent_pattern"

    /* Inner class that defines the table contents of the category table */
    object CategoryEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CATEGORY)
                .build()
        val TABLE_NAME: String? = "category"
        val COLUMN_ID: String? = "id"
        val COLUMN_NAME: String? = "name"
        val COLUMN_DESCRIPTION: String? = "description"
        val CATEGORY_COLUMNS: Array<String?>? = arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_DESCRIPTION
        )

        fun buildCategoryUri(date: Long): Uri? {
            return CONTENT_URI
        }
    }

    /* Inner class that defines the table contents of the pattern table */
    object PatternEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PATTERN)
                .build()
        val TABLE_NAME: String? = "pattern"
        val COLUMN_ID: String? = "id"
        val COLUMN_CATEGORY_ID: String? = "categoryId"
        val COLUMN_NAME: String? = "name"
        val COLUMN_DESCRIPTION: String? = "description"
        val COLUMN_INTENT: String? = "intent"
        val COLUMN_IMAGE_NAME: String? = "imageName"
        val PATTERN_COLUMNS: Array<String?>? = arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_INTENT,
                COLUMN_DESCRIPTION,
                COLUMN_IMAGE_NAME,
                COLUMN_CATEGORY_ID)

        fun buildPatternUri(date: Long): Uri? {
            return CONTENT_URI
        }
    }

    /* Inner class that defines the table contents of the favorite_pattern table */
    object FavoritePatternEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_PATTERN)
                .build()
        val TABLE_NAME: String? = "favorite_pattern"
        val COLUMN_ID: String? = "id"
        val COLUMN_CATEGORY_ID: String? = "categoryId"
        val COLUMN_NAME: String? = "name"
        val COLUMN_DESCRIPTION: String? = "description"
        val COLUMN_INTENT: String? = "intent"
        val COLUMN_IMAGE_NAME: String? = "imageName"
        val FAVORITE_PATTERN_COLUMNS: Array<String?>? = arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_INTENT,
                COLUMN_DESCRIPTION,
                COLUMN_IMAGE_NAME,
                COLUMN_CATEGORY_ID
        )

        fun buildPatternUri(date: Long): Uri? {
            return CONTENT_URI
        }
    }

    /* Inner class that defines the table contents of the recent_pattern table */
    object RecentPatternEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECENT_PATTERN)
                .build()
        val TABLE_NAME: String? = "recent_pattern"
        val COLUMN_ID: String? = "id"
        val COLUMN_CATEGORY_ID: String? = "categoryId"
        val COLUMN_NAME: String? = "name"
        val COLUMN_DESCRIPTION: String? = "description"
        val COLUMN_INTENT: String? = "intent"
        val COLUMN_IMAGE_NAME: String? = "imageName"
        val RECENT_PATTERN_COLUMNS: Array<String?>? = arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_INTENT,
                COLUMN_DESCRIPTION,
                COLUMN_IMAGE_NAME,
                COLUMN_CATEGORY_ID
        )

        fun buildPatternUri(date: Long): Uri? {
            return CONTENT_URI
        }
    }
}