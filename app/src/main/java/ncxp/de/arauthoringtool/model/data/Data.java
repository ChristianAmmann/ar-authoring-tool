package ncxp.de.arauthoringtool.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Data.TABLE_NAME, foreignKeys = {
		@ForeignKey(entity = TestPerson.class, parentColumns = TestPerson.COLUMN_ID, childColumns = Data.COLUMN_TEST_PERSON_ID, onDelete = CASCADE)})
public class Data {

	public static final String TABLE_NAME            = "Data";
	public static final String COLUMN_ID             = "id";
	public static final String COLUMN_TEST_PERSON_ID = "test_person_id";
	public static final String COLUMN_TIMESTAMP      = "timestamp";
	public static final String COLUMN_SOURCE         = "source";
	public static final String COLUMN_VALUES         = "values";


	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private Long   id;
	@ColumnInfo(name = COLUMN_TEST_PERSON_ID)
	private Long   testPersonId;
	@ColumnInfo(name = COLUMN_TIMESTAMP)
	private Long   timestamp;
	@ColumnInfo(name = COLUMN_SOURCE)
	private String source;
	@ColumnInfo(name = COLUMN_VALUES)
	private String values;

	public Data() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTestPersonId() {
		return testPersonId;
	}

	public void setTestPersonId(Long testPersonId) {
		this.testPersonId = testPersonId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
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

	public static String[] getCSVHeader() {
		return new String[]{COLUMN_ID, COLUMN_TEST_PERSON_ID, COLUMN_SOURCE, COLUMN_TIMESTAMP, COLUMN_VALUES};
	}

	public String[] getCsvBody() {
		return new String[]{id.toString(), testPersonId.toString(), source, "" + timestamp, values};
	}
}
