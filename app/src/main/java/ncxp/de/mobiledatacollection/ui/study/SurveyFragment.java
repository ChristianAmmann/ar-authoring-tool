package ncxp.de.mobiledatacollection.ui.study;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.datalogger.SensorDataManager;
import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.ui.studies.MoreListener;
import ncxp.de.mobiledatacollection.ui.study.adapter.SurveyAdapter;

public class SurveyFragment extends Fragment implements OptionSurveyListener {

	private RecyclerView   surveyRecyclerView;
	private SurveyAdapter  surveyAdapter;
	private StudyViewModel viewModel;
	private LinearLayout   placeHolder;

	public static SurveyFragment newInstance() {
		return new SurveyFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.configureViewModel();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.survey_fragment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		surveyRecyclerView = view.findViewById(R.id.recyclerview_survey);
		placeHolder = view.findViewById(R.id.empty_survey_placeholder);
		setupSurveyView();
	}

	private void setupSurveyView() {
		surveyRecyclerView.setHasFixedSize(true);
		surveyAdapter = new SurveyAdapter(new ArrayList<>(), this);
		surveyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		surveyRecyclerView.setAdapter(surveyAdapter);
		viewModel.getSurveys().observe(SurveyFragment.this, surveys -> {
			showPlaceHolder(surveys);
			surveyAdapter.addItems(surveys);
		});
	}

	private void configureViewModel() {
		StudyDao study = StudyDatabase.getInstance(getContext()).study();
		SurveyDao survey = StudyDatabase.getInstance(getContext()).survey();
		StudyRepository studyRepo = new StudyRepository(study);
		SurveyRepository surveyRepo = new SurveyRepository(survey);
		SensorDataManager sensorDataManager = SensorDataManager.getInstance(getContext());
		StudyViewModelFactory factory = new StudyViewModelFactory(studyRepo, surveyRepo, sensorDataManager);
		viewModel = ViewModelProviders.of(this, factory).get(StudyViewModel.class);
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

	private boolean onPopupMenuClicked(MenuItem menuItem, Survey survey) {
		switch (menuItem.getItemId()) {
			case R.id.edit:
				//TODO edit
				break;
			case R.id.delete:
				showDeleteDialog(survey);
				break;
		}
		return true;
	}

	private void showDeleteDialog(Survey survey) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(R.string.dialog_survey_delete_message);
		builder.setTitle(R.string.dialog_survey_delete_title);
		builder.setPositiveButton(R.string.dialog_survey_remove, (dialog, which) -> {
			surveyAdapter.deleteItem(survey);
			//TODO Remove in db with viewmodel
		});
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
		builder.create().show();
	}

	private void showPlaceHolder(List<Survey> surveys) {
		int visible = (surveys != null && surveys.size() > 0) ? View.INVISIBLE : View.VISIBLE;
		placeHolder.setVisibility(visible);
	}
}
