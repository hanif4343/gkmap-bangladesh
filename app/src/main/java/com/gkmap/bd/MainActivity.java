package com.gkmap.bd;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.gkmap.bd.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private WebView webView;

    @SuppressLint({"SetJavaScriptEnabled"})
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        // Full immersive
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        webView=binding.webView;
        setup(); webView.loadUrl("file:///android_asset/map.html");
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setup(){
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        // GPU hardware acceleration — smooth map
        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        s.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // Disable browser zoom (JS handles it)
        s.setSupportZoom(false); s.setBuiltInZoomControls(false); s.setDisplayZoomControls(false);
        s.setLoadWithOverviewMode(true); s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setAllowFileAccess(true); s.setAllowContentAccess(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollBarEnabled(false); webView.setHorizontalScrollBarEnabled(false);
        webView.addJavascriptInterface(new Bridge(),"AndroidBridge");
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){return false;}
        });
        webView.setWebChromeClient(new WebChromeClient());
    }

    public class Bridge {
        @android.webkit.JavascriptInterface
        public void showToast(String m){runOnUiThread(()->Toast.makeText(MainActivity.this,m,Toast.LENGTH_SHORT).show());}
    }

    @Override public void onBackPressed(){if(webView.canGoBack())webView.goBack();else super.onBackPressed();}
    @Override protected void onResume(){super.onResume();webView.onResume();webView.resumeTimers();}
    @Override protected void onPause(){super.onPause();webView.onPause();webView.pauseTimers();}
    @Override protected void onDestroy(){super.onDestroy();webView.destroy();}
}
