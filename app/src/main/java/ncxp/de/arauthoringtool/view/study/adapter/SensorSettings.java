package ncxp.de.arauthoringtool.view.study.adapter;

import android.hardware.SensorManager;

public class SensorSettings {

	// Between 1 - 3
	private int    sensorAccuracy          = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
	//in s
	private double sensorMeasuringDistance = 1.0;

	public SensorSettings() {
	}

	public int getSensorAccuracy() {
		return sensorAccuracy;
	}

	public void setSensorAccuracy(int sensorAccuracy) {
		this.sensorAccuracy = sensorAccuracy;
	}

	public double getSensorMeasuringDistance() {
		return sensorMeasuringDistance;
	}

	public int getSeconds() {
		return (int) sensorMeasuringDistance;
	}

	public int getMilliseconds() {
		return (int) ((sensorMeasuringDistance - (int) sensorMeasuringDistance) * 1000);

	}

	public void setSensorMeasuringDistance(double sensorMeasuringDistance) {
		this.sensorMeasuringDistance = sensorMeasuringDistance;
	}
}
