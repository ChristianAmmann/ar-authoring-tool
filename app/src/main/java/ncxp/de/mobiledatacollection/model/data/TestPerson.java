package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = TestPerson.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId", onDelete = CASCADE))
public class TestPerson {

	public static final String TABLE_NAME = "subjects";

	@PrimaryKey
	private final long id;
	private final long studyId;

	public TestPerson(long id, long studyId) {
		this.id = id;
		this.studyId = studyId;
	}

	public long getId() {
		return id;
	}

	public long getStudyId() {
		return studyId;
	}
}
