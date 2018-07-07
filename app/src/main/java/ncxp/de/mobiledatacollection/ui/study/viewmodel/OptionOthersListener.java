package ncxp.de.mobiledatacollection.ui.study.viewmodel;

import android.widget.Button;

import ncxp.de.mobiledatacollection.model.data.SensorSettings;

public interface OptionOthersListener {

	public void onTimePickerClicked(Button timeButton, SensorSettings settings);
}
