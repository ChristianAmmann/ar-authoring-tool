package ncxp.de.arauthoringtool.view.arscene;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ncxp.de.arauthoringtool.R;

public class ArSceneViewHolder extends RecyclerView.ViewHolder {

	private TextView    arSceneName;
	private TextView    arSceneDescription;
	private Button      loadButton;
	private ImageButton moreButton;

	public ArSceneViewHolder(View itemView) {
		super(itemView);
		arSceneName = itemView.findViewById(R.id.arscene_name);
		arSceneDescription = itemView.findViewById(R.id.arscene_description);
		loadButton = itemView.findViewById(R.id.load_button);
		moreButton = itemView.findViewById(R.id.more_options);
	}

	public TextView getArSceneDescription() {
		return arSceneDescription;
	}

	public TextView getArSceneName() {
		return arSceneName;
	}

	public Button getLoadButton() {
		return loadButton;
	}

	public ImageButton getMoreButton() {
		return moreButton;
	}
}
