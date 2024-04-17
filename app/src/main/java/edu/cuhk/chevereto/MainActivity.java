package edu.cuhk.chevereto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText emailEdit;
    EditText passwordEdit;
    Button signinBtn;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        signinBtn = findViewById(R.id.signinBtn);

        webView = findViewById(R.id.cheWeb);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                // Inject JavaScript after the page has finished loading
//                injectJavascript();
//            }
//        });
        webView.loadUrl("https://demo.chevereto.com/login");
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