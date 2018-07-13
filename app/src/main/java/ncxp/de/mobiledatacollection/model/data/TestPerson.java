package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = TestPerson.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = Study.COLUMN_ID, childColumns = TestPerson.COLUMN_STUDY_ID, onDelete =
		CASCADE))
public class TestPerson {

	public static final String TABLE_NAME               = "Subject";
	public static final String COLUMN_ID                = "id";
	public static final String COLUMN_STUDY_ID          = "study_id";
	public static final String COLUMN_SURVEY_SUBJECT_ID = "survey_subject_id";

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private Long            id;
	@ColumnInfo(name = COLUMN_STUDY_ID)
	private Long            studyId;
	//For connecting data with survey
	@ColumnInfo(name = COLUMN_SURVEY_SUBJECT_ID)
	private Long            surveySubjectId;
	@Ignore
	private TestPersonState state;
	@Ignore
	private List<Data>      dataList;

	public TestPerson() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStudyId() {
		return studyId;
	}

	public void setStudyId(Long studyId) {
		this.studyId = studyId;
	}

	public Long getSurveySubjectId() {
		return surveySubjectId;
	}

	public void setSurveySubjectId(Long surveySubjectId) {
		this.surveySubjectId = surveySubjectId;
	}

	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}

	public static String[] getCSVHeader() {
		return new String[]{COLUMN_ID, COLUMN_SURVEY_SUBJECT_ID};
	}

	public String[] geCsvValues() {
		return new String[]{id.toString(), surveySubjectId + ""};
	}
}
