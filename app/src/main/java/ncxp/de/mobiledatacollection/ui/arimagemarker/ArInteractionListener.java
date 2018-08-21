package ncxp.de.mobiledatacollection.ui.arimagemarker;

import com.google.ar.sceneform.Node;

public interface ArInteractionListener {

	void onReplaceArObject(String imageName, Node value);

	void onDeleteAllArObjects();

	void onEditorStateChanged();
}
