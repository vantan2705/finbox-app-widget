package com.example.finboxappwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class OverviewSignalWidget extends AppWidgetProvider {

    static String dataUrl = "https://api.finbox.vn/api/app_new/getMarketData/";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        HashMap data = new HashMap();
        data.put("day", 0);
        JSONObject jsonBody = new JSONObject(data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, dataUrl, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject marketTwoData = response.getJSONObject("marketTwoData");
                            marketTwoData.keys();
                            String overviewString = marketTwoData.getString("overview");
                            JSONObject overviewJSON = new JSONObject(overviewString);
                            String chartBaseString = overviewJSON.getString("chartSignal");
                            JSONObject chartBaseJSON = new JSONObject(chartBaseString);

                            String buy = chartBaseJSON.getString("buy");
                            String sell = chartBaseJSON.getString("sell");
                            String ratio = chartBaseJSON.getString("ratio");
                            String note = chartBaseJSON.getString("note");

                            float fBuy, fSell;

                            try {
                                fBuy = Float.parseFloat(buy);
                                fSell = Float.parseFloat(sell);
                            } catch (Exception e) {
                                fBuy = fSell = 0;
                            }

                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.overview_signal_widget);
                            views.setTextViewText(R.id.txtWidgetSignalNote, note);
                            views.setTextViewText(R.id.txtWidgetSignalBuy, buy);
                            views.setTextViewText(R.id.txtWidgetSignalSell, sell);
                            views.setTextViewText(R.id.txtWidgetSignalRatio, ratio);
                            views.setImageViewBitmap(R.id.imageViewWidgetSignal, chart(context, fBuy, fSell));

                            // Instruct the widget manager to update the widget
                            for (int appWidgetId : appWidgetIds) {
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        //Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidget(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static Bitmap chart(Context context, float buy, float sell) {
        int positiveColor = ContextCompat.getColor(context, R.color.widget_overview_positive);
        int negativeColor = ContextCompat.getColor(context, R.color.widget_overview_negative);
        BarChart chart = new BarChart(context);
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, buy));
        entries.add(new BarEntry(6f, sell));

        BarDataSet dataset = new BarDataSet(entries, "");
        dataset.setColors(new int[] {positiveColor, negativeColor});
        dataset.setValueTextSize(45);

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Buy");
        labels.add("Sell");
        BarData data = new BarData(dataset);
        data.setBarWidth(5f);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true); // value align center
        chart.setExtraTopOffset(50); // Top padding to avoid value covered

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        chart.getAxisLeft().setAxisMinimum(0f); // Đúng tỉ lệ giữa 2 cột
        chart.getAxisRight().setAxisMinimum(0f); // Đúng tỉ lệ giữa 2 cột
        chart.getAxisLeft().setTextSize(15f);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);

        chart.setDrawingCacheEnabled(true);
        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        chart.measure(View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY));
        chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());
        chart.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(chart.getDrawingCache());
        chart.setDrawingCacheEnabled(false); // clear drawing cache
        return bitmap;
    }

}