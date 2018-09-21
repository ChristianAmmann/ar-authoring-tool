package ncxp.de.arauthoringtool;

import android.content.DialogInterface;
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
import android.webkit.JavascriptInterface;
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

import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.Survey;

public class SurveyActivity extends AppCompatActivity {

	public static final String            PREF_KEY_STUDY_SURVEYS = "pref_study_for_survey_key";
	private             Toolbar           toolbar;
	private             WebView           webView;
	private             ProgressBar       progressBar;
	private             CoordinatorLayout coordinatorLayout;
	private             Survey            currentSurvey;
	private             Study             study;
	private             Snackbar          snackbarHint;
	private             Snackbar          snackbarError;
	private             boolean           showHintSnackbar;
	private             MenuItem          editItem;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		coordinatorLayout = findViewById(R.id.webview_container);
		toolbar = findViewById(R.id.survey_toolbar);
		webView = findViewById(R.id.webview);
		progressBar = findViewById(R.id.progress_bar);
		if (getIntent() != null) {
			study = getIntent().getParcelableExtra(PREF_KEY_STUDY_SURVEYS);
			toolbar.setTitleTextColor(Color.WHITE);
			if (study != null && !study.getSurveys().isEmpty()) {
				currentSurvey = study.getSurveys().get(0);
				toolbar.setTitle(R.string.survey);
			}
		}
		setSupportActionBar(toolbar);
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
				editItem.setVisible(true);
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				snackbarError.show();
				editItem.setVisible(true);
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
		});
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		configureJavascript();
		loadUrl(currentSurvey);

	}

	private void configureJavascript() {
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(this, "Android");
	}

	private void initHintSnackbar() {
		snackbarHint = Snackbar.make(coordinatorLayout, R.string.snackbar_error_sosci_survey, Snackbar.LENGTH_INDEFINITE);
		showHintSnackbar = true;
		snackbarHint.setAction(R.string.ok, (view) -> {
			snackbarHint.dismiss();
			showHintSnackbar = false;
		});
	}

	@JavascriptInterface
	public void finishedSurvey() {
		int position = study.getSurveys().indexOf(currentSurvey);
		if (study.getSurveys().size() > position + 1 && study.getSurveys().get(position + 1) != null) {
			currentSurvey = study.getSurveys().get(position + 1);
			updateView(currentSurvey);
		} else {
			finish();
		}
	}

	private void initErrorSnackbar() {
		snackbarError = Snackbar.make(coordinatorLayout, R.string.snackbar_proband_error_sosci_survey, Snackbar.LENGTH_INDEFINITE);
		View snackbarView = snackbarError.getView();
		TextView snackTextView = snackbarView.findViewById(R.id.snackbar_text);
		snackTextView.setMaxLines(3);
		snackbarError.setAction(R.string.ok, (view) -> snackbarError.dismiss());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.survey_menu, menu);
		editItem = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit:
				showSurveyDialog(currentSurvey);
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
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
				survey.setName(name);
				survey.setDescription(description);
				survey.setProjectDirectory(projectDirectory);
				survey.setIdentifier(identifier);
				alertDialog.dismiss();
				updateView(survey);
				editItem.setVisible(false);
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
		loadUrl(survey);
	}

	private void loadUrl(Survey survey) {
		runOnUiThread(() -> {
			String url = getString(R.string.sosci_survey_url, survey.getProjectDirectory(), survey.getIdentifier(), study.getCurrentSubject().getId().toString());
			webView.loadUrl(url);
		});
	}
}
