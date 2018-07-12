package com.example.hp2.sirus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WheatherActivity extends AppCompatActivity {

    WebView webView;
    String url = "https://weather.com/en-IN/weather/today/l/30.86,75.86";

    void initWebView() {
        webView = (WebView) findViewById(R.id.webView);

        // Code to show WebPage in WebView
        WebViewClient client = new WebViewClient();
        webView.setWebViewClient(client);
        webView.getSettings().setJavaScriptEnabled(true); // webview supports javascript
        webView.loadUrl(url); // Their must be Internet Connection. You must give a permission in the Manifest File for Internet

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheather);
        initWebView();
    }


}