package ncxp.de.arauthoringtool.view.arscene;

import android.view.View;

import ncxp.de.arauthoringtool.model.data.ARScene;

public interface ArSceneListener {
	void onPopupMenuClick(View view, ARScene arScene);

	void onArSceneLoadClick(ARScene arScene);
}
