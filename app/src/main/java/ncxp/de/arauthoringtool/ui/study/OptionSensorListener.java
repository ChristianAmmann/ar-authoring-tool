package ncxp.de.arauthoringtool.ui.study;

import ncxp.de.arauthoringtool.model.data.DeviceSensor;

public interface OptionSensorListener {

	void onActiveChanged(DeviceSensor deviceSensor, boolean active);
}
