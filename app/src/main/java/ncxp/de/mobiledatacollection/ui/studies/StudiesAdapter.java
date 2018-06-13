package ncxp.de.mobiledatacollection.ui.studies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.Study;

public class StudiesAdapter extends RecyclerView.Adapter<StudyViewHolder> {

	private List<Study>   studies;
	private MoreListener  moreListener;
	private ShareListener shareListener;

	public StudiesAdapter(List<Study> studies, MoreListener moreListener, ShareListener shareListener) {
		this.studies = studies;
		this.moreListener = moreListener;
		this.shareListener = shareListener;
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
		//TODO Right text
		holder.getAmountOfTestPersonView().setText("0 Probanden teilgenommen");
		holder.getMoreButton().setOnClickListener(view -> moreListener.onPopupMenuClick(view, position));
		holder.getShareButton().setOnClickListener(view -> shareListener.shareStudy(position));

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
