package ncxp.de.arauthoringtool.ui.study;

import android.widget.Button;

import ncxp.de.arauthoringtool.ui.study.adapter.OptionItem;
import ncxp.de.arauthoringtool.ui.study.adapter.SensorSettings;

public interface OptionOthersListener {

	void onTimePickerClicked(Button timeButton, SensorSettings settings);

	void onOptionItemClicked(OptionItem item);
}
