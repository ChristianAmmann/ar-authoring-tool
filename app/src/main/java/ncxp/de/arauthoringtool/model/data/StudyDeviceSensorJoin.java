package ncxp.de.arauthoringtool.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = StudyDeviceSensorJoin.TABLE_NAME, primaryKeys = {StudyDeviceSensorJoin.COLUMN_STUDY_ID, StudyDeviceSensorJoin.COLUMN_DEVICE_SENSOR_ID}, foreignKeys = {
		@ForeignKey(entity = Study.class, parentColumns = Study.COLUMN_ID, childColumns = StudyDeviceSensorJoin.COLUMN_STUDY_ID, onDelete = CASCADE),
		@ForeignKey(entity = DeviceSensor.class, parentColumns = DeviceSensor.COLUMN_ID, childColumns = StudyDeviceSensorJoin.COLUMN_DEVICE_SENSOR_ID)})
public class StudyDeviceSensorJoin {

	public static final String TABLE_NAME              = "StudyDeviceSensorJoin";
	public static final String COLUMN_STUDY_ID         = "study_id";
	public static final String COLUMN_DEVICE_SENSOR_ID = "device_sensor_id";

	@ColumnInfo(name = COLUMN_STUDY_ID)
	private final long   studyId;
	@NonNull
	@ColumnInfo(name = COLUMN_DEVICE_SENSOR_ID)
	private final String deviceSensorId;

	public StudyDeviceSensorJoin(long studyId, String deviceSensorId) {
		this.studyId = studyId;
		this.deviceSensorId = deviceSensorId;
	}

	public long getStudyId() {
		return studyId;
	}

	public String getDeviceSensorId() {
		return deviceSensorId;
	}
}
