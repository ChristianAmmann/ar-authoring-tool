package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = StudyMeasurementJoin.TABLE_NAME, primaryKeys = {"studyId", "measurementId"}, foreignKeys = {
		@ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId", onDelete = CASCADE),
		@ForeignKey(entity = Measurement.class, parentColumns = "id", childColumns = "measurementId")})
public class StudyMeasurementJoin {

	public static final String TABLE_NAME = "StudyMeasurementJoin";

	private final long studyId;
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
