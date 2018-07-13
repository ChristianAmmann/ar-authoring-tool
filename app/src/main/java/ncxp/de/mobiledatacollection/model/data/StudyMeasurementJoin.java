package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = StudyMeasurementJoin.TABLE_NAME, primaryKeys = {StudyMeasurementJoin.COLUMN_STUDY_ID, StudyMeasurementJoin.COLUMN_MEASUREMENT_ID}, foreignKeys = {
		@ForeignKey(entity = Study.class, parentColumns = Study.COLUMN_ID, childColumns = StudyMeasurementJoin.COLUMN_STUDY_ID, onDelete = CASCADE),
		@ForeignKey(entity = Measurement.class, parentColumns = Measurement.COLUMN_ID, childColumns = StudyMeasurementJoin.COLUMN_MEASUREMENT_ID)})
public class StudyMeasurementJoin {

	public static final String TABLE_NAME            = "StudyMeasurementJoin";
	public static final String COLUMN_STUDY_ID       = "study_id";
	public static final String COLUMN_MEASUREMENT_ID = "measurement_id";

	@ColumnInfo(name = COLUMN_STUDY_ID)
	private final long studyId;
	@ColumnInfo(name = COLUMN_MEASUREMENT_ID)
	private final long measurementId;

	public StudyMeasurementJoin(long studyId, long measurementId) {
		this.studyId = studyId;
		this.measurementId = measurementId;
	}

	public long getStudyId() {
		return studyId;
	}

	public long getMeasurementId() {
		return measurementId;
	}
}
