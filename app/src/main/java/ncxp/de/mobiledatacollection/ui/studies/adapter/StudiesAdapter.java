package ncxp.de.mobiledatacollection.ui.studies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.ui.studies.MoreListener;
import ncxp.de.mobiledatacollection.ui.studies.ShareListener;
import ncxp.de.mobiledatacollection.ui.studies.viewholder.StudyViewHolder;

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
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_study, parent, false);
		return new StudyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull StudyViewHolder holder, int position) {
		Study study = studies.get(position);
		holder.getStudyNameView().setText(study.getName());
		//TODO Right text
		holder.getAmountOfTestPersonView().setText("0 Probanden teilgenommen");
		holder.getMoreButton().setOnClickListener(view -> moreListener.onPopupMenuClick(view, study));
		holder.getShareButton().setOnClickListener(view -> shareListener.shareStudy(position));
		holder.getDescriptionView().setText(study.getDescription());
		holder.getStartButton().setOnClickListener((view) -> {
			//start AR
		});
		//TODO get sensors
		LinearLayout expandableView = holder.getExpandableView();
		holder.getSensorsView().setText("Sensor 1\nSensor 1\nSensor 1\nSensor 1\nSensor 1\nSensor 1\nSensor 1\n");
		holder.getExpandArrowButton().setOnClickListener(view -> {
			int visibility = expandableView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
			if (visibility == View.GONE) {
				holder.getExpandArrowButton().setImageResource(R.drawable.chevron_up);
			} else {
				holder.getExpandArrowButton().setImageResource(R.drawable.chevron_down);
			}
			expandableView.setVisibility(visibility);
		});

	}

	@Override
	public int getItemCount() {
		return studies == null ? 0 : studies.size();
	}

	public void deleteItem(Study study) {
		int position = studies.indexOf(study);
		studies.remove(position);
		notifyItemRemoved(position);
	}

	public void addItems(List<Study> newStudies) {
		studies.addAll(newStudies);
		notifyDataSetChanged();
	}

	public void replaceItems(List<Study> newStudies) {
		studies = newStudies;
		notifyDataSetChanged();
	}


}
