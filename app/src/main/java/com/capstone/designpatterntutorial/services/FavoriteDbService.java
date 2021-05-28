package com.capstone.designpatterntutorial.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.util.Log;

import com.capstone.designpatterntutorial.database.DesignPatternContract.FavoritePatternEntry;
import com.capstone.designpatterntutorial.model.converter.MainScreenConverter;
import com.capstone.designpatterntutorial.model.mainscreen.Pattern;

/**
 * Created by gubbave on 7/12/2017.
 */

public class FavoriteDbService extends IntentService {

    private static final String TAG = FavoriteDbService.class.getSimpleName();

    public static final int OPERATION_ADD = 101;
    public static final int OPERATION_REMOVE = 102;
    public static final String OPERATION_FAVORITE = "favoriteOperation";
    public static final String PATTERN = "pattern";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FavoriteDbService(String name) {
        super(name);
    }

    public FavoriteDbService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int operation = intent.getIntExtra(OPERATION_FAVORITE, 0);

        switch (operation) {
            case OPERATION_ADD:
                insertFavoritePattern((Pattern) intent.getParcelableExtra(PATTERN));
                break;
            case OPERATION_REMOVE:
                deleteFavoritePattern((Pattern) intent.getParcelableExtra(PATTERN));
                break;
            default:
                Log.d(TAG, "Invalid Operation Received");
        }
    }

    private void deleteFavoritePattern(Pattern pattern) {
        if (pattern == null) {
            return;
        }
        String selection = FavoritePatternEntry.COLUMN_NAME + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = "" + pattern.getName();

        int result = getApplicationContext().getContentResolver().delete(FavoritePatternEntry.CONTENT_URI, selection, selectionArgs);
        Log.d(TAG, " Deleted Movie with Id:: " + pattern.getName() + " result:: " + result);
    }

    private void insertFavoritePattern(Pattern pattern) {
        if (pattern == null ||
                MainScreenConverter.isFavoritePattern(getApplicationContext().getContentResolver(), pattern.getName())) {
            return;
        }

        getApplicationContext().getContentResolver().insert(FavoritePatternEntry.CONTENT_URI,
                MainScreenConverter.toFavoritePatternContentValues(pattern));
    }
}