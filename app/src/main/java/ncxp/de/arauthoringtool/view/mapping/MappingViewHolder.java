package ncxp.de.arauthoringtool.view.mapping;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ncxp.de.arauthoringtool.R;

public class MappingViewHolder extends RecyclerView.ViewHolder {

	private ImageView thumbnail;
	private TextView  number;

	public MappingViewHolder(View itemView) {
		super(itemView);
		this.thumbnail = itemView.findViewById(R.id.map_thumbnail);
		this.number = itemView.findViewById(R.id.map_number);
	}

	public ImageView getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(ImageView thumbnail) {
		this.thumbnail = thumbnail;
	}

	public TextView getNumber() {
		return number;
	}

	public void setNumber(TextView number) {
		this.number = number;
	}
}
