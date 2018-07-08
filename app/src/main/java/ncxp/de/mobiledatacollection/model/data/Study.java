package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.hardware.SensorManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

@Entity(tableName = Study.TABLE_NAME)
public class Study implements Parcelable {

	public static final String TABLE_NAME = "Study";

	@PrimaryKey(autoGenerate = true)
	private long               id;
	private String             name;
	private String             description;
	// Between 1 - 3
	private int                sensorAccuracy          = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
	//in s
	private double             sensorMeasuringDistance = 1.0;
	private boolean            isCapturingScreen       = false;
	private boolean            isCapturingAudio        = false;
	@Ignore
	private List<DeviceSensor> sensors;

	public Study() {
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DeviceSensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<DeviceSensor> sensors) {
		this.sensors = sensors;
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
		return (int) ((sensorMeasuringDistance - (int) sensorMeasuringDistance) * 100);

	}

	public void setSensorMeasuringDistance(double sensorMeasuringDistance) {
		this.sensorMeasuringDistance = sensorMeasuringDistance;
	}

	public boolean isCapturingScreen() {
		return isCapturingScreen;
	}

	public void setCapturingScreen(boolean capturingScreen) {
		isCapturingScreen = capturingScreen;
	}

	public boolean isCapturingAudio() {
		return isCapturingAudio;
	}

	public void setCapturingAudio(boolean capturingAudio) {
		isCapturingAudio = capturingAudio;
	}

	protected Study(Parcel in) {
		id = in.readLong();
		name = in.readString();
		description = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(description);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Study> CREATOR = new Parcelable.Creator<Study>() {
		@Override
		public Study createFromParcel(Parcel in) {
			return new Study(in);
		}

		@Override
		public Study[] newArray(int size) {
			return new Study[size];
		}
	};
}


