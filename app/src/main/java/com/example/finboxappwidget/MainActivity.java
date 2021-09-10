package com.example.finboxappwidget;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnAddOverviewChartBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddOverviewChartBase = (Button) findViewById(R.id.btnAddOverviewChartBase);

        btnAddOverviewChartBase.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                pinWidget(v);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pinWidget(View view) {
        Context context = MainActivity.this;
        AppWidgetManager appWidgetManager =
                context.getSystemService(AppWidgetManager.class);
        ComponentName myProvider =
                new ComponentName(context, OverviewBaseWidget.class);

        if (appWidgetManager.isRequestPinAppWidgetSupported()) {
            // Create the PendingIntent object only if your app needs to be notified
            // that the user allowed the widget to be pinned. Note that, if the pinning
            // operation fails, your app isn't notified.
            Intent pinnedWidgetCallbackIntent = new Intent();

            // Configure the intent so that your app's broadcast receiver gets
            // the callback successfully. This callback receives the ID of the
            // newly-pinned widget (EXTRA_APPWIDGET_ID).
            PendingIntent successCallback = PendingIntent.getBroadcast(context, 0,
                    pinnedWidgetCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback);
        }
    }

}