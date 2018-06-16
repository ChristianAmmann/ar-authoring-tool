package ncxp.de.mobiledatacollection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by cammann on 02/01/17.
 */
public class SurveyTestActivity extends AppCompatActivity {

	//TODO Plattform Id
	private static final String SURVEY_URL      = "https://www.soscisurvey.de/PdF-Interruptibility/?q=qnr2&r=";
	public static final  String PREF_KEY        = "pref_s_key";
	public static final  String PREF_KEY_SURVEY = "pref_key_survey";

	private WebView webView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		webView = findViewById(R.id.webview);
		initWebview();
	}

	void initWebview() {
		ProgressDialog progressDialog = ProgressDialog.show(this, "Öffnen der Umfrage", "Ladet ...", true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		});
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(this, "Android");
		// TODO Probanden id
		String id = "testpersom";
		webView.loadUrl(SURVEY_URL + id);
	}

	@JavascriptInterface
	public void finishedSurvey() {
		SharedPreferences sharedPref = this.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(PREF_KEY_SURVEY, true);
		editor.apply();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
