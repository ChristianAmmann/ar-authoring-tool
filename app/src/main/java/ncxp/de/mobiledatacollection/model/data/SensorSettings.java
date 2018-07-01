package ncxp.de.mobiledatacollection.model.data;

import android.hardware.SensorManager;

public class SensorSettings {

	// Between 1 - 3
	private int accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;

	//in s
	private double timeInterval = 1.0;

	public SensorSettings() {
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public double getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(double timeInterval) {
		this.timeInterval = timeInterval;
	}
}
