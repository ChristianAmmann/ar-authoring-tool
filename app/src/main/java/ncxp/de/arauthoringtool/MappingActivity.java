package ncxp.de.arauthoringtool;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.ui.areditor.util.EditorState;
import ncxp.de.arauthoringtool.ui.mapping.MappingFragment;
import ncxp.de.arauthoringtool.viewmodel.MappingViewModel;
import ncxp.de.arauthoringtool.viewmodel.factory.MappingViewModelFactory;

public class MappingActivity extends AppCompatActivity {

	public static final String               ARSCENE_KEY      = "arscene_key";
	public static final String               KEY_STUDY        = "study_key";
	public static final String               KEY_EDITOR_STATE = "editor_state_key";
	private             FloatingActionButton fab;
	private             MappingViewModel     viewModel;

	public static MappingViewModel obtainViewModel(FragmentActivity activity) {
		ARScene arScene = activity.getIntent().getParcelableExtra(ARSCENE_KEY);
		Study study = activity.getIntent().getParcelableExtra(KEY_STUDY);
		EditorState state = (EditorState) activity.getIntent().getSerializableExtra(KEY_EDITOR_STATE);
		MappingViewModel viewModel = ViewModelProviders.of(activity, createFactory(activity)).get(MappingViewModel.class);
		viewModel.setArScene(arScene);
		viewModel.setStudy(study);
		viewModel.setState(state);
		return viewModel;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapping);
		viewModel = obtainViewModel(this);
		fab = findViewById(R.id.fab_play);
		fab.setOnClickListener(view -> {
			Intent intent = new Intent(this, ArEditorActivity.class);
			intent.putExtra(MappingActivity.ARSCENE_KEY, viewModel.getArScene());
			intent.putExtra(MappingActivity.KEY_STUDY, viewModel.getStudy());
			intent.putExtra(MappingActivity.KEY_EDITOR_STATE, viewModel.getState());
			startActivity(intent);
			finish();
		});
		showFragment(savedInstanceState);
	}

	private void showFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.mapping_container, MappingFragment.newInstance(), null).commit();
		}
	}

	private static MappingViewModelFactory createFactory(FragmentActivity activity) {
		return new MappingViewModelFactory(activity.getApplication());
	}
}
