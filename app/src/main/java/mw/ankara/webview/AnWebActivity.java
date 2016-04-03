package mw.ankara.webview;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * @author masawong
 * @since 10/6/15
 */
public class AnWebActivity extends AppCompatActivity {

    private AnWebView mAwvView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_an_web);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAwvView = (AnWebView) findViewById(R.id.web_awv_view);
        mAwvView.setReceivedTitleListener(new AnWebView.ReceivedTitleListener() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                getSupportActionBar().setTitle(title);
            }
        });
        mAwvView.setUrlLoadingListener(new AnWebView.UrlLoadingListener() {
            @Override
            public boolean overrideUrlLoading(WebView view, String url) {
                return processUrlLoading(url);
            }
        });

        String url = getIntent().getDataString();
        if (!TextUtils.isEmpty(url)) {
            mAwvView.loadUrl(url);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mAwvView.canGoBack()) {
            mAwvView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAwvView.reload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAwvView.destroy();
        mAwvView = null;
    }

    @Override
    public void onStop() {
        super.onStop();

        mAwvView.stopLoading();
    }

    protected boolean processUrlLoading(String url) {
        if (!url.startsWith("http")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(getPackageName());
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            try {
                startActivity(intent);
                return true;
            } catch (ActivityNotFoundException ignored) {
            }
        }
        return false;
    }
}
