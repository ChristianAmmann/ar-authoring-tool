package ncxp.de.arauthoringtool.ui.studies;

import android.view.View;

import ncxp.de.arauthoringtool.model.data.Study;

public interface StudyListener {

	void onPopupMenuClick(View view, Study study);

	void onStudyStartClick(Study study);
}
