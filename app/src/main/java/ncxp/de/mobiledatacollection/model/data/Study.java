package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.hardware.SensorManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = Study.TABLE_NAME)
public class Study implements Parcelable {

	public static final String TABLE_NAME              = "Study";
	public static final String COLUMN_ID               = "id";
	public static final String COLUMN_NAME             = "name";
	public static final String COLUMN_DESCRIPTION      = "description";
	public static final String COLUMN_SENSOR_ACCURACY  = "accuracy";
	public static final String COLUMN_SAMPLING_RATE    = "sampling_rate";
	public static final String COLUMN_SCREEN_CAPTURING = "screen_capturing";
	public static final String COLUMN_AUDIO_CAPTURING  = "audio_capturing";

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private Long               id;
	@ColumnInfo(name = COLUMN_NAME)
	private String             name;
	@ColumnInfo(name = COLUMN_DESCRIPTION)
	private String             description;
	// Between 1 -
	@ColumnInfo(name = COLUMN_SENSOR_ACCURACY)
	private Integer            accuracy          = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
	//in s
	@ColumnInfo(name = COLUMN_SAMPLING_RATE)
	private Double             samplingRate      = 1.0;
	@ColumnInfo(name = COLUMN_SCREEN_CAPTURING)
	private Boolean            isCapturingScreen = false;
	@ColumnInfo(name = COLUMN_AUDIO_CAPTURING)
	private Boolean            isCapturingAudio  = false;
	@Ignore
	private List<DeviceSensor> sensors;

	public Study() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
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

	public Integer getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}

	public Double getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(Double samplingRate) {
		this.samplingRate = samplingRate;
	}

	public Boolean isCapturingScreen() {
		return isCapturingScreen;
	}

	public void setCapturingScreen(Boolean capturingScreen) {
		isCapturingScreen = capturingScreen;
	}

	public Boolean isCapturingAudio() {
		return isCapturingAudio;
	}

	public void setCapturingAudio(Boolean capturingAudio) {
		isCapturingAudio = capturingAudio;
	}

	public List<DeviceSensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<DeviceSensor> sensors) {
		this.sensors = sensors;
	}

	public static String[] getCSVHeader() {
		return new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_SENSOR_ACCURACY, COLUMN_SAMPLING_RATE, COLUMN_SCREEN_CAPTURING, COLUMN_AUDIO_CAPTURING};
	}

	public String[] getCsvValues() {
		return new String[]{id.toString(), name, description, accuracy.toString(), samplingRate.toString(), isCapturingScreen.toString(), isCapturingAudio.toString()};
	}

	protected Study(Parcel in) {
		id = in.readByte() == 0x00 ? null : in.readLong();
		name = in.readString();
		description = in.readString();
		accuracy = in.readByte() == 0x00 ? null : in.readInt();
		samplingRate = in.readByte() == 0x00 ? null : in.readDouble();
		byte isCapturingScreenVal = in.readByte();
		isCapturingScreen = isCapturingScreenVal == 0x02 ? null : isCapturingScreenVal != 0x00;
		byte isCapturingAudioVal = in.readByte();
		isCapturingAudio = isCapturingAudioVal == 0x02 ? null : isCapturingAudioVal != 0x00;
		if (in.readByte() == 0x01) {
			sensors = new ArrayList<DeviceSensor>();
			in.readList(sensors, DeviceSensor.class.getClassLoader());
		} else {
			sensors = null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (id == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeLong(id);
		}
		dest.writeString(name);
		dest.writeString(description);
		if (accuracy == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeInt(accuracy);
		}
		if (samplingRate == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeDouble(samplingRate);
		}
		if (isCapturingScreen == null) {
			dest.writeByte((byte) (0x02));
		} else {
			dest.writeByte((byte) (isCapturingScreen ? 0x01 : 0x00));
		}
		if (isCapturingAudio == null) {
			dest.writeByte((byte) (0x02));
		} else {
			dest.writeByte((byte) (isCapturingAudio ? 0x01 : 0x00));
		}
		if (sensors == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeList(sensors);
		}
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


