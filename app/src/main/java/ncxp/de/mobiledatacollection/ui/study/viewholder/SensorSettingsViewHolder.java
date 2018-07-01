package ncxp.de.mobiledatacollection.ui.study.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import ncxp.de.mobiledatacollection.R;

public class SensorSettingsViewHolder extends RecyclerView.ViewHolder {

	private SeekBar accuracySeekBar;
	private Button  timeButton;

	public SensorSettingsViewHolder(View itemView) {
		super(itemView);
		accuracySeekBar = itemView.findViewById(R.id.accuracy_seek_bar);
		timeButton = itemView.findViewById(R.id.time_button);
	}

	public SeekBar getAccuracySeekBar() {
		return accuracySeekBar;
	}

	public Button getTimeButton() {
		return timeButton;
	}
}
