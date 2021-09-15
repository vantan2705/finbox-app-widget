package com.example.finboxappwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class OverviewNNWidget extends AppWidgetProvider {

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
                            String chartBaseString = overviewJSON.getString("chartNN");
                            JSONObject chartBaseJSON = new JSONObject(chartBaseString);

                            String buy = chartBaseJSON.getString("buy");
                            String sell = chartBaseJSON.getString("sell");
                            String substract = chartBaseJSON.getString("substract");
                            String note = chartBaseJSON.getString("note");

                            float fBuy, fSell;

                            try {
                                fBuy = Float.parseFloat(buy);
                                fSell = Float.parseFloat(sell);
                            } catch (Exception e) {
                                fBuy = fSell = 0;
                            }

                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.overview_n_n_widget);
                            views.setTextViewText(R.id.txtWidgetNNNote, note);
                            views.setTextViewText(R.id.txtWidgetNNBuy, buy);
                            views.setTextViewText(R.id.txtWidgetNNSell, sell);
                            views.setTextViewText(R.id.txtWidgetNNSubstract, substract);
                            views.setImageViewBitmap(R.id.imageViewWidgetNN, chart(context, fBuy, fSell));

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
        if (MyNetworkManager.isOnline(context)) {
            OverviewNNWidget.updateAppWidget(context, appWidgetManager, appWidgetIds);
        }
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
        PieChart chart = new PieChart(context);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(buy, "Mua"));
        entries.add(new PieEntry(Math.abs(sell), "Bán"));

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(new int[] {positiveColor, negativeColor});
        dataset.setValueTextSize(30);

        PieData data = new PieData(dataset);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float sum = buy + Math.abs(sell);
                float percentage = value / sum;
                return String.format("%.01f", percentage * 100) + "%";
            }
        });
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawHoleEnabled(false);
        chart.setDrawSliceText(false);

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