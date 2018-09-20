package ncxp.de.arauthoringtool.ui.areditor;

import com.google.ar.sceneform.Node;

import ncxp.de.arauthoringtool.ui.areditor.util.EditorState;

public interface ArInteractionListener {

	void onReplaceArObject(String imageName, Node value);

	void onDeleteAllArObjects();

	void onEditorStateChanged(EditorState state);
}
