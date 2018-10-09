package ncxp.de.arauthoringtool.view.qr_object_mapping;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.viewmodel.MappingViewModel;

public class MappingFragment extends Fragment {

	private MappingViewModel viewModel;
	private RecyclerView     mappingRecyclerView;
	private MappingAdapter   mappingAdapter;

	public static MappingFragment newInstance() {
		return new MappingFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = MappingActivity.obtainViewModel(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_mapping, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		mappingRecyclerView = view.findViewById(R.id.mapping_recyclerview);
		setupMappingView();
		viewModel.getThumbnails().observe(this, thumbnails -> {
			mappingAdapter.replaceItems(thumbnails);
		});
		viewModel.init();
	}

	private void setupMappingView() {
		mappingRecyclerView.setHasFixedSize(true);
		mappingAdapter = new MappingAdapter(new ArrayList<>());
		mappingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mappingRecyclerView.setAdapter(mappingAdapter);
	}
}
