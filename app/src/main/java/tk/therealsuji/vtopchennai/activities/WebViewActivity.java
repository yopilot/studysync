package tk.therealsuji.vtopchennai.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import tk.therealsuji.vtopchennai.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        findViewById(R.id.image_button_back).setOnClickListener(view -> finish());

        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");

        TextView titleView = findViewById(R.id.text_view_title);
        titleView.setText(title);

        WebView webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String title = getIntent().getStringExtra("title");

        // Firebase Analytics Logging
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "WebViewActivity");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, title);
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }
}
