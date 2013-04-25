package com.MyMobileWebApp;

import android.app.Activity;
import android.os.Bundle;

// Webview imports
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;

public class MainActivity extends Activity {
  // Logging stuffs
  private static final String TAG = "MyMobileWebApp";
  private static final boolean VERBOSE = true;
  private static final boolean DEVELOPMENT = false;

  private static String url;

  private WebView web;

  private WebViewClient webClient = new WebViewClient() {

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      progressDialog.dismiss();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

      if (Uri.parse(url).getHost().equals(url)) {
        return false;
      }

      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      startActivity(intent);
      return true;
    }

    public void onPageFinished(WebView view, String url) {

    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (DEVELOPMENT) {
      url = "http://wifiip";
    } else {
      url = "https://someproductionurl";
    }

    setContentView(R.layout.main);

    web = (WebView)findViewById(R.id.web);

    web.getSettings().setJavaScriptEnabled(true);
    web.getSettings().setDomStorageEnabled(true);

    web.addJavascriptInterface(new WebAppInterface(this), "Android");

    web.setWebViewClient(webClient);

    // for debug purposes
    web.setWebChromeClient(new WebChromeClient() {
      public boolean onConsoleMessage(ConsoleMessage cm) {
        if (VERBOSE) Log.d(TAG, cm.message() + " -- From line "
                             + cm.lineNumber() + " of "
                             + cm.sourceId() );
        return true;
      }
    });

  }

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
