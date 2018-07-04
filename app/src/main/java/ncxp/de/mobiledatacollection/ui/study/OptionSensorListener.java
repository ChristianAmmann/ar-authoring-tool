package ncxp.de.mobiledatacollection.ui.study;

import ncxp.de.mobiledatacollection.model.data.DeviceSensor;

public interface OptionSensorListener {

	void onActiveChanged(DeviceSensor deviceSensor, boolean active);
}
