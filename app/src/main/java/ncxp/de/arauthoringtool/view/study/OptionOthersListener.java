package ncxp.de.arauthoringtool.view.study;

import android.widget.Button;

import ncxp.de.arauthoringtool.view.study.adapter.OptionItem;
import ncxp.de.arauthoringtool.view.study.adapter.SensorSettings;

public interface OptionOthersListener {

	void onTimePickerClicked(Button timeButton, SensorSettings settings);

	void onOptionItemClicked(OptionItem item);
}
