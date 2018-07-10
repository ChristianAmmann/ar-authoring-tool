package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = TestPerson.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId", onDelete = CASCADE))
public class TestPerson {

	public static final String TABLE_NAME = "Subject";

	@PrimaryKey(autoGenerate = true)
	private long id;
	private long studyId;
	//For connecting data with survey
	private long surveySubjectId;

	public TestPerson(long id, long studyId) {
		this.id = id;
		this.studyId = studyId;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setStudyId(long studyId) {
		this.studyId = studyId;
	}

	public long getSurveySubjectId() {
		return surveySubjectId;
	}

	public void setSurveySubjectId(long surveySubjectId) {
		this.surveySubjectId = surveySubjectId;
	}

	public long getId() {
		return id;
	}

	public long getStudyId() {
		return studyId;
	}
}
