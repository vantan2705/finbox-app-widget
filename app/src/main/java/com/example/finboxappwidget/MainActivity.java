package com.example.finboxappwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView lvWidget;
    private ArrayList<WidgetItem> listWidgets;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateAllWidgets();
        setPowerWhiteListReceiver();

        setContentView(R.layout.activity_main);

        requestPermission();

//        Custom listview
        lvWidget = (ListView) findViewById(R.id.lvWidget);
        listWidgets = new ArrayList<>();

        // Ticker detail
        ComponentName componentNameTickerDetail =
                new ComponentName(MainActivity.this, TickerDetailWidget.class);
        listWidgets.add(
                new WidgetItem("Tìm kiếm mã", R.drawable.widget_ticker_detail_icon, componentNameTickerDetail)
        );

        // Chart Trend
        ComponentName componentNameOverviewTrend =
                new ComponentName(MainActivity.this, OverviewTrendWidget.class);
        listWidgets.add(
                new WidgetItem("Biểu đồ xu hướng",R.drawable.widget_overview_trend_icon, componentNameOverviewTrend)
        );

        // Chart Base
        ComponentName componentNameOverviewBase =
                new ComponentName(MainActivity.this, OverviewBaseWidget.class);
        listWidgets.add(
                new WidgetItem("Biểu đồ nền tảng",R.drawable.widget_overview_base_icon, componentNameOverviewBase)
        );

        // Chart Signal
        ComponentName componentNameOverviewSignal =
                new ComponentName(MainActivity.this, OverviewSignalWidget.class);
        listWidgets.add(
                new WidgetItem("Biểu đồ tín hiệu",R.drawable.widget_overview_signal_icon, componentNameOverviewSignal)
        );

        // Chart NN
        ComponentName componentNameOverviewNN =
                new ComponentName(MainActivity.this, OverviewNNWidget.class);
        listWidgets.add(
                new WidgetItem("Biểu đồ nước ngoài",R.drawable.widget_overview_nn_icon, componentNameOverviewNN)
        );

        WidgetAdapter adapter = new WidgetAdapter(this, R.layout.item_custom_list_view_widget,listWidgets);
        lvWidget.setAdapter(adapter);

        // End custom listview

    }

    public void requestPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Để ứng dụng cập nhật thông tin chính xác, bạn cần cho phép ứng dụng chạy nền")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                builder.create().show();
            }
        }
    }

    public void updateAllWidgets() {
        OverviewBaseWidget.updateAppWidget(context);
        OverviewNNWidget.updateAppWidget(context);
        OverviewTrendWidget.updateAppWidget(context);
        OverviewSignalWidget.updateAppWidget(context);
    }

    public void setPowerWhiteListReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                // the battery optimization whitelist changed
                updateAllWidgets();
            }
        };
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

}