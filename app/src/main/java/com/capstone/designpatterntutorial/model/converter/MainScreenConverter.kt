package com.capstone.designpatterntutorial.model.converter

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
import com.capstone.designpatterntutorial.model.mainscreen.Category
import com.capstone.designpatterntutorial.model.mainscreen.Pattern
import kotlin.Throws
import org.junit.runner.RunWith
import java.util.ArrayList

/**
 * Created by gubbave on 4/27/2017.
 */
object MainScreenConverter {
    //Convert data from CategoryList Table to model
    fun convertCategoryListEntry(cursor: Cursor?): MainScreenData? {
        if (cursor == null || cursor.count <= 0) {
            return null
        }
        val mainScreenData = MainScreenData()
        val tabList = ArrayList<Category?>()
        cursor.moveToFirst()
        for (pos in 0 until cursor.count) {
            val category = Category()
            val patternList = ArrayList<Pattern?>()
            category.id = cursor.getInt(cursor.getColumnIndex(CategoryEntry.COLUMN_ID))
            category.name = cursor.getString(cursor.getColumnIndex(CategoryEntry.COLUMN_NAME))
            category.description = cursor.getString(cursor.getColumnIndex(CategoryEntry.COLUMN_DESCRIPTION))
            category.patternList = patternList
            tabList.add(category)
            cursor.moveToNext()
        }
        mainScreenData.categoryList = tabList
        return mainScreenData
    }

    //Convert data from CategoryDetails Table to model
    fun convertCategoryDetailsEntry(contentResolver: ContentResolver?, cursor: Cursor?, patternList: ArrayList<Pattern?>?) {
        if (cursor == null || cursor.count <= 0) {
            return
        }
        cursor.moveToFirst()
        for (pos in 0 until cursor.count) {
            val pattern = Pattern()
            val name = cursor.getString(cursor.getColumnIndex(DesignPatternContract.PatternEntry.COLUMN_NAME))
            pattern.id = cursor.getInt(cursor.getColumnIndex(DesignPatternContract.PatternEntry.COLUMN_ID))
            pattern.categoryId = cursor.getInt(cursor.getColumnIndex(DesignPatternContract.PatternEntry.COLUMN_CATEGORY_ID))
            pattern.name = name
            pattern.description = cursor.getString(cursor.getColumnIndex(DesignPatternContract.PatternEntry.COLUMN_DESCRIPTION))
            pattern.intent = cursor.getString(cursor.getColumnIndex(DesignPatternContract.PatternEntry.COLUMN_INTENT))
            pattern.imageName = cursor.getString(cursor.getColumnIndex(DesignPatternContract.PatternEntry.COLUMN_IMAGE_NAME))
            pattern.isFavorite = isFavoritePattern(contentResolver, name)
            patternList.add(pattern)
            cursor.moveToNext()
        }
    }

    fun isFavoritePattern(contentResolver: ContentResolver?, name: String?): Boolean {
        val selection = String.format("%s=?", FavoritePatternEntry.COLUMN_NAME)
        val selectionArgs = arrayOf(name)
        val cursor = contentResolver.query(FavoritePatternEntry.CONTENT_URI,
                FavoritePatternEntry.FAVORITE_PATTERN_COLUMNS,
                selection,
                selectionArgs,
                null)
        val result = cursor != null && cursor.count > 0
        cursor.close()
        return result
    }

    fun toFavoritePatternContentValues(pattern: Pattern?): ContentValues? {
        val cv = ContentValues()
        cv.put(FavoritePatternEntry.COLUMN_ID, pattern.getId())
        cv.put(FavoritePatternEntry.COLUMN_CATEGORY_ID, pattern.getCategoryId())
        cv.put(FavoritePatternEntry.COLUMN_NAME, pattern.getName())
        cv.put(FavoritePatternEntry.COLUMN_DESCRIPTION, pattern.getDescription())
        cv.put(FavoritePatternEntry.COLUMN_INTENT, pattern.getIntent())
        cv.put(FavoritePatternEntry.COLUMN_IMAGE_NAME, pattern.getImageName())
        return cv
    }

    //Convert data from CategoryDetails Table to model
    fun convertFavoriteScreenData(cursor: Cursor?): FavoriteScreenData? {
        val favoriteScreenData = FavoriteScreenData()
        favoriteScreenData.patternList = ArrayList()
        if (cursor == null || cursor.count <= 0) {
            return favoriteScreenData
        }
        val patternList = favoriteScreenData.patternList
        cursor.moveToFirst()
        for (pos in 0 until cursor.count) {
            val pattern = Pattern()
            pattern.id = cursor.getInt(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_ID))
            pattern.categoryId = cursor.getInt(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_CATEGORY_ID))
            pattern.name = cursor.getString(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_NAME))
            pattern.description = cursor.getString(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_DESCRIPTION))
            pattern.intent = cursor.getString(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_INTENT))
            pattern.imageName = cursor.getString(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_IMAGE_NAME))
            patternList.add(pattern)
            cursor.moveToNext()
        }
        return favoriteScreenData
    }
}