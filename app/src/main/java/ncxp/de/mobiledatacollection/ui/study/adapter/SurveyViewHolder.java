package ncxp.de.mobiledatacollection.ui.study.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ncxp.de.mobiledatacollection.R;

public class SurveyViewHolder extends RecyclerView.ViewHolder {

	private TextView    surveyName;
	private TextView    surveyDescription;
	private ImageButton moreButton;

	public SurveyViewHolder(View itemView) {
		super(itemView);
		surveyName = itemView.findViewById(R.id.survey_name);
		surveyDescription = itemView.findViewById(R.id.survey_description);
		moreButton = itemView.findViewById(R.id.more_options);
	}

	public TextView getSurveyName() {
		return surveyName;
	}

	public TextView getSurveyDescription() {
		return surveyDescription;
	}

	public ImageButton getMoreButton() {
		return moreButton;
	}
}
