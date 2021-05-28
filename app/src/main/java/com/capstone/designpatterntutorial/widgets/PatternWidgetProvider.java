package com.capstone.designpatterntutorial.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.capstone.designpatterntutorial.R;
import com.capstone.designpatterntutorial.views.activities.HomeActivity;


/**
 * Created by gubbave on 12/29/2016.
 */
public class PatternWidgetProvider extends AppWidgetProvider {

    private String TAG = PatternWidgetProvider.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");

        if (intent.getAction().equalsIgnoreCase(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            int[] ids = intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else
            super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate()");

        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, PatternWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.fav_pattern_list_widget_layout);

            remoteViews.setRemoteAdapter(R.id.listViewWidget, intent);
            remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

            Intent clickIntent = new Intent(context, HomeActivity.class);
            PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.listViewWidget, clickPI);
            remoteViews.setOnClickPendingIntent(R.id.empty_view, clickPI);
            remoteViews.setOnClickPendingIntent(R.id.refresh_fav_list, createRefreshIntent(context, appWidgetIds));

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public PendingIntent createRefreshIntent(Context context, int[] appWidgetIds) {

        Intent refreshIntent = new Intent(context, PatternWidgetProvider.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

/*
        PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                                                    refreshIntent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);
*/

        PendingIntent clickPI = PendingIntent.getBroadcast(context, 0,
                refreshIntent,
                0);

        return clickPI;
    }
}
