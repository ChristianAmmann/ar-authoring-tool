package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import ncxp.de.mobiledatacollection.ArImageMarkerActivity;
import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.SurveyActivity;
import ncxp.de.mobiledatacollection.model.data.TestPersonState;

public class ArStudyFragment extends Fragment {

	private ArImageMarkerViewModel viewModel;
	private ImageButton            settingsButton;
	private ImageButton            expandBottomToolbarButton;
	private ImageButton            testModusButton;
	private ImageButton            addSubjectButton;
	private ImageButton            cancelButton;
	private ImageButton            playAndPauseButton;
	private ImageButton            finishButton;
	private ImageView              timeIcon;
	private LinearLayout           bottomToolbar;
	private TestPersonState        state = TestPersonState.STOPPED;
	private TextView               studyStatusView;
	private Chronometer            chronometer;
	private ArInteractionListener  arInteractionListener;

	public static ArStudyFragment newInstance() {
		return new ArStudyFragment();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof ArImageMarkerActivity) {
			arInteractionListener = (ArInteractionListener) context;
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ar_study, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		viewModel = ArImageMarkerActivity.obtainViewModel(getActivity());
		expandBottomToolbarButton = view.findViewById(R.id.expand_bottom_toolbar_button);
		settingsButton = view.findViewById(R.id.ar_settings_button);
		addSubjectButton = view.findViewById(R.id.add_subject_button);
		bottomToolbar = view.findViewById(R.id.study_bottom_toolbar);
		playAndPauseButton = view.findViewById(R.id.play_and_pause);
		finishButton = view.findViewById(R.id.finish);
		cancelButton = view.findViewById(R.id.cancel);
		testModusButton = view.findViewById(R.id.test_modus);
		timeIcon = view.findViewById(R.id.timer_icon);
		studyStatusView = view.findViewById(R.id.study_status);
		ImageButton editButton = view.findViewById(R.id.edit_modus);
		editButton.setOnClickListener(clickedView -> {
			viewModel.setState(EditorState.EDIT_MODE);
			arInteractionListener.onEditorStateChanged();
		});
		chronometer = view.findViewById(R.id.time_view);
		chronometer.setOnChronometerTickListener(chronometerChanged -> chronometer = chronometerChanged);
		ImageButton backButton = view.findViewById(R.id.back);
		backButton.setOnClickListener(clickedView -> getActivity().finish());
		settingsButton.setOnClickListener(clickedView -> showArSettingDialog());
		addSubjectButton.setOnClickListener(this::onAddSubjectClicked);
		playAndPauseButton.setOnClickListener(this::onPlayAndPauseClicked);
		cancelButton.setOnClickListener(this::onCancelClicked);
		finishButton.setOnClickListener(this::onFinishClicked);
		initStudyBottomBar();
	}

	private void initStudyBottomBar() {
		expandBottomToolbarButton.setOnClickListener((view) -> {
			if (bottomToolbar.getVisibility() == View.GONE) {
				showStudyBottomBar();
			} else {
				hideStudyBottomBar();
			}
		});
		showStudyBottomBar();
	}

