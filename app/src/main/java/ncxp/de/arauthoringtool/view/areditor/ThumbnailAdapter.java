package ncxp.de.arauthoringtool.view.areditor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ncxp.de.arauthoringtool.R;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailViewHolder> {

	private List<Thumbnail>   thumbnails;
	private ThumbnailListener listener;
	private Thumbnail         selectedThumbnail;
	private ImageView         selectionFrame;


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
		int visibility = thumbnail.isSelected() ? View.VISIBLE : View.GONE;
		holder.getSelectionView().setVisibility(visibility);
		holder.getThumbnail().setOnClickListener(view -> {
			if (selectedThumbnail != null && selectionFrame != null) {
				selectedThumbnail.setSelected(false);
				selectionFrame.setVisibility(View.GONE);
			}
			if (!thumbnail.equals(selectedThumbnail)) {
				thumbnail.setSelected(true);
				selectedThumbnail = thumbnail;
				selectionFrame = holder.getSelectionView();
				selectionFrame.setVisibility(View.VISIBLE);
				listener.onThumbnailClicked(thumbnail.getImageName());
			} else {
				selectionFrame = null;
				selectedThumbnail = null;
				listener.onThumbnailClicked(null);
			}

		});

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
