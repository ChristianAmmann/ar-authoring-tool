package ncxp.de.arauthoringtool.ui.study;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.StudyActivity;
import ncxp.de.arauthoringtool.SurveyTestActivity;
import ncxp.de.arauthoringtool.model.data.Survey;
import ncxp.de.arauthoringtool.ui.study.adapter.SurveyAdapter;
import ncxp.de.arauthoringtool.viewmodel.StudyViewModel;

public class SurveyFragment extends Fragment implements OptionSurveyListener {

	private RecyclerView         surveyRecyclerView;
	private SurveyAdapter        surveyAdapter;
	private StudyViewModel       viewModel;
	private LinearLayout         placeHolder;
	private FloatingActionButton fab;

	public static SurveyFragment newInstance() {
		return new SurveyFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = StudyActivity.obtainViewModel(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_survey, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		surveyRecyclerView = view.findViewById(R.id.recyclerview_survey);
		placeHolder = view.findViewById(R.id.empty_survey_placeholder);
		fab = view.findViewById(R.id.fab_survey);
		fab.setOnClickListener(clickedView -> showSurveyDialog());
		setupSurveyView();
	}

	private void setupSurveyView() {
		surveyRecyclerView.setHasFixedSize(true);
		surveyAdapter = new SurveyAdapter(new ArrayList<>(), this);
		surveyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		surveyRecyclerView.setAdapter(surveyAdapter);
		viewModel.getSurveys().observe(SurveyFragment.this, surveys -> {
			showPlaceHolder(surveys);
			surveyAdapter.replaceItems(surveys);
		});
		viewModel.init();
	}

	@Override
	public void onPopupMenuClick(View view, Survey survey) {
		PopupMenu popupMenu = new PopupMenu(getContext(), view);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.survey_popup_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(item -> onPopupMenuClicked(item, survey));
		popupMenu.show();
	}

	@Override
	public void onTestButtonClick(View view, Survey survey) {
		Intent intent = new Intent(getActivity(), SurveyTestActivity.class);
		intent.putExtra(SurveyTestActivity.PREF_KEY, survey);
		startActivity(intent);
	}

	private boolean onPopupMenuClicked(MenuItem menuItem, Survey survey) {
		switch (menuItem.getItemId()) {
			case R.id.edit:
				showSurveyDialog(survey);
				break;
			case R.id.delete:
				showDeleteDialog(survey);
				break;
		}
		return true;
	}

	private void showSurveyDialog() {
		showSurveyDialog(null);
	}

	private void showSurveyDialog(Survey survey) {
		//TODO Custom Dialog Refactor
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
				if (survey != null) {
					survey.setName(name);
					survey.setDescription(description);
					survey.setProjectDirectory(projectDirectory);
					survey.setIdentifier(identifier);
					surveyAdapter.updateItem(survey);
				} else {
					viewModel.createSurvey(name, description, projectDirectory, identifier);
				}
				showPlaceHolder(false);
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


	private void showDeleteDialog(Survey survey) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(R.string.dialog_survey_delete_message);
		builder.setTitle(R.string.dialog_survey_delete_title);
		builder.setPositiveButton(R.string.dialog_survey_remove, (dialog, which) -> {
			surveyAdapter.deleteItem(survey);
			viewModel.removeSurvey(survey);
		});
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
		builder.create().show();
	}

	private void showPlaceHolder(List<Survey> surveys) {
		int visible = (surveys != null && surveys.size() > 0) ? View.INVISIBLE : View.VISIBLE;
		placeHolder.setVisibility(visible);
	}

	private void showPlaceHolder(boolean visible) {
		if (visible) {
			placeHolder.setVisibility(View.VISIBLE);
		} else {
			placeHolder.setVisibility(View.GONE);
		}
	}
}
