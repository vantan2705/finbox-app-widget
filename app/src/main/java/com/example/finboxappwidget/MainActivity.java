package com.example.finboxappwidget;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView lvWidget;
    private ArrayList<WidgetItem> listWidgets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

}