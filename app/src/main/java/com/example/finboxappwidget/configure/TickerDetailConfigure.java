package com.example.finboxappwidget.configure;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import com.example.finboxappwidget.widget.TickerDetailWidget;

public class TickerDetailConfigure extends AppCompatActivity {
    private int appWidgetId;
    private int[] appWidgetIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

//        Custom configuratons
        appWidgetIds =  new int[]{ appWidgetId };
        TickerDetailWidget.updateAppWidget(this, appWidgetManager, appWidgetIds);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}