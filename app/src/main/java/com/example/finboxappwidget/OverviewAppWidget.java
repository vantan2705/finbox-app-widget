package com.example.finboxappwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class OverviewAppWidget extends AppWidgetProvider {
    static final int chartColumnWidth = 160;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        String url = "https://api.finbox.vn/api/app_new/getMarketData/";
        HashMap data = new HashMap();
        data.put("day", 0);
        JSONObject jsonBody = new JSONObject(data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject marketTwoData = response.getJSONObject("marketTwoData");
                            marketTwoData.keys();
                            String overviewString = marketTwoData.getString("overview");
                            JSONObject overviewJSON = new JSONObject(overviewString);
                            String chartBaseString = overviewJSON.getString("chartBase");
                            JSONObject chartBaseJSON = new JSONObject(chartBaseString);

                            String strong = chartBaseJSON.getString("strong");
                            String weak = chartBaseJSON.getString("weak");
                            String ratio = chartBaseJSON.getString("ratio");
                            String note = chartBaseJSON.getString("note");

                            ImageLoader imageLoader = MySingleton.getInstance(context).getImageLoader();
                            imageLoader.get("https://img.nhandan.com.vn/Files/Images/2020/07/26/giai_thuong_lon-1595747403778.jpg", new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer imageResponse, boolean isImmediate) {

                                    if(imageResponse != null && imageResponse.getBitmap() != null){
                                        for (int i = 0; i<N; i++) {
                                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.overview_app_widget);
                                            views.setTextViewText(R.id.txtNote, note);
                                            views.setTextViewText(R.id.appwidget_txtIncrease, strong);
                                            views.setTextViewText(R.id.appwidget_txtNormal, weak);
                                            views.setTextViewText(R.id.appwidget_txtDecrease, ratio);

                                            views.setImageViewBitmap(R.id.imageView, imageResponse.getBitmap());

                                            appWidgetManager.updateAppWidget(appWidgetIds[i], views);

                                        }
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("widget volley error", error.getMessage());
                                }
                            });

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

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(jsonObjectRequest);
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

    public String getChart(int height) {
        String chart = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < chartColumnWidth; j++) {
                chart += ".";
            }
            chart += "\n";
        }
        return chart;
    }
}