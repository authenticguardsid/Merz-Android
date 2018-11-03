package com.realmattersid;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MenuProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_news);
        String mprofile = getIntent().getStringExtra("mkind");
        WebView webView = (WebView) findViewById(R.id.NewsWebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.clearCache(true);
        webView.loadUrl("http://control.zodapos.com/core/api/menu_profile.class.php?action="+mprofile);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressBar mProgressBar = findViewById(R.id.progressProfile);
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }, 3000);
    }
}