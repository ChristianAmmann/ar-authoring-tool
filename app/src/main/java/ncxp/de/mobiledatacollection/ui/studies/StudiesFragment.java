package ncxp.de.mobiledatacollection.ui.studies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;

public class StudiesFragment extends Fragment implements MoreListener, ShareListener {

	private StudiesViewModel viewModel;
	private RecyclerView     studiesView;
	private StudiesAdapter   studiesAdapter;
	private LinearLayout     placeHolder;

	public static StudiesFragment newInstance() {
		return new StudiesFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.configureViewModel();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.studies_fragment, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		studiesView = view.findViewById(R.id.studies_recyclerview);
		placeHolder = view.findViewById(R.id.empty_study_placeholder);
		setupStudiesView();
	}

	private void setupStudiesView() {
		studiesView.setHasFixedSize(true);
		studiesAdapter = new StudiesAdapter(new ArrayList<>(), this, this);
		studiesView.setLayoutManager(new LinearLayoutManager(getContext()));
		studiesView.setAdapter(studiesAdapter);
		viewModel.getStudies().observe(StudiesFragment.this, studies -> {
			showPlaceHolder(studies);
			studiesAdapter.addItems(studies);
		});
	}

	private void configureViewModel() {
		StudyDao study = StudyDatabase.getInstance(getActivity().getApplicationContext()).study();
		StudyRepository repository = new StudyRepository(study);
		StudiesViewModelFactory factory = new StudiesViewModelFactory(repository);
		viewModel = ViewModelProviders.of(this, factory).get(StudiesViewModel.class);
		viewModel.init();
	}

	@Override
	public void onPopupMenuClick(View view, int position) {
		PopupMenu popupMenu = new PopupMenu(getContext(), view);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.card_popup_menu, popupMenu.getMenu());
		//TODO setOnMenuItemClickListener
		popupMenu.show();

	}

	@Override
	public void shareStudy(int position) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("file/*");
		startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_study)));
	}

	private void showPlaceHolder(List<Study> studies) {
		int visible = (studies != null && studies.size() > 0) ? View.INVISIBLE : View.VISIBLE;
		placeHolder.setVisibility(visible);
	}
}
