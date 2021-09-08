package com.example.finboxappwidget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TickerDetailActivity extends AppCompatActivity {
    private EditText editTicker;
    public static String TICKER_DETAIL_URL = "https://www.finbox.vn/ticker-detail/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker_detail);

        editTicker = (EditText) findViewById(R.id.editTicker);
        editTicker.requestFocus();
        editTicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String ticker = editTicker.getText().toString();
        ticker = ticker.replaceAll("\\s+","");
        if (ticker.isEmpty()) {
            Toast.makeText(TickerDetailActivity.this, "Mã không hợp lệ. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent (Intent.ACTION_VIEW);
            intent.setData (Uri.parse(TICKER_DETAIL_URL + ticker));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(TickerDetailActivity.this, anfe.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

//        editTicker.clearFocus();
//        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        in.hideSoftInputFromWindow(editTicker.getWindowToken(), 0);
    }
}