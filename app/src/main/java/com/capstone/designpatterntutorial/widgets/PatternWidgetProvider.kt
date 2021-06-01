package com.capstone.designpatterntutorial.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.capstone.designpatterntutorial.R
import com.capstone.designpatterntutorial.views.activities.HomeActivity

/**
 * Created by gubbave on 12/29/2016.
 */
class PatternWidgetProvider : AppWidgetProvider() {
    private val TAG = PatternWidgetProvider::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive()")
        if (intent?.action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE, ignoreCase = true)) {
            val ids = intent?.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            onUpdate(context, AppWidgetManager.getInstance(context), ids)
        } else super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        Log.d(TAG, "onUpdate()")
        if (appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {
                val intent = Intent(context, PatternWidgetService::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
                val remoteViews = RemoteViews(context?.packageName,
                        R.layout.fav_pattern_list_widget_layout)
                remoteViews.setRemoteAdapter(R.id.listViewWidget, intent)
                remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view)
                val clickIntent = Intent(context, HomeActivity::class.java)
                val clickPI = PendingIntent.getActivity(context, 0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
                remoteViews.setPendingIntentTemplate(R.id.listViewWidget, clickPI)
                remoteViews.setOnClickPendingIntent(R.id.empty_view, clickPI)
                remoteViews.setOnClickPendingIntent(R.id.refresh_fav_list, createRefreshIntent(context, appWidgetIds))
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun createRefreshIntent(context: Context?, appWidgetIds: IntArray?): PendingIntent? {
        val refreshIntent = Intent(context, PatternWidgetProvider::class.java)
        refreshIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

/*
        PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                                                    refreshIntent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);
*/
        return PendingIntent.getBroadcast(context, 0,
                refreshIntent,
                0)
    }
}