package com.capstone.designpatterntutorial.model.mainscreen

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
import kotlin.Throws
import org.junit.runner.RunWith

/**
 * Created by venugopalraog on 4/24/17.
 */
class Pattern : Parcelable {
    private var id = 0
    private var categoryId = 0
    private var name: String? = null
    private var intent: String? = null
    private var description: String? = null
    private var imageName: String? = null
    private var favorite = false

    constructor() {}
    protected constructor(`in`: Parcel?) {
        id = `in`.readInt()
        categoryId = `in`.readInt()
        name = `in`.readString()
        intent = `in`.readString()
        description = `in`.readString()
        imageName = `in`.readString()
        favorite = `in`.readByte().toInt() != 0
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getCategoryId(): Int {
        return categoryId
    }

    fun setCategoryId(categoryId: Int) {
        this.categoryId = categoryId
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getIntent(): String? {
        return intent
    }

    fun setIntent(intent: String?) {
        this.intent = intent
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getImageName(): String? {
        return imageName
    }

    fun setImageName(imageName: String?) {
        this.imageName = imageName
    }

    fun isFavorite(): Boolean {
        return favorite
    }

    fun setFavorite(favorite: Boolean) {
        this.favorite = favorite
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest.writeInt(id)
        dest.writeInt(categoryId)
        dest.writeString(name)
        dest.writeString(intent)
        dest.writeString(description)
        dest.writeString(imageName)
        dest.writeByte((if (favorite) 1 else 0) as Byte)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val pattern = o as Pattern?
        return EqualsBuilder()
                .append(id, pattern.id)
                .append(categoryId, pattern.categoryId)
                .append(name, pattern.name)
                .append(intent, pattern.intent)
                .append(description, pattern.description)
                .append(imageName, pattern.imageName)
                .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(INITIAL_ODD_NUMBER, MULTIPLIER_ODD_NUMBER)
                .append(id)
                .append(categoryId)
                .append(name)
                .append(intent)
                .append(description)
                .append(imageName)
                .toHashCode()
    }

    companion object {
        const val INITIAL_ODD_NUMBER = 17
        const val MULTIPLIER_ODD_NUMBER = 37
        val CREATOR: Creator<Pattern?>? = object : Creator<Pattern?> {
            override fun createFromParcel(`in`: Parcel?): Pattern? {
                return Pattern(`in`)
            }

            override fun newArray(size: Int): Array<Pattern?>? {
                return arrayOfNulls<Pattern?>(size)
            }
        }
    }
}