package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ncxp.de.mobiledatacollection.R;

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
