package ncxp.de.mobiledatacollection.ui.arscene;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.ARScene;
import ncxp.de.mobiledatacollection.ui.arscene.ArSceneListener;
import ncxp.de.mobiledatacollection.ui.arscene.ArSceneViewHolder;

public class ArSceneAdapter extends RecyclerView.Adapter<ArSceneViewHolder> {

	private List<ARScene>   arScenes;
	private ArSceneListener arSceneListener;

	public ArSceneAdapter(List<ARScene> arScenes, ArSceneListener arSceneListener) {
		this.arScenes = arScenes;
		this.arSceneListener = arSceneListener;

	}

	@NonNull
	@Override
	public ArSceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_arscene, parent, false);
		return new ArSceneViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ArSceneViewHolder holder, int position) {
		ARScene arScene = arScenes.get(position);
		holder.getArSceneName().setText(arScene.getName());
		holder.getArSceneDescription().setText(arScene.getDescription());
		holder.getMoreButton().setOnClickListener(view -> arSceneListener.onPopupMenuClick(view, arScene));
		holder.getLoadButton().setOnClickListener(view -> arSceneListener.onArSceneLoadClick(arScene));
	}

	@Override
	public int getItemCount() {
		return arScenes == null ? 0 : arScenes.size();
	}

	public void deleteItem(ARScene arScene) {
		int position = arScenes.indexOf(arScene);
		arScenes.remove(position);
		notifyItemRemoved(position);
	}

	public void addItems(List<ARScene> newArScenes) {
		arScenes.addAll(newArScenes);
		notifyDataSetChanged();
	}

	public void replaceItems(List<ARScene> newArScenes) {
		arScenes = newArScenes;
		notifyDataSetChanged();
	}

}



