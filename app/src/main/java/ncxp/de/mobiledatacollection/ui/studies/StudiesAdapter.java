package ncxp.de.mobiledatacollection.ui.studies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.Study;

public class StudiesAdapter extends RecyclerView.Adapter<StudyViewHolder> {

	private List<Study> studies;

	public StudiesAdapter(List<Study> studies) {
		this.studies = studies;
	}

	@NonNull
	@Override
	public StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_item_card, parent, false);
		return new StudyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull StudyViewHolder holder, int position) {
		holder.getStudyNameView().setText(studies.get(position).getName());
	}

	@Override
	public int getItemCount() {
		return studies == null ? 0 : studies.size();
	}

	public void addItems(List<Study> newStudies) {
		studies.addAll(newStudies);
		notifyDataSetChanged();
	}

}
