package ncxp.de.arauthoringtool.view.study.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ncxp.de.arauthoringtool.R;

public class SurveyViewHolder extends RecyclerView.ViewHolder {

	private TextView    surveyName;
	private TextView    surveyDescription;
	private ImageButton moreButton;
	private Button      testButton;

	public SurveyViewHolder(View itemView) {
		super(itemView);
		surveyName = itemView.findViewById(R.id.survey_name);
		surveyDescription = itemView.findViewById(R.id.survey_description);
		moreButton = itemView.findViewById(R.id.more_options);
		testButton = itemView.findViewById(R.id.test_button);
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

	public Button getTestButton() {
		return testButton;
	}
}
