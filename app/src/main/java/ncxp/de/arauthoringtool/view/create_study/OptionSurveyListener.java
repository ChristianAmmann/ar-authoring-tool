package ncxp.de.arauthoringtool.view.create_study;

import android.view.View;

import ncxp.de.arauthoringtool.model.data.Survey;

public interface OptionSurveyListener {
	void onPopupMenuClick(View view, Survey survey);

	void onTestButtonClick(View view, Survey survey);
}
