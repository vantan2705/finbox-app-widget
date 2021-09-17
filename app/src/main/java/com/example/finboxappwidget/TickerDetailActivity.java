package com.example.finboxappwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TickerDetailActivity extends AppCompatActivity {
    public ArrayAdapter<String> adapter;
    public static String TICKER_DETAIL_URL = "https://www.finbox.vn/ticker-detail/";
    AutoCompleteTextView autoCompleteTextViewTicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_detail);

        autoCompleteTextViewTicker = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTicker);
        autoCompleteTextViewTicker.setThreshold(1);
        autoCompleteTextViewTicker.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        getTickers();

        autoCompleteTextViewTicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String ticker = autoCompleteTextViewTicker.getText().toString();
                    performSearch(ticker);
                    return true;
                }
                return false;
            }
        });

        autoCompleteTextViewTicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                performSearch(selected);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void performSearch(String ticker) {
        ticker = ticker.replaceAll("\\s+","");
        if (ticker.isEmpty()) {
            Toast.makeText(TickerDetailActivity.this, "Mã không hợp lệ. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent (Intent.ACTION_VIEW);
            intent.setData (Uri.parse(TICKER_DETAIL_URL + ticker.toUpperCase()));
            try {
                autoCompleteTextViewTicker.setText("");
                startActivity(intent);
                finish();
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(TickerDetailActivity.this, anfe.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getTickers() {
        String url ="https://api.finbox.vn/api/blueprint/tickers";
        List<String> tickers = new ArrayList<String>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject tickerItem = response.getJSONObject(i);
                                String code = tickerItem.getString("code");
                                tickers.add(code);
                            }
                            adapter = new ArrayAdapter<String>(TickerDetailActivity.this, android.R.layout.simple_list_item_1, tickers);
                            autoCompleteTextViewTicker.setAdapter(adapter);
                        }
                        catch (JSONException e) {
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

        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}