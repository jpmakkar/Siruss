package com.example.hp2.sirus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Timetable extends AppCompatActivity {

    WebView webView;
    String url = "https://cse.gndec.ac.in/sites/default/files/TT_JAN_MAY_2018_Years_days_horizontal.html#table_49";

    void initWebView() {
        webView = (WebView) findViewById(R.id.webView3);

        // Code to show WebPage in WebView
        WebViewClient client = new WebViewClient();
        webView.setWebViewClient(client);
        webView.getSettings().setJavaScriptEnabled(true); // webview supports javascript
        webView.loadUrl(url); // Their must be Internet Connection. You must give a permission in the Manifest File for Internet

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        initWebView();
    }


}