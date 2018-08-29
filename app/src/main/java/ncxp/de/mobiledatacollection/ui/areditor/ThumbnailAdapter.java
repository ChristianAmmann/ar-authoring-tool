package ncxp.de.mobiledatacollection.ui.areditor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.mobiledatacollection.R;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailViewHolder> {

	private List<Thumbnail>   thumbnails;
	private ThumbnailListener listener;

	public ThumbnailAdapter(List<Thumbnail> thumbnails, ThumbnailListener listener) {
		this.thumbnails = thumbnails;
		this.listener = listener;
	}

	@NonNull
	@Override
	public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_model_thumbnail, parent, false);
		return new ThumbnailViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
		Thumbnail thumbnail = thumbnails.get(position);
		holder.getThumbnail().setImageDrawable(thumbnail.getDrawable());
		holder.getThumbnail().setOnClickListener(view -> listener.onThumbnailClicked(thumbnail.getImageName()));

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
