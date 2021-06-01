package com.capstone.designpatterntutorial.database

import android.content.Context
import android.database.Cursor
import android.os.RemoteException
import org.junit.Assert

/**
 * Created by gubbave on 4/17/2017.
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class)
class DesignPatternProviderTest {
    private var mContext: Context? = null
    private var mContentResolver: ContentResolver? = null
    private var mProvider: DesignPatternProvider? = null
    private var mShadowContentResolver: ShadowContentResolver? = null
    @Before
    fun setup() {
        mProvider = DesignPatternProvider()
        mContext = ShadowApplication.getInstance().getApplicationContext()
        mContentResolver = mContext.getContentResolver()
        mShadowContentResolver = Shadows.shadowOf(mContentResolver)
        mProvider.onCreate()
        ShadowContentResolver.registerProviderInternal(DesignPatternContract.CONTENT_AUTHORITY, mProvider)
    }

    @Test
    fun check_database_query_from_customer_list_table() {
        val client: ContentProviderClient = mShadowContentResolver.acquireContentProviderClient(DesignPatternContract.CONTENT_AUTHORITY)
        var cursor: Cursor? = null
        try {
            cursor = client.query(CategoryEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        Assert.assertNotEquals(cursor, null)
    }
}