package ncxp.de.arauthoringtool.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.arauthoringtool.model.data.DeviceSensor;
import ncxp.de.arauthoringtool.model.data.StudyDeviceSensorJoin;

@Dao
public interface StudyDeviceSensorJoinDao {

	@Insert
	void insert(StudyDeviceSensorJoin studyDeviceSensorJoin);

	@Query("SELECT * FROM " + DeviceSensor.TABLE_NAME + " INNER JOIN " + StudyDeviceSensorJoin.TABLE_NAME + " ON " + DeviceSensor.COLUMN_ID + "=" + StudyDeviceSensorJoin
			.COLUMN_DEVICE_SENSOR_ID + " WHERE " + StudyDeviceSensorJoin.COLUMN_STUDY_ID + "=:id")
	List<DeviceSensor> getDeviceSensorsForStudy(long id);
}
