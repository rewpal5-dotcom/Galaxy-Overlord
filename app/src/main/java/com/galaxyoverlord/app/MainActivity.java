package com.galaxyoverlord.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Galaxy Overlord — offline Android build (v2.0.2)
 *
 * The whole game ships inside the APK (assets/index.html) so it needs no internet,
 * no server and shows no ads.
 *
 * v2.0.2 hardening: plain platform theme, guarded WebView creation, a visible
 * loading / error state (never a silent black screen), and file:// access flags.
 */
public class MainActivity extends Activity {

    private static final String GAME_URL = "file:///android_asset/index.html";

    private WebView web;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try { goFullscreen(); } catch (Throwable ignored) { }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        setContentView(root, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        /* visible status line, so a slow/failed load never looks like a dead app */
        status = new TextView(this);
        status.setText("Galaxy Overlord load ho raha hai…");
        status.setTextColor(Color.parseColor("#9fd6ea"));
        status.setTextSize(15f);
        status.setGravity(Gravity.CENTER);
        status.setPadding(32, 32, 32, 32);
        root.addView(status, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        try {
            web = new WebView(this);
            web.setBackgroundColor(Color.BLACK);
            web.setOverScrollMode(View.OVER_SCROLL_NEVER);
            web.setHorizontalScrollBarEnabled(false);
            web.setVerticalScrollBarEnabled(false);

            WebSettings s = web.getSettings();
            s.setJavaScriptEnabled(true);
            s.setDomStorageEnabled(true);
            s.setDatabaseEnabled(true);
            s.setAllowFileAccess(true);
            s.setAllowContentAccess(true);
            s.setAllowFileAccessFromFileURLs(true);
            s.setAllowUniversalAccessFromFileURLs(true);
            s.setMediaPlaybackRequiresUserGesture(false);
            s.setSupportZoom(false);
            s.setBuiltInZoomControls(false);
            s.setDisplayZoomControls(false);
            s.setTextZoom(100);
            s.setUseWideViewPort(false);
            s.setLoadWithOverviewMode(false);

            web.setWebChromeClient(new WebChromeClient());
            web.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (status != null) {
                        status.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    showProblem("Game load nahi hua.\n\n" + (error != null ? error.getDescription() : ""));
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    showProblem("Game load nahi hua.\n\n" + description);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return handleUrl(request.getUrl().toString());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return handleUrl(url);
                }
            });

            root.addView(web, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            if (savedInstanceState != null) {
                web.restoreState(savedInstanceState);
            } else {
                web.loadUrl(GAME_URL);
            }
        } catch (Throwable t) {
            showProblem("Is phone par WebView start nahi ho paya.\n\n" + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private void showProblem(String msg) {
        if (status == null) return;
        status.setVisibility(View.VISIBLE);
        status.setText(msg + "\n\nPhone ka Chrome / Android System WebView update karke dobara try karein.");
        if (web != null) web.setVisibility(View.GONE);
        Toast.makeText(this, "Galaxy Overlord: load problem", Toast.LENGTH_LONG).show();
    }

    private boolean handleUrl(String url) {
        if (url == null) return false;
        if (url.startsWith("file://")) return false;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ignored) { }
        return true;
    }

    private void goFullscreen() {
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController c = getWindow().getInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.systemBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) { try { goFullscreen(); } catch (Throwable ignored) { } }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (web != null) web.saveState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setTitle("Game chhodein?")
                    .setMessage("Aapka score save ho chuka hai. Bahar nikalna hai?")
                    .setPositiveButton("Haan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { finish(); }
                    })
                    .setNegativeButton("Khelte rahein", null)
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (web != null) web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) web.onResume();
        try { goFullscreen(); } catch (Throwable ignored) { }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.loadUrl("about:blank");
            web.destroy();
        }
        super.onDestroy();
    }
}
