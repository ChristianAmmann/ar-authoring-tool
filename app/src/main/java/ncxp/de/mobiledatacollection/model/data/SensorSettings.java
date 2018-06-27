package ncxp.de.mobiledatacollection.model.data;

public class SensorSettings {

	// Between 1 - 3
	private int accuracy;

	//in s
	private double timeInterval;

	public SensorSettings(int accuracy, double timeInterval) {
		this.accuracy = accuracy;
		this.timeInterval = timeInterval;
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
