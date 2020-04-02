package pl.rzeszow.swiktor.tabujw;


import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String biblioteczkaString = getIntent().getStringExtra("biblioteczka");

        WebView biblioteczkaWebView = new WebView(this);
        setContentView(biblioteczkaWebView);
        biblioteczkaWebView.loadUrl(biblioteczkaString);

    }
}