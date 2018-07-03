package ncxp.de.mobiledatacollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import ncxp.de.mobiledatacollection.model.data.Survey;

/**
 * Created by cammann on 02/01/17.
 */
public class SurveyTestActivity extends AppCompatActivity {

	public static final String PREF_KEY = "pref_survey_key";

	private WebView           webView;
	private Toolbar           toolbar;
	private ProgressBar       progressBar;
	private CoordinatorLayout coordinatorLayout;
	private Survey            survey;
	private Snackbar          snackbarHint;
	private Snackbar          snackbarError;
	private boolean           showHintSnackbar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_survey);
		webView = findViewById(R.id.webview);
		progressBar = findViewById(R.id.progress_bar);
		coordinatorLayout = findViewById(R.id.webview_container);
		toolbar = findViewById(R.id.test_survey_toolbar);

		survey = new Survey();
		if (getIntent() != null) {
			survey = getIntent().getParcelableExtra(PREF_KEY);
			toolbar.setTitleTextColor(Color.WHITE);
			toolbar.setTitle(survey.getName());
		}
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(view -> this.finish());
		initHintSnackbar();
		initErrorSnackbar();
		initWebview();
	}

	void initWebview() {
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				progressBar.setVisibility(View.GONE);
				if (showHintSnackbar && !snackbarError.isShown()) {
					snackbarHint.show();
				}
			}

			@Override
			public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
				snackbarError.show();
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				snackbarError.show();
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
		});
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		loadUrl(survey);
	}

	private void initHintSnackbar() {
		snackbarHint = Snackbar.make(coordinatorLayout, R.string.snackbar_hint_sosci_survey, Snackbar.LENGTH_INDEFINITE);
		showHintSnackbar = true;
		snackbarHint.setAction(R.string.ok, (view) -> {
			snackbarHint.dismiss();
			showHintSnackbar = false;
		});
	}

	private void initErrorSnackbar() {
		snackbarError = Snackbar.make(coordinatorLayout, R.string.snackbar_error_sosci_survey, Snackbar.LENGTH_INDEFINITE);
		View snackbarView = snackbarError.getView();
		TextView snackTextView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
		snackTextView.setMaxLines(3);
		snackbarError.setAction(R.string.ok, (view) -> snackbarError.dismiss());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.test_survey_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit:
				showSurveyDialog(survey);
				break;
			case R.id.help:
				Intent intent = new Intent(this, OnboardingSurveyActivity.class);
				startActivity(intent);
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
			finish();
		}
	}

	private void showSurveyDialog(Survey survey) {
		if (snackbarError.isShown()) {
			snackbarError.dismiss();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		builder.setTitle(R.string.dialog_survey_title);
		View dialogView = inflater.inflate(R.layout.dialog_import_survey, null);
		TextInputEditText surveyTitle = dialogView.findViewById(R.id.survey_title);
		TextInputEditText surveyDescription = dialogView.findViewById(R.id.survey_description);
		TextInputEditText surveyProjectDirectory = dialogView.findViewById(R.id.survey_project_directory);
		TextInputEditText surveyIdentifier = dialogView.findViewById(R.id.survey_identifier);
		TextView surveyProjectDirectoryURL = dialogView.findViewById(R.id.survey_project_directory_url);
		TextView surveyIdentiferURL = dialogView.findViewById(R.id.survey_identifier_url);
		surveyProjectDirectory.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				surveyProjectDirectoryURL.setText(s);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		surveyIdentifier.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				surveyIdentiferURL.setText(s);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		if (survey != null) {
			surveyTitle.setText(survey.getName());
			surveyDescription.setText(survey.getDescription());
			surveyProjectDirectory.setText(survey.getProjectDirectory());
			surveyIdentifier.setText(survey.getIdentifier());
			surveyProjectDirectoryURL.setText(survey.getProjectDirectory());
			surveyIdentiferURL.setText(survey.getIdentifier());
		}
		builder.setView(dialogView).setPositiveButton(R.string.save, null).setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()));
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		positiveButton.setOnClickListener((clickedView) -> {
			boolean validInput = validateInput(surveyTitle, R.string.dialog_survey_error_title);
			validInput &= validateInput(surveyDescription, R.string.dialog_survey_error_description);
			validInput &= validateInput(surveyProjectDirectory, R.string.dialog_survey_error_project_directory);

			if (validInput) {
				String name = surveyTitle.getText().toString();
				String description = surveyDescription.getText().toString();
				String projectDirectory = surveyProjectDirectory.getText().toString();
				String identifier = surveyIdentifier.getText().toString();
				alertDialog.dismiss();
				updateView(survey);
				alertDialog.dismiss();
			}

		});
	}

	private boolean validateInput(TextInputEditText editText, int errorCode) {
		if (editText.getText().toString().isEmpty()) {
			editText.setError(getString(errorCode));
			return false;
		}
		return true;
	}

	private void updateView(Survey survey) {
		getSupportActionBar().setTitle(survey.getName());
		loadUrl(survey);
	}

	private void loadUrl(Survey survey) {
		String url = getString(R.string.sosci_survey_url, survey.getProjectDirectory(), survey.getIdentifier());
		webView.loadUrl(url);
		progressBar.setIndeterminate(true);
	}
}
