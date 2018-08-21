package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ncxp.de.mobiledatacollection.ArImageMarkerActivity;
import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.ARScene;

public class ArEditFragment extends Fragment implements ThumbnailListener {

	private ArImageMarkerViewModel viewModel;
	private ImageView              fitToScanView;
	private ImageButton            expandThumbnailButton;
	private LinearLayout           actionButtonsContainer;
	private RecyclerView           modelRecyclerView;
	private ThumbnailAdapter       thumbnailAdapter;
	private ArInteractionListener  arInteractionListener;


	public static ArEditFragment newInstance() {
		return new ArEditFragment();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof ArInteractionListener) {
			arInteractionListener = (ArInteractionListener) context;
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ar_edit, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		viewModel = ArImageMarkerActivity.obtainViewModel(getActivity());
		fitToScanView = view.findViewById(R.id.image_view_fit_to_scan);
		modelRecyclerView = view.findViewById(R.id.model_thumbnail_list);
		expandThumbnailButton = view.findViewById(R.id.expand_thumbnail_button);
		expandThumbnailButton.setOnClickListener(clickedView -> {
			if (modelRecyclerView.getVisibility() != View.GONE) {
				hideBottomThumbnails();
			} else {
				showBottomThumbnails();
			}
		});
		actionButtonsContainer = view.findViewById(R.id.action_container);
		ImageButton saveSceneButton = view.findViewById(R.id.save_scene);
		saveSceneButton.setOnClickListener(clickedView -> showSaveDialog());
		ImageButton deleteButton = view.findViewById(R.id.delete_object);
		deleteButton.setOnClickListener(clickedView -> showDeleteDialog());
		deleteButton.setOnDragListener(new TrashDragListener(R.drawable.delete_empty, R.drawable.delete));
		ImageButton backSceneButton = view.findViewById(R.id.back_arscene);
		backSceneButton.setOnClickListener(clickedView -> getActivity().finish());

		initBottomBar();
		setupAdapter();
		viewModel.getThumbnails().observe(this, drawables -> thumbnailAdapter.replaceItems(drawables));
		viewModel.init();
		viewModel.getCurrentSelectedNode().observe(this, current -> {
			if (current != null) {
				showBottomThumbnails();
				showActionButtons();
				fitToScanView.setVisibility(View.GONE);
			}
		});

	}


	private void setupAdapter() {
		modelRecyclerView.setHasFixedSize(true);
		thumbnailAdapter = new ThumbnailAdapter(new ArrayList<>(), this);
		modelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
		modelRecyclerView.setAdapter(thumbnailAdapter);
	}

	@Override
	public void onThumbnailClicked(String imageName) {
		arInteractionListener.onReplaceArObject(imageName, viewModel.getCurrentSelectedNode().getValue());
	}

	private void showSaveDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		LayoutInflater inflater = getLayoutInflater();
		builder.setTitle(R.string.dialog_title_save_ar_scene);
		View dialogView = inflater.inflate(R.layout.dialog_save_arscene, null);
		TextInputEditText titleInput = dialogView.findViewById(R.id.arscene_title_edit);
		TextInputEditText descriptionInput = dialogView.findViewById(R.id.arscene_description_edit);
		builder.setView(dialogView).setPositiveButton(R.string.save, (dialog, which) -> {
			boolean validInput = validateInput(titleInput, R.string.dialog_arscene_error_title);
			validInput &= validateInput(descriptionInput, R.string.dialog_arscene_error_description);
			if (validInput) {
				String title = titleInput.getText().toString();
				String description = descriptionInput.getText().toString();
				if (viewModel.getArScene() != null) {
					ARScene arScene = viewModel.getArScene();
					arScene.setName(title);
					arScene.setDescription(description);
					viewModel.update(arScene);
				} else {
					viewModel.save(title, description);
				}
				viewModel.setState(EditorState.STUDY_MODE);
				arInteractionListener.onEditorStateChanged();
			}

		});
		builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
		builder.setCancelable(false);
		if (viewModel.getArScene() != null) {
			titleInput.setText(viewModel.getArScene().getName());
			descriptionInput.setText(viewModel.getArScene().getDescription());
		}
		builder.create().show();
	}

	private boolean validateInput(TextInputEditText editText, int errorCode) {
		if (editText.getText().toString().isEmpty()) {
			editText.setError(getString(errorCode));
			return false;
		}
		return true;
	}


	private void showDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.dialog_ar_objects_delete_all).setMessage(R.string.dialog_ar_objects_delete_all_message);
		builder.setPositiveButton(R.string.delete, (dialog, which) -> {
			arInteractionListener.onDeleteAllArObjects();
			showActionButtons();
		}).setNegativeButton(R.string.cancel, null).setCancelable(false).show();
	}

	private void showBottomThumbnails() {
		expandThumbnailButton.setVisibility(View.VISIBLE);
		modelRecyclerView.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) expandThumbnailButton.getLayoutParams();
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		expandThumbnailButton.setImageResource(R.drawable.chevron_down);
	}

	private void hideBottomThumbnails() {
		modelRecyclerView.setVisibility(View.GONE);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) expandThumbnailButton.getLayoutParams();
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		expandThumbnailButton.setImageResource(R.drawable.chevron_up);
	}

	private void showActionButtons() {
		if (viewModel.containsARObject()) {
			actionButtonsContainer.setVisibility(View.VISIBLE);
		} else {
			actionButtonsContainer.setVisibility(View.GONE);
		}
	}


	private void initBottomBar() {
		expandThumbnailButton.setOnClickListener((view) -> {
			int visibility = modelRecyclerView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
			if (visibility == View.GONE) {
				hideBottomThumbnails();
			} else {
				showBottomThumbnails();
			}
			modelRecyclerView.setVisibility(visibility);

		});
	}
}
