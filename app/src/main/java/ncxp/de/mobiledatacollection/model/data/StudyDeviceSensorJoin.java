package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(tableName = StudyDeviceSensorJoin.TABLE_NAME, primaryKeys = {"studyId", "deviceSensorId"}, foreignKeys = {
		@ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId"),
		@ForeignKey(entity = DeviceSensor.class, parentColumns = "id", childColumns = "deviceSensorId")})
public class StudyDeviceSensorJoin {

	public static final String TABLE_NAME = "StudyDeviceSensorJoin";

	private final long studyId;
	private final long deviceSensorId;

	public StudyDeviceSensorJoin(long studyId, long deviceSensorId) {
		this.studyId = studyId;
		this.deviceSensorId = deviceSensorId;
	}
}
