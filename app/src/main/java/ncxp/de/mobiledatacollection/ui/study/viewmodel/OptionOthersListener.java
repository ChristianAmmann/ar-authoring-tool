package ncxp.de.mobiledatacollection.ui.study.viewmodel;

import android.widget.Button;

import ncxp.de.mobiledatacollection.ui.study.adapter.OptionItem;
import ncxp.de.mobiledatacollection.ui.study.adapter.SensorSettings;

public interface OptionOthersListener {

	void onTimePickerClicked(Button timeButton, SensorSettings settings);

	void onOptionItemClicked(OptionItem item);
}
