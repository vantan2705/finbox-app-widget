package com.example.finboxappwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class OverviewSignalWidget extends AppWidgetProvider {

    private String dataUrl = "https://api.finbox.vn/api/app_new/getMarketData/";
    private String imageUrl = "https://api.finbox.vn/api/widget/chart/signal";

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
                            String chartBaseString = overviewJSON.getString("chartSignal");
                            JSONObject chartBaseJSON = new JSONObject(chartBaseString);

                            String buy = chartBaseJSON.getString("buy");
                            String sell = chartBaseJSON.getString("sell");
                            String ratio = chartBaseJSON.getString("ratio");
                            String note = chartBaseJSON.getString("note");

                            imageUrl += "?buy=" + buy + "&sell=" + sell;
                            ImageLoader imageLoader = MySingleton.getInstance(context).getImageLoader();
                            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer imageResponse, boolean isImmediate) {

                                    if(imageResponse != null && imageResponse.getBitmap() != null){
                                        for (int i = 0; i<N; i++) {
                                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.overview_signal_widget);
                                            views.setTextViewText(R.id.txtWidgetSignalNote, note);
                                            views.setTextViewText(R.id.txtWidgetSignalBuy, buy);
                                            views.setTextViewText(R.id.txtWidgetSignalSell, sell);
                                            views.setTextViewText(R.id.txtWidgetSignalRatio, ratio);
                                            views.setImageViewBitmap(R.id.imageViewWidgetSignal, imageResponse.getBitmap());
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

}