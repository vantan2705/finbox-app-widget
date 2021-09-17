package com.example.finboxappwidget.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.SystemClock;

import com.example.finboxappwidget.R;
import com.example.finboxappwidget.service.UpdateOverviewBaseService;

/**
 * Implementation of App Widget functionality.
 */
public class OverviewBaseWidget extends AppWidgetProvider {
    private static AlarmManager alarmManager;
    private static PendingIntent updateServiceIntent;
    public static void updateAppWidget(Context context) {
        final Intent intent = new Intent(context, UpdateOverviewBaseService.class);
        PowerManager pm = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        context.startService(intent);
        if (pm.isIgnoringBatteryOptimizations(context.getPackageName())) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            updateServiceIntent = updateServiceIntent.getService(context, 0, intent, 0);
            alarmManager.cancel(updateServiceIntent);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),60000, updateServiceIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        savePreferenceWidgetStatus(context, true);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        alarmManager.cancel(updateServiceIntent);
        savePreferenceWidgetStatus(context, false);
    }

    public void savePreferenceWidgetStatus(Context context, boolean status) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.preference_widget_status), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.preference_widget_overview_base_key), status);
        editor.apply();
    }

}
