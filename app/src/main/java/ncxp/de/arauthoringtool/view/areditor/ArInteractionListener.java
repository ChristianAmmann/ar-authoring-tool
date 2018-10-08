package ncxp.de.arauthoringtool.view.areditor;

import ncxp.de.arauthoringtool.view.areditor.util.EditorState;

public interface ArInteractionListener {

	void onDeleteAllArObjects();

	void onEditorStateChanged(EditorState state);
}
