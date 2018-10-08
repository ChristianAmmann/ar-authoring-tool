package ncxp.de.arauthoringtool.view.studies;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.arauthoringtool.view.arscene.ArSceneActivity;
import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.view.study.StudyActivity;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.viewmodel.StudiesViewModel;
import ncxp.de.arauthoringtool.viewmodel.util.ToastMessage;

public class StudiesFragment extends Fragment implements StudyListener {

	private static final int EXTERNAL_PERMISSION_CODE = 4001;

	private StudiesViewModel viewModel;
	private RecyclerView     studiesView;
	private StudiesAdapter   studiesAdapter;
	private LinearLayout     placeHolder;
	private Study            exportStudy;

	public static StudiesFragment newInstance() {
		return new StudiesFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = StudiesActivity.obtainViewModel(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_studies, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		studiesView = view.findViewById(R.id.studies_recyclerview);
		placeHolder = view.findViewById(R.id.empty_study_placeholder);
		setupStudiesView();
		setupToast();
	}

	private void setupStudiesView() {
		studiesView.setHasFixedSize(true);
		studiesAdapter = new StudiesAdapter(new ArrayList<>(), this);
		studiesView.setLayoutManager(new LinearLayoutManager(getContext()));
		studiesView.setAdapter(studiesAdapter);
		viewModel.getStudies().observe(StudiesFragment.this, studies -> {
			showPlaceHolder(studies);
			studiesAdapter.replaceItems(studies);
		});
		viewModel.init();
	}

	private void setupToast() {
		viewModel.getToastMessage().observe(this, (ToastMessage.ToastObserver) text -> Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show());
	}

	@Override
	public void onPopupMenuClick(View view, Study study) {
		PopupMenu popupMenu = new PopupMenu(getContext(), view);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.card_popup_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(menuItem -> onPopupMenuItemClicked(menuItem, study));
		popupMenu.show();

	}

	@Override
	public void onStudyStartClick(Study study) {
		Intent intent = new Intent(getActivity(), ArSceneActivity.class);
		intent.putExtra(ArSceneActivity.KEY_STUDY, study);
		getActivity().startActivity(intent);
	}

	private boolean onPopupMenuItemClicked(MenuItem menuItem, Study study) {
		switch (menuItem.getItemId()) {
			case R.id.export:
				exportStudy = study;
				if (isExternalPermissionForDatabaseGranted()) {
					viewModel.exportStudyAsCSV(study);
					exportStudy = null;
				}
				break;
			case R.id.edit:
				Intent intent = new Intent(getActivity(), StudyActivity.class);
				intent.putExtra(StudyActivity.STUDY_KEY, study);
				startActivity(intent);
				break;
			case R.id.delete:
				showDeleteDialog(study);
				break;
		}
		return true;
	}

	private void showDeleteDialog(Study study) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(R.string.dialog_delete_message);
		builder.setTitle(R.string.dialog_delete_title);
		builder.setPositiveButton(R.string.delete, (dialog, which) -> {
			viewModel.deleteStudy(study);
			studiesAdapter.deleteItem(study);
		});
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
		builder.create().show();
	}

	private void showPlaceHolder(List<Study> studies) {
		int visible = (studies != null && studies.size() > 0) ? View.INVISIBLE : View.VISIBLE;
		placeHolder.setVisibility(visible);
	}

	@Override
	public void onResume() {
		viewModel.refreshStudies();
		super.onResume();
	}

	private boolean isExternalPermissionForDatabaseGranted() {
		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestExternalPermission();
			return false;
		}
		return true;
	}

	private void requestExternalPermission() {
		ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_PERMISSION_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case EXTERNAL_PERMISSION_CODE: {
				viewModel.exportStudyAsCSV(exportStudy);
				break;
			}
		}
	}
}
