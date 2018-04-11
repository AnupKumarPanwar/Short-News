package com.newsupdates.headlines;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class NewsWebView extends AppCompatActivity {

    WebView webView;

    WebSettings webSettings;

    ProgressBar progressBar;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web_view);

        MobileAds.initialize(this,getResources().getString(R.string.admob_app_id));

        webView=(WebView)findViewById(R.id.web_view);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);

        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        webView.setWebViewClient(new WebViewClient() {

                                     @Override
                                     public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                if (showProgressBar) {
                                         progressBar.setVisibility(View.VISIBLE);
//                }
//                                         super.onPageStarted(view, url, favicon);
                                     }

                                     public void onPageFinished(WebView view, String url) {
//                if (showProgressBar) {
                                         progressBar.setVisibility(View.GONE);
//                }

                                         if (mInterstitialAd.isLoaded()) {
                                             mInterstitialAd.show();
                                         }
                                     }
                                 });


        webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        String newsUrl=getIntent().getExtras().getString("url");

        webView.loadUrl(newsUrl);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        //        super.onBackPressed();
    }
}
