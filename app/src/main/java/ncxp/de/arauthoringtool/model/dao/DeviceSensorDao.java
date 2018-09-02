package ncxp.de.arauthoringtool.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.arauthoringtool.model.data.DeviceSensor;

@Dao
public interface DeviceSensorDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long insert(DeviceSensor deviceSensor);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long[] insertAll(DeviceSensor[] deviceSensors);

	@Query("SELECT * FROM " + DeviceSensor.TABLE_NAME)
	LiveData<List<DeviceSensor>> selectAll();
}
