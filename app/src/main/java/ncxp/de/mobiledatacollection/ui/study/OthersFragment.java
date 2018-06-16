package ncxp.de.mobiledatacollection.ui.study;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ncxp.de.mobiledatacollection.R;

public class OthersFragment extends Fragment {

	private RecyclerView othersRecyclerView;

	public static OthersFragment newInstance() {
		return new OthersFragment();
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.others_fragment, container, false);
	}
}