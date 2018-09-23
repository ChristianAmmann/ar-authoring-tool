package ncxp.de.arauthoringtool.ui.arscene;

import android.content.Intent;
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

import ncxp.de.arauthoringtool.ArSceneActivity;
import ncxp.de.arauthoringtool.MappingActivity;
import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.viewmodel.ArSceneViewModel;

public class ArSceneFragment extends Fragment implements ArSceneListener {

	private ArSceneViewModel viewModel;
	private RecyclerView     arsceneView;
	private ArSceneAdapter   arSceneAdapter;
	private LinearLayout     placeHolder;

	public static ArSceneFragment newInstance() {
		return new ArSceneFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = ArSceneActivity.obtainViewModel(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_arscene, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		arsceneView = view.findViewById(R.id.arscene_recyclerview);
		placeHolder = view.findViewById(R.id.empty_arscene_placeholder);
		setupArSceneView();
	}

	private void setupArSceneView() {
		arsceneView.setHasFixedSize(true);
		arSceneAdapter = new ArSceneAdapter(new ArrayList<>(), this);
		arsceneView.setLayoutManager(new LinearLayoutManager(getContext()));
		arsceneView.setAdapter(arSceneAdapter);
		viewModel.geArScenes().observe(ArSceneFragment.this, arScenes -> {
			showPlaceHolder(arScenes);
			arSceneAdapter.replaceItems(arScenes);
		});
		viewModel.init();
	}

	private void showPlaceHolder(List<ARScene> arScenes) {
		int visible = (arScenes != null && arScenes.size() > 0) ? View.INVISIBLE : View.VISIBLE;
		placeHolder.setVisibility(visible);
	}


	private boolean onPopupMenuItemClicked(MenuItem menuItem, ARScene arScene) {
		switch (menuItem.getItemId()) {
			case R.id.edit:
				Intent intent = new Intent(getActivity(), MappingActivity.class);
				intent.putExtra(MappingActivity.ARSCENE_KEY, arScene);
				intent.putExtra(MappingActivity.KEY_STUDY, viewModel.getStudy());
				//intent.putExtra(ArEditorActivity.KEY_EDITOR_STATE, EditorState.EDIT_MODE);
				startActivity(intent);
				break;
			case R.id.delete:
				showDeleteDialog(arScene);
				break;
		}
		return true;
	}

	private void showDeleteDialog(ARScene arScene) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(R.string.dialog_delete_arscene_message);
		builder.setTitle(R.string.dialog_delete_arscene_title);
		builder.setPositiveButton(R.string.delete, (dialog, which) -> {
			arSceneAdapter.deleteItem(arScene);
			viewModel.deleteArScene(arScene);
		});
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
		builder.create().show();
	}

	@Override
	public void onPopupMenuClick(View view, ARScene arScene) {
		PopupMenu popupMenu = new PopupMenu(getContext(), view);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.card_popup_arscene_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(menuItem -> onPopupMenuItemClicked(menuItem, arScene));
		popupMenu.show();
	}

	@Override
	public void onArSceneLoadClick(ARScene arScene) {
		Intent intent = new Intent(getActivity(), MappingActivity.class);
		intent.putExtra(MappingActivity.ARSCENE_KEY, arScene);
		intent.putExtra(MappingActivity.KEY_STUDY, viewModel.getStudy());
		//intent.putExtra(ArEditorActivity.KEY_EDITOR_STATE, EditorState.STUDY_MODE);
		getActivity().startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		viewModel.init();
	}
}
