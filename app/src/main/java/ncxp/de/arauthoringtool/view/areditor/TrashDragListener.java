package ncxp.de.arauthoringtool.view.areditor;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

public class TrashDragListener implements View.OnDragListener {

	public static final String TAG = "blablabla";

	private int     enterShape;
	private int     normalShape;
	private boolean hit;

	public TrashDragListener(int enterShape, int normalShape) {
		this.enterShape = enterShape;
		this.normalShape = normalShape;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		final ImageView containerView = (ImageView) v;
		final ImageView draggedView = (ImageView) event.getLocalState();
		switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				Log.d(TAG, "onDrag: ACTION_DRAG_STARTED");
				hit = false;
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				Log.d(TAG, "onDrag: ACTION_DRAG_ENTERED");
				containerView.setImageResource(enterShape);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				Log.d(TAG, "onDrag: ACTION_DRAG_EXITED");
				containerView.setImageResource(normalShape);
				break;
			case DragEvent.ACTION_DROP:
				Log.d(TAG, "onDrag: ACTION_DROP");
				//TODO remove object
				hit = true;
				draggedView.post(() -> draggedView.setVisibility(View.GONE));
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				Log.d(TAG, "onDrag: ACTION_DRAG_ENDED");
				containerView.setImageResource(normalShape);
				v.setVisibility(View.VISIBLE);
				if (!hit) {
					draggedView.post(() -> draggedView.setVisibility(View.VISIBLE));
				}
				break;
			default:
				break;
		}
		return true;
	}
}
