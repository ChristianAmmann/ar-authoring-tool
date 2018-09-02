package ncxp.de.arauthoringtool.ui.arscene;

import android.view.View;

import ncxp.de.arauthoringtool.model.data.ARScene;

public interface ArSceneListener {
	void onPopupMenuClick(View view, ARScene arScene);

	void onArSceneLoadClick(ARScene arScene);
}
