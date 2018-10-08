package ncxp.de.arauthoringtool.view.mapping;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.view.areditor.Thumbnail;

public class MappingAdapter extends RecyclerView.Adapter<MappingViewHolder> {

	private List<Thumbnail> thumbnails;

	public MappingAdapter(List<Thumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}

	@NonNull
	@Override
	public MappingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mapping, parent, false);
		return new MappingViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MappingViewHolder holder, int position) {
		Thumbnail thumbnail = thumbnails.get(position);
		holder.getThumbnail().setImageDrawable(thumbnail.getDrawable());
		String qrCodeNumber = Integer.toString(position + 1);
		holder.getNumber().setText(qrCodeNumber);
	}

	@Override
	public int getItemCount() {
		return thumbnails != null ? thumbnails.size() : 0;
	}

	public void replaceItems(List<Thumbnail> newThumbnails) {
		thumbnails = newThumbnails;
		notifyDataSetChanged();
	}
}
