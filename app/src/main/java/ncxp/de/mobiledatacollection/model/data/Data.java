package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Data.TABLE_NAME, foreignKeys = {
		@ForeignKey(entity = TestPerson.class, parentColumns = "id", childColumns = "testPersonId", onDelete = CASCADE)})
public class Data {

	public static final String TABLE_NAME = "Data";

	@PrimaryKey(autoGenerate = true)
	private long   id;
	private long   testPersonId;
	private long   timestamp;
	private String source;
	private String values;

	public Data() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTestPersonId() {
		return testPersonId;
	}

	public void setTestPersonId(long testPersonId) {
		this.testPersonId = testPersonId;
	}

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

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "Data{" + "testPersonId=" + testPersonId + ", timestamp=" + timestamp + ", source='" + source + '\'' + ", values='" + values + '\'' + '}';
	}
}
