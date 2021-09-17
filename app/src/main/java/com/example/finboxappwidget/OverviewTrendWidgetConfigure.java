package com.example.finboxappwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

public class OverviewTrendWidgetConfigure extends AppCompatActivity {
    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OverviewTrendWidget.updateAppWidget(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}