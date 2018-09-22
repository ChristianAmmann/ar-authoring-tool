package ncxp.de.arauthoringtool.ui.areditor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ncxp.de.arauthoringtool.R;

public class ThumbnailViewHolder extends RecyclerView.ViewHolder {

	private ImageView thumbnail;
	private ImageView selectionView;

	public ThumbnailViewHolder(View itemView) {
		super(itemView);
		thumbnail = itemView.findViewById(R.id.thumbnail);
		selectionView = itemView.findViewById(R.id.selection_view);
	}

	public ImageView getThumbnail() {
		return thumbnail;
	}

	public ImageView getSelectionView() {
		return selectionView;
	}
}
