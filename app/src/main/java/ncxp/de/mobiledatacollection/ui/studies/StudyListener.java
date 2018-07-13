package ncxp.de.mobiledatacollection.ui.studies;

import android.view.View;

import ncxp.de.mobiledatacollection.model.data.Study;

public interface StudyListener {

	void onPopupMenuClick(View view, Study study);

	void onStudyStartClick(Study study);
}
