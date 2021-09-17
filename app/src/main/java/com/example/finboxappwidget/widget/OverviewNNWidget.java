package com.example.finboxappwidget.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

import com.example.finboxappwidget.service.UpdateOverviewNNService;

/**
 * Implementation of App Widget functionality.
 */
public class OverviewNNWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context) {
        final Intent intent = new Intent(context, UpdateOverviewNNService.class);
        context.startService(intent);
        PowerManager pm = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(context.getPackageName())) {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

            alarmManager.cancel(pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),60000, pendingIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}