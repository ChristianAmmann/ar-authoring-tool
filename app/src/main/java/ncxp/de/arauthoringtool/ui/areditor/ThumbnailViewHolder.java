package ncxp.de.arauthoringtool.ui.areditor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ncxp.de.arauthoringtool.R;

public class ThumbnailViewHolder extends RecyclerView.ViewHolder {

	private ImageView thumbnail;

	public ThumbnailViewHolder(View itemView) {
		super(itemView);
		thumbnail = itemView.findViewById(R.id.thumbnail);
	}

	public ImageView getThumbnail() {
		return thumbnail;
	}
}
