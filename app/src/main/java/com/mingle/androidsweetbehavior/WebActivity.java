package com.mingle.androidsweetbehavior;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mingle.widget.LoadingToolBar;

/**
 * 内部网页加载器
 */
public class WebActivity extends AppCompatActivity {





    public static final String KEY_URL="key_url";

    private WebView mWebView;
    private String mUrl;

    private LoadingToolBar mLoadingToolBar;

    public static  void startActivity(Context ctx, String url){
        Intent intent=new Intent(ctx,WebActivity.class);
        intent.putExtra(KEY_URL, url);
        ctx.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webctivity);

        if(getIntent().hasExtra(KEY_URL)){
            handIntentExtra();
        }
        initView();


    }

    private void initView() {
        mWebView= (WebView) findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient());
        mLoadingToolBar= (LoadingToolBar) findViewById(R.id.toolbar);
        mLoadingToolBar.setColorScheme(R.color.triangle, R.color.rect, R.color.circle, R.color.rect);

        setSupportActionBar(mLoadingToolBar);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoadingToolBar.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mLoadingToolBar.setRefreshing(false);

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        mWebView.loadUrl(mUrl);

    }

    private void handIntentExtra() {

        mUrl=getIntent().getStringExtra(KEY_URL);

    }
}
