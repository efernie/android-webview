package com.MyMobileWebApp;

// import android.app.Activity;
import android.app.*;
import android.os.Bundle;

// Webview imports
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;

// uri methods
import android.net.Uri;

// intent
import android.content.Intent;

// key events
import android.view.KeyEvent;

//context
import android.content.Context;

// log imports
import android.util.Log;
import android.webkit.ConsoleMessage;

public class MyMobileWebApp extends Activity {
  // Logging stuffs
  private static final String TAG = "MyMobileWebApp";
  private static final boolean VERBOSE = true;
  private static final boolean DEVELOPMENT = true;

  private static String url;

  private WebView web;

  private WebViewClient webClient = new WebViewClient() {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.d("MyMobileWebApp","onen link");
      if (Uri.parse(url).getHost().equals(url)) {
        return false;
      }

      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivity(intent);
      return true;
    }

    public void onPageFinished(WebView view, String url) {
      Log.d(TAG,"Finished");
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("MyMobileWebApp","on create");
    // switch the url from dev to production
    if (DEVELOPMENT) {
      url = "http://192.168.5.103:3000";
    } else {
      url = "https://someproductionurl";
    }

    setContentView(R.layout.main);

    web = (WebView)findViewById(R.id.web);

    // If you want javascript enabled!!
    web.getSettings().setJavaScriptEnabled(true);
    // For local storage/cookies
    web.getSettings().setDomStorageEnabled(true);

    // For exposing native functions to javacsript
    web.addJavascriptInterface(new WebAppInterface(this), "Android");
    Log.d("MyMobileWebApp","set client");
    web.setWebViewClient(webClient);

    // Load the site
    web.loadUrl(url);

    // for debug purposes to cathc console.log in the adb shell
    web.setWebChromeClient(new WebChromeClient() {
      public boolean onConsoleMessage(ConsoleMessage cm) {
        if (VERBOSE) Log.d(TAG, cm.message() + " -- From line "
                             + cm.lineNumber() + " of "
                             + cm.sourceId() );
        return true;
      }
    });

  }

  // WHere you can place native functions to expose them to javascript!
  public class WebAppInterface {
    Context mContext;

    WebAppInterface(Context c) {
      mContext = c;
    }
    // Stuff you want to expose
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
      web.goBack();
      return true;
    }
    // If it wasn't the Back key or there's no web page history, bubble up to the default
    // system behavior (probably exit the activity)
    return super.onKeyDown(keyCode, event);
  }
}
