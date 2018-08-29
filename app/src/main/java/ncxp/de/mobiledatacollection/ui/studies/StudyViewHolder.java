package ncxp.de.mobiledatacollection.ui.studies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import ncxp.de.mobiledatacollection.R;

public class StudyViewHolder extends RecyclerView.ViewHolder {

	private TextView     studyNameView;
	private TextView     amountOfTestPersonView;
	private ImageButton  expandArrowButton;
	private ImageButton  moreButton;
	private LinearLayout expandableView;
	private TextView     descriptionView;
	private TextView     sensorsView;
	private Button       startButton;


	public StudyViewHolder(@NonNull View itemView) {
		super(itemView);
		studyNameView = itemView.findViewById(R.id.study_name);
		amountOfTestPersonView = itemView.findViewById(R.id.amount_of_testperson);
		expandArrowButton = itemView.findViewById(R.id.expand_arrow);
		moreButton = itemView.findViewById(R.id.more);
		expandableView = itemView.findViewById(R.id.expandable_view);
		descriptionView = itemView.findViewById(R.id.study_description);
		sensorsView = itemView.findViewById(R.id.sensors);
		startButton = itemView.findViewById(R.id.start_button);
	}

	public ImageButton getExpandArrowButton() {
		return expandArrowButton;
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

	public LinearLayout getExpandableView() {
		return expandableView;
	}

	public TextView getDescriptionView() {
		return descriptionView;
	}

	public TextView getSensorsView() {
		return sensorsView;
	}

	public Button getStartButton() {
		return startButton;
	}
}
