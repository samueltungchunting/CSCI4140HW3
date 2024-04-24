package edu.cuhk.chevereto;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.CookieManager;
import android.webkit.WebSettings;

import java.util.Objects;
import android.webkit.CookieManager;


public class MainActivity extends AppCompatActivity {

    EditText emailEdit;
    EditText passwordEdit;
    Button signinBtn;
    WebView webView;

    String chevereto_url = "http://10.0.2.2:8080";
    private void checkCookieExist() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieString = cookieManager.getCookie(chevereto_url);

        if(cookieString != null && cookieString.contains("sessionid")) {
            Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkCookieExist();

        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        signinBtn = findViewById(R.id.signinBtn);

        webView = findViewById(R.id.cheWeb);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
//        webSettings.setAppCacheEnabled(false);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);


        webView.loadUrl(chevereto_url + "/login");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, "current over url:"+url);
                if (!Objects.equals(url, chevereto_url+"login")) {
                    Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                    startActivity(intent);
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onLoadResource(WebView view, String url){
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    public void injectJavascript() {
        webView.evaluateJavascript(
                "document.querySelector(\"#login > div.display-flex.height-min-full > div.flex-center > div > div > form > fieldset > div:nth-child(1) > input\").value = '" + emailEdit.getText().toString() + "';" +
                        "document.querySelector(\"#login > div.display-flex.height-min-full > div.flex-center > div > div > form > fieldset > div.input-with-button > input\").value = '" + passwordEdit.getText().toString() + "';" +
                        "document.querySelector(\"#login > div.display-flex.height-min-full > div.flex-center > div > div > form > fieldset > div.input-with-button > button\").click()",
                null
        );
    }

    public void handleLogin(View view) {
        injectJavascript();
    }

}