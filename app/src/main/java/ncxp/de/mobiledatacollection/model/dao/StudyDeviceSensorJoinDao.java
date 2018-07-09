package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.StudyDeviceSensorJoin;

@Dao
public interface StudyDeviceSensorJoinDao {

	@Insert
	void insert(StudyDeviceSensorJoin studyDeviceSensorJoin);

	@Query("SELECT * FROM " + DeviceSensor.TABLE_NAME + " INNER JOIN " + StudyDeviceSensorJoin.TABLE_NAME + " ON name=deviceSensorId WHERE studyId=:id")
	List<DeviceSensor> getDeviceSensorsForStudy(long id);
}
