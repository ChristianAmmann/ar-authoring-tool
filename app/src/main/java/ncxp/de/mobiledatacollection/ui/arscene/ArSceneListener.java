package ncxp.de.mobiledatacollection.ui.arscene;

import android.view.View;

import ncxp.de.mobiledatacollection.model.data.ARScene;

public interface ArSceneListener {
	void onPopupMenuClick(View view, ARScene arScene);

	void onArSceneLoadClick(ARScene arScene);
}
