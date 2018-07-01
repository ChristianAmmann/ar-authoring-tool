package ncxp.de.mobiledatacollection;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by cammann on 02/01/17.
 */
public class SurveyTestActivity extends AppCompatActivity {

	public static final String PREF_KEY = "pref_s_key";

	private WebView     webView;
	private ProgressBar progressBar;
	private String      platformId;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_survey);
		webView = findViewById(R.id.webview);
		progressBar = findViewById(R.id.progress_bar);
		platformId = "";
		if (getIntent() != null) {
			platformId = getIntent().getStringExtra(PREF_KEY);
		}
		initWebview();
	}

	void initWebview() {
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {


			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
		});
		//WebSettings webSettings = webView.getSettings();
		//webSettings.setJavaScriptEnabled(true);
		//webView.addJavascriptInterface(this, "Android");
		String url = getString(R.string.sosci_survey_url, platformId);
		webView.loadUrl(url);
		progressBar.setIndeterminate(true);
	}

	/*@JavascriptInterface
	public void finishedSurvey() {
		SharedPreferences sharedPref = this.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(PREF_KEY_SURVEY, true);
		editor.apply();
		finish();
	}*/

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
