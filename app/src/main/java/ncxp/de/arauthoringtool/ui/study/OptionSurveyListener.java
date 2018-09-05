package ncxp.de.arauthoringtool.ui.study;

import android.view.View;

import ncxp.de.arauthoringtool.model.data.Survey;

public interface OptionSurveyListener {
	void onPopupMenuClick(View view, Survey survey);

	void onTestButtonClick(View view, Survey survey);
}
