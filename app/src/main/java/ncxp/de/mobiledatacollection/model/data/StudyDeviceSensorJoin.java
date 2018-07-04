package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = StudyDeviceSensorJoin.TABLE_NAME, primaryKeys = {"studyId", "deviceSensorId"}, foreignKeys = {
		@ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId", onDelete = CASCADE),
		@ForeignKey(entity = DeviceSensor.class, parentColumns = "id", childColumns = "deviceSensorId")})
public class StudyDeviceSensorJoin {

	public static final String TABLE_NAME = "StudyDeviceSensorJoin";

	private final long studyId;
	private final long deviceSensorId;

	public StudyDeviceSensorJoin(long studyId, long deviceSensorId) {
		this.studyId = studyId;
		this.deviceSensorId = deviceSensorId;
	}

	public long getStudyId() {
		return studyId;
	}

	public long getDeviceSensorId() {
		return deviceSensorId;
	}
}
