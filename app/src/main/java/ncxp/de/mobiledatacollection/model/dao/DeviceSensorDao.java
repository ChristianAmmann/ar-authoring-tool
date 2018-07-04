package ncxp.de.mobiledatacollection.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.DeviceSensor;

@Dao
public interface DeviceSensorDao {

	@Insert
	long insert(DeviceSensor deviceSensor);

	@Insert
	long[] insertAll(DeviceSensor[] deviceSensors);

	@Query("SELECT * FROM " + DeviceSensor.TABLE_NAME)
	LiveData<List<DeviceSensor>> selectAll();
}