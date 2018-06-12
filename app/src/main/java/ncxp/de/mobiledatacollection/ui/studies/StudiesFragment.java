package ncxp.de.mobiledatacollection.ui.studies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.StudyDatabase;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;

public class StudiesFragment extends Fragment {

	private StudiesViewModel viewModel;
	private RecyclerView     studiesView;
	private StudiesAdapter   studiesAdapter;

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
		View view = inflater.inflate(R.layout.studies_fragment, container, false);
		studiesView = view.findViewById(R.id.studies_recyclerview);
		setupStudiesView();
		return view;
	}

	private void setupStudiesView() {
		studiesAdapter = new StudiesAdapter(new ArrayList<>());
		studiesView.setLayoutManager(new LinearLayoutManager(getContext()));
		studiesView.setAdapter(studiesAdapter);
		viewModel.getStudies().observe(StudiesFragment.this, studies -> studiesAdapter.addItems(studies));
	}

	private void configureViewModel() {
		StudyDao study = StudyDatabase.getInstance(getActivity().getApplicationContext()).study();
		StudyRepository repository = new StudyRepository(study);
		StudiesViewModelFactory factory = new StudiesViewModelFactory(repository);
		viewModel = ViewModelProviders.of(this, factory).get(StudiesViewModel.class);
		viewModel.init();
	}

}
