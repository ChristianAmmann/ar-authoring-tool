package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Data.TABLE_NAME, foreignKeys = {
		@ForeignKey(entity = TestPerson.class, parentColumns = "id", childColumns = "testPersonId", onDelete = CASCADE),
		@ForeignKey(entity = DeviceSensor.class, parentColumns = "id", childColumns = "deviceSensorId", onDelete = CASCADE)})
public class Data {

	public static final String TABLE_NAME = "data";

	@PrimaryKey(autoGenerate = true)
	private final long    id;
	private final long    testPersonId;
	private final long    deviceSensorId;
	private       long    timestamp;
	private       String  source;
	private       float[] values;

	public Data(long id, long testPersonId, long deviceSensorId, long timestamp, String source, float[] values) {
		this.id = id;
		this.testPersonId = testPersonId;
		this.deviceSensorId = deviceSensorId;
		this.timestamp = timestamp;
		this.source = source;
		this.values = values;
	}

	/**
	 * Getter & Setter
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public float[] getValues() {
		return values;
	}

	public void setValues(float[] values) {
		this.values = values;
	}

}
