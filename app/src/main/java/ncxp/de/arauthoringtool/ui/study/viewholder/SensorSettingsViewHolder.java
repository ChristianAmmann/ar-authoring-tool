package ncxp.de.arauthoringtool.ui.study.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import ncxp.de.arauthoringtool.R;

public class SensorSettingsViewHolder extends RecyclerView.ViewHolder {

	private Spinner accuracySpinner;
	private Button  timeButton;

	public SensorSettingsViewHolder(View itemView) {
		super(itemView);
		accuracySpinner = itemView.findViewById(R.id.accuracy_spinner);
		timeButton = itemView.findViewById(R.id.time_button);
	}

	public Spinner getAccuracySpinner() {
		return accuracySpinner;
	}

	public Button getTimeButton() {
		return timeButton;
	}
}
