package com.capstone.designpatterntutorial.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.capstone.designpatterntutorial.R;
import com.capstone.designpatterntutorial.database.DesignPatternContract.FavoritePatternEntry;
import com.capstone.designpatterntutorial.views.activities.HomeActivity;


import static android.content.ContentValues.TAG;

/**
 * Created by gubbave on 12/29/2016.
 */

public class PatternWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int appWidgetId;
    private Cursor cursor;


    public PatternWidgetViewsFactory(Context context, Intent intent) {
        mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"Widget :: onCreate");
        cursor = mContext.getContentResolver().query(FavoritePatternEntry.CONTENT_URI,
                FavoritePatternEntry.FAVORITE_PATTERN_COLUMNS,
                null, null, FavoritePatternEntry.COLUMN_NAME);
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        if (cursor != null) {
            cursor.close();
        }
        Log.d(TAG, "Widget :: onDataSetChanged");
        cursor = mContext.getContentResolver().query(FavoritePatternEntry.CONTENT_URI,
                FavoritePatternEntry.FAVORITE_PATTERN_COLUMNS,
                null, null, FavoritePatternEntry.COLUMN_NAME);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "Widget :: getViewAt : " + position);

        RemoteViews row = new RemoteViews(mContext.getPackageName(),
                R.layout.list_item_quote_widget);
        cursor.moveToPosition(position);

        row.setTextViewText(R.id.item_design_pattern_title,
                cursor.getString(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_NAME)));
        row.setTextViewText(R.id.item_design_pattern_description,
                cursor.getString(cursor.getColumnIndex(FavoritePatternEntry.COLUMN_INTENT)));

        Intent clickIntent = new Intent(mContext, HomeActivity.class);
        row.setOnClickFillInIntent(R.id.widget_row, clickIntent);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
