package pl.rzeszow.swiktor.tabuteokratyczne;


import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class BiblioteczkaActivity extends AppCompatActivity {

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