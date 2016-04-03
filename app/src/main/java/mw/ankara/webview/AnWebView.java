package mw.ankara.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 用于控制WebView的View
 * 由于使用WebView的类型太多，如Activity，Fragment，DialogFragment等
 * 因此将WebView的功能独立出来作为一个View
 *
 * @author MasaWong
 * @date 14/12/29.
 */
public class AnWebView extends WebView {

    public static final String K_URL = "url";

    private ReceivedTitleListener mReceivedTitleListener;
    private PageFinishedListener mPageFinishedListener;
    private ReceivedErrorListener mReceivedErrorListener;
    private UrlLoadingListener mUrlLoadingListener;
    private JsAlertListener mJsAlertListener;

    private View mErrorView;

    public AnWebView(Context context) {
        super(context);
        init();
    }

    public AnWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 构造函数，配置WebView
     */
    public void init() {
        // clearView() is deprecated, but onBackPressed returns to about:blank
        clearView();
        setWebViewClient();
        setWebChromeClient();
        setWebViewSettings();
        setHorizontalScrollBarEnabled(false);
    }

    /**
     * 配置WebView参数
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void setWebViewSettings() {
        WebSettings settings = getSettings();

        // User settings
        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(false);

        // Technical settings
        settings.setSupportMultipleWindows(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
    }

    /**
     * 配置WebViewClient来处理网页加载的各种状态
     */
    protected void setWebViewClient() {
        // open external url should setWebViewClient
        WebViewClient webClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                getSettings().setBlockNetworkImage(false);
                if (mPageFinishedListener != null) {
                    mPageFinishedListener.overridePageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                if (mErrorView != null) {
                    mErrorView.setVisibility(VISIBLE);
                }

                if (mReceivedErrorListener != null) {
                    mReceivedErrorListener.overrideReceivedError(view, errorCode,
                            description, failingUrl);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return mUrlLoadingListener != null &&
                        mUrlLoadingListener.overrideUrlLoading(view, url);
            }
        };
        setWebViewClient(webClient);
    }

    /**
     * 配置WebChromeClient来处理JsAlert，用于从网页取得一些复杂的数据
     */
    protected void setWebChromeClient() {
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (mReceivedTitleListener != null) {
                    mReceivedTitleListener.onReceivedTitle(view, title);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return mJsAlertListener != null &&
                        mJsAlertListener.overrideJsAlert(view, url, message, result);
            }
        };
        setWebChromeClient(webChromeClient);
    }

    public void setErrorView(View errorView) {
        mErrorView = errorView;
    }

    /**
     * 加载Url
     *
     * @param url 需要加载的url
     */
    @Override
    public void loadUrl(String url) {
        getSettings().setBlockNetworkImage(true);
        if (mErrorView != null) {
            mErrorView.setVisibility(GONE);
        }
        super.loadUrl(url);
    }

    public void setReceivedTitleListener(ReceivedTitleListener receivedTitleListener) {
        mReceivedTitleListener = receivedTitleListener;
    }

    public void setPageFinishedListener(PageFinishedListener pageFinishedListener) {
        mPageFinishedListener = pageFinishedListener;
    }

    public void setReceivedErrorListener(ReceivedErrorListener receivedErrorListener) {
        mReceivedErrorListener = receivedErrorListener;
    }

    public void setUrlLoadingListener(UrlLoadingListener urlLoadingListener) {
        mUrlLoadingListener = urlLoadingListener;
    }

    public void setJsAlertListener(JsAlertListener jsAlertListener) {
        mJsAlertListener = jsAlertListener;
    }

    public static interface ReceivedTitleListener {
        public void onReceivedTitle(WebView view, String title);
    }

    public static interface PageFinishedListener {
        public void overridePageFinished(WebView view, String url);
    }

    public static interface ReceivedErrorListener {
        public void overrideReceivedError(WebView view, int errorCode, String description,
                                          String failingUrl);
    }

    public static interface UrlLoadingListener {
        public boolean overrideUrlLoading(WebView view, String url);
    }

    public static interface JsAlertListener {
        public boolean overrideJsAlert(WebView view, String url, String message, JsResult result);
    }
}
