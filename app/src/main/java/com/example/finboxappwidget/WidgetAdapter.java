package com.example.finboxappwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class WidgetAdapter extends BaseAdapter {
    private Context context;
    private int idLayout;
    private List<WidgetItem> listWidgets;
    private int positionSelect = -1;

    public WidgetAdapter(Context context, int idLayout, List<WidgetItem> listWidgets) {
        this.context = context;
        this.idLayout = idLayout;
        this.listWidgets = listWidgets;
    }

    @Override
    public int getCount() {
        if (this.listWidgets.size() != 0 && !this.listWidgets.isEmpty()) {
            return this.listWidgets.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(idLayout, parent, false);
        }

        TextView tvWidgetName = (TextView) convertView.findViewById(R.id.tvWidgetItemName);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewWidgetItemLogo);
        final LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayoutWidgetItem);
        final WidgetItem widget = listWidgets.get(position);

        if (listWidgets != null && !listWidgets.isEmpty()) {
            tvWidgetName.setText(widget.getName());
            imageView.setImageResource(widget.getImageId());
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppWidgetManager appWidgetManager =
                        context.getSystemService(AppWidgetManager.class);
                ComponentName myProvider = widget.getComponentName();

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
        });

        if (positionSelect == position) {
            linearLayout.setBackgroundColor(Color.BLUE);
        } else {
            linearLayout.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}