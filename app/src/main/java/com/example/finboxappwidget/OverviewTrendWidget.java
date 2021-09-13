package com.example.finboxappwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
public class OverviewTrendWidget extends AppWidgetProvider {

    private String dataUrl = "https://api.finbox.vn/api/app_new/getMarketData/";
    private String imageUrl = "https://api.finbox.vn/api/widget/chart/trend";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
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
                            String chartBaseString = overviewJSON.getString("chartTrend");
                            JSONObject chartBaseJSON = new JSONObject(chartBaseString);

                            String increase = chartBaseJSON.getString("increase");
                            String normal = chartBaseJSON.getString("normal");
                            String decrease = chartBaseJSON.getString("decrease");
                            String note = chartBaseJSON.getString("note");

                            float fIncrease, fNormal, fDecrease;

                            try {
                                fIncrease = Float.parseFloat(increase);
                                fNormal = Float.parseFloat(normal);
                                fDecrease = Float.parseFloat(decrease);
                            } catch (Exception e) {
                                fIncrease = fNormal = fDecrease = 0;
                            }

                            for (int i = 0; i<N; i++) {
                                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.overview_trend_widget);
                                views.setTextViewText(R.id.txtWidgetTrendNote, note);
                                views.setTextViewText(R.id.txtWidgetTrendIncrease, increase);
                                views.setTextViewText(R.id.txtWidgetTrendNormal, normal);
                                views.setTextViewText(R.id.txtWidgetTrendDecrease, decrease);
                                views.setImageViewBitmap(R.id.imageViewWidgetTrend, chart(context, fIncrease, fNormal, fDecrease));
                                appWidgetManager.updateAppWidget(appWidgetIds[i], views);
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
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public Bitmap chart(Context context, float increase, float normal, float decrease) {
        int positiveColor = ContextCompat.getColor(context, R.color.widget_overview_positive);
        int negativeColor = ContextCompat.getColor(context, R.color.widget_overview_negative);
        int normalColor = ContextCompat.getColor(context, R.color.widget_overview_normal);
        PieChart chart = new PieChart(context);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(increase, "Tăng"));
        entries.add(new PieEntry(normal, "Trung lập"));
        entries.add(new PieEntry(decrease, "Giảm"));

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(new int[] {positiveColor, normalColor, negativeColor});
        dataset.setValueTextSize(25);

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Strong");
        labels.add("Weak");
        PieData data = new PieData(dataset);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float sum = increase + normal + decrease;
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