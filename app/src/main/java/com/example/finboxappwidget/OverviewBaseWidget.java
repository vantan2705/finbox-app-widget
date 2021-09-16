package com.example.finboxappwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

/**
 * Implementation of App Widget functionality.
 */
public class OverviewBaseWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(context.getPackageName())) {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            final Intent intent = new Intent(context, UpdateOverviewBaseService.class);
            final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

            alarmManager.cancel(pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),60000, pendingIntent);
        } else {
            Intent intent = new Intent(context, UpdateOverviewBaseService.class);
            context.startService(intent);
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
