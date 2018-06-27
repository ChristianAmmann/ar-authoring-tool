package ncxp.de.mobiledatacollection.ui.study;

import ncxp.de.mobiledatacollection.datalogger.AvailableDeviceSensor;

public interface OptionSensorListener {

	void onActiveChanged(AvailableDeviceSensor deviceSensor, boolean active);
}