	private void showStudyBottomBar() {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) expandBottomToolbarButton.getLayoutParams();
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		expandBottomToolbarButton.setImageResource(R.drawable.chevron_down);
		bottomToolbar.setVisibility(View.VISIBLE);
	}

	private void hideStudyBottomBar() {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) expandBottomToolbarButton.getLayoutParams();
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		expandBottomToolbarButton.setImageResource(R.drawable.chevron_up);
		bottomToolbar.setVisibility(View.GONE);
	}

	private void showArSettingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		LayoutInflater inflater = getLayoutInflater();
		builder.setTitle(R.string.ar_settings);
		View dialogView = inflater.inflate(R.layout.dialog_ar_setting, null);
		Spinner selectionSpinner = dialogView.findViewById(R.id.spinner_selection);
		Spinner rotationSpinner = dialogView.findViewById(R.id.spinner_rotation);
		Spinner scaleSpinner = dialogView.findViewById(R.id.spinner_scale);
		scaleSpinner.setSelection(viewModel.getScaleTechnique().getValue().getPosition());
		rotationSpinner.setSelection(viewModel.getRotationTechnique().getValue().getPosition());
		selectionSpinner.setSelection(viewModel.getSelectionTechnique().getValue().getPosition());
		builder.setView(dialogView).setPositiveButton(R.string.apply, ((dialog, which) -> {
			viewModel.setSelectionTechnique(SelectionTechnique.getTechnique(selectionSpinner.getSelectedItemPosition()));
			viewModel.setRotationTechnique(RotationTechnique.getTechnique(rotationSpinner.getSelectedItemPosition()));
			viewModel.setScaleTechnique(ScaleTechnique.getTechnique(scaleSpinner.getSelectedItemPosition()));

		})).setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()));
		builder.create().show();
	}


	private void updateState(TestPersonState state) {
		this.state = state;
		studyStatusView.setText(state.name());
	}

	private void showStudyStartDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.are_you_ready).setMessage(R.string.subject_start_help).setPositiveButton(R.string.lets_go, (dialog, which) -> {
			hideDirectorModus();
			showSubjectModus();
			timeIcon.setImageDrawable(getContext().getDrawable(R.drawable.timer));
			chronometer.setBase(SystemClock.elapsedRealtime());
			chronometer.start();
			updateState(TestPersonState.RUNNING);
			viewModel.startSensorService();
		}).setNegativeButton(R.string.cancel, null).create().show();
	}

	private void showFinishDialog(TestPersonState currentState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.finish_study).setMessage(R.string.finish_study_hint).setPositiveButton(R.string.done, (dialog, which) -> {
			hideSubjectModus();
			showDirectorModus();
			startSurveyActivity();
		}).setNegativeButton(R.string.cancel, (dialog, which) -> {
			viewModel.startSensorService();
			updateState(currentState);
			chronometer.start();
		}).create().show();
	}

	private void startSurveyActivity() {
		Intent intent = new Intent(getActivity(), SurveyActivity.class);
		//TODO
		//intent.putExtra(SurveyActivity.PREF_KEY_STUDY_SURVEYS, study);
		//startActivity(intent);
	}

	private void onTestModusClicked(View view) {

	}

	private void showDirectorModus() {
		testModusButton.setVisibility(View.VISIBLE);
		addSubjectButton.setVisibility(View.VISIBLE);
		settingsButton.setVisibility(View.VISIBLE);
	}

	private void hideDirectorModus() {
		testModusButton.setVisibility(View.GONE);
		addSubjectButton.setVisibility(View.GONE);
		settingsButton.setVisibility(View.GONE);
	}

	private void showSubjectModus() {
		cancelButton.setVisibility(View.VISIBLE);
		playAndPauseButton.setVisibility(View.VISIBLE);
		finishButton.setVisibility(View.VISIBLE);
	}

	private void hideSubjectModus() {
		cancelButton.setVisibility(View.GONE);
		playAndPauseButton.setVisibility(View.GONE);
		finishButton.setVisibility(View.GONE);
	}

	private void showAddSubjectDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.add_subject).setMessage(getString(R.string.add_subject_hint, viewModel.getStudy().getName())).setPositiveButton(R.string.add, (dialog, which)
				-> showStudyStartDialog()).setNegativeButton(R.string.cancel, null).create().show();
	}

	private void onAddSubjectClicked(View view) {
		showAddSubjectDialog();
	}

	private void onPlayAndPauseClicked(View view) {
		if (state.equals(TestPersonState.RUNNING)) {
			state = TestPersonState.STOPPED;
			chronometer.stop();
			playAndPauseButton.setImageResource(R.drawable.play_circle_outline);
			timeIcon.setImageDrawable(getContext().getDrawable(R.drawable.timer_off));
			viewModel.stopSensorService();
		} else {
			state = TestPersonState.RUNNING;
			chronometer.start();
			viewModel.startSensorService();
			playAndPauseButton.setImageResource(R.drawable.pause_circle_outline);
		}
		updateState(state);

	}

	private void onCancelClicked(View view) {
		showCancelDialog(state);
		chronometer.stop();
		viewModel.stopSensorService();
		timeIcon.setImageDrawable(getContext().getDrawable(R.drawable.timer_off));
		updateState(TestPersonState.STOPPED);
	}

	private void showCancelDialog(TestPersonState currentState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.cancel_study).setMessage(R.string.cancel_study_hint).setPositiveButton(R.string.confirm, (dialog, which) -> {
			showDirectorModus();
			hideSubjectModus();
			chronometer.setBase(SystemClock.elapsedRealtime());
			updateState(TestPersonState.IDLE);
			viewModel.abortSensorService();
		}).setNegativeButton(R.string.no, (dialog, which) -> updateState(currentState)).create().show();
	}


	private void onFinishClicked(View view) {
		chronometer.stop();
		viewModel.stopSensorService();
		updateState(TestPersonState.STOPPED);
		showFinishDialog(state);
	}
}
