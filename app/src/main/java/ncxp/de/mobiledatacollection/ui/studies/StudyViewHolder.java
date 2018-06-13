package ncxp.de.mobiledatacollection.ui.studies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ncxp.de.mobiledatacollection.R;

public class StudyViewHolder extends RecyclerView.ViewHolder {

	private TextView    studyNameView;
	private TextView    amountOfTestPersonView;
	private ImageButton expandArrowButton;
	private ImageButton shareButton;
	private ImageButton moreButton;


	public StudyViewHolder(@NonNull View itemView) {
		super(itemView);
		studyNameView = itemView.findViewById(R.id.study_name);
		amountOfTestPersonView = itemView.findViewById(R.id.amount_of_testperson);
		expandArrowButton = itemView.findViewById(R.id.expand_arrow);
		shareButton = itemView.findViewById(R.id.share);
		moreButton = itemView.findViewById(R.id.more);
	}

	public ImageButton getExpandArrowButton() {
		return expandArrowButton;
	}

	public ImageButton getShareButton() {
		return shareButton;
	}

	public ImageButton getMoreButton() {
		return moreButton;
	}

	public TextView getStudyNameView() {
		return studyNameView;
	}

	public TextView getAmountOfTestPersonView() {
		return amountOfTestPersonView;
	}
}
