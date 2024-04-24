package edu.cuhk.chevereto;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class DisplayActivity extends AppCompatActivity {


    WebView cheUploadWeb;

    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    static String chevereto_url = "http://10.0.2.2:8080";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        EdgeToEdge.enable(this);


        cheUploadWeb = findViewById(R.id.cheUploadWeb);
        WebSettings webSettings = cheUploadWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; " +
                "SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/91.0.4472.77 Mobile Safari/537.36");
        cheUploadWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
//                DisplayActivity.this.uploadFiles = filePathCallback;
//                openFileChooseProcess();
                return true;
            }
        });

        cheUploadWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                DisplayActivity.this.uploadFiles = filePathCallback;
                openFileChooseProcess();
                return true;
            }
        });

        TextView menuTextView = findViewById(R.id.menuView);
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String javascript = "javascript:" +
                        "document.getElementsByClassName('top-btn-text')[0].click();";
                cheUploadWeb.evaluateJavascript(javascript, null);
            }
        });

        TextView uploadTextView = findViewById(R.id.uploadView);
        uploadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUrl = chevereto_url + "/upload";
                cheUploadWeb.loadUrl(newUrl);
            }
        });

        cheUploadWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                if (url.contains(chevereto_url) || url.contains(chevereto_url+"/upload")) {
                    hideBottom();
//                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains(chevereto_url)) {
                    // Load the URL within the current WebView
                    view.loadUrl(url);
                    return true; // Indicate that the URL has been handled
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("WebView Error", "Error code: " + errorCode + ", Description: " + description);
            }
        });

        cheUploadWeb.loadUrl(chevereto_url);
    }

    public void hideBottom() {
        Log.d("hideBottom: 1", "123");
        try {
            String javascript = "var observer = new MutationObserver(function(mutationsList, observer) {"
                    + "var btn_menu = document.getElementsByClassName('top-btn-text')[0];"
                    + "var upload_menu = document.getElementsByClassName('top-btn-el phone-hide')[1];"
                    + "if (btn_menu && upload_menu) {"
                    + "btn_menu.style.display = 'none';"
                    + "upload_menu.style.display = 'none';"
                    + "}});"
                    + "var observerConfig = {childList: true, subtree: true, };"
                    + "observer.observe(document.body, observerConfig);";
            cheUploadWeb.evaluateJavascript(javascript, null);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            int itemCount = clipData.getItemCount();
                            Uri[] imageUris = new Uri[itemCount];
                            for (int i = 0; i < itemCount; i++) {
                                imageUris[i] = clipData.getItemAt(i).getUri();
                            }
                            if (uploadFiles != null) {
                                uploadFiles.onReceiveValue(imageUris);
                                uploadFiles = null;
                            }
                        } else {
// Single image selected
                            Uri imageUri = data.getData();
                            if (uploadFile != null) {
                                uploadFile.onReceiveValue(imageUri);
                                uploadFile = null;
                            }
                        }
                    }
                }
            }
    );

    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImageLauncher.launch(Intent.createChooser(i, "Select Picture"));
    }
    @Override
    protected void onResume() {
        super.onResume();
        cheUploadWeb.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cheUploadWeb.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (null != uploadFile) {
                    Uri result = data == null ? null
                            : data.getData();
                    uploadFile.onReceiveValue(result);
                    uploadFile = null;
                }
                if (null != uploadFiles) {
                    Uri result = data == null ? null
                            : data.getData();
                    uploadFiles.onReceiveValue(new Uri[]{result});
                    uploadFiles = null;
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (null != uploadFile) {
                    uploadFile.onReceiveValue(null);
                    uploadFile = null;
                }
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (cheUploadWeb.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){
            cheUploadWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cheUploadWeb.destroy();
        cheUploadWeb=null;
    }
}
