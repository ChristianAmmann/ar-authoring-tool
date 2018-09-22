package ncxp.de.arauthoringtool.ui.areditor;

import ncxp.de.arauthoringtool.ui.areditor.util.EditorState;

public interface ArInteractionListener {

	void onDeleteAllArObjects();

	void onEditorStateChanged(EditorState state);
}
