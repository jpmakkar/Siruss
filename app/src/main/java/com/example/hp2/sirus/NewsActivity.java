package com.example.hp2.sirus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends AppCompatActivity {

    WebView webView2;
    String url = "http://aajtak.intoday.in/";

    void initWebView(){
        webView2 = (WebView)findViewById(R.id.webView2);

        // Code to show WebPage in WebView
        WebViewClient client = new WebViewClient();
        webView2.setWebViewClient(client);
        webView2.getSettings().setJavaScriptEnabled(true); // webview supports javascript
        webView2.loadUrl(url);// Their must be Internet Connection. You must give a permission in the Manifest File for Internet

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        initWebView();
    }


}
