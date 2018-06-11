package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import ncxp.de.mobiledatacollection.model.data.DeviceSensor;

@Dao
public interface DeviceSensorDao {

	@Insert
	long insert(DeviceSensor sensor);

	@Insert
	long[] insertAll(DeviceSensor[] sensors);

	@Query("SELECT * FROM " + DeviceSensor.TABLE_NAME)
	Cursor selectAll();

	@Query("SELECT * FROM " + DeviceSensor.TABLE_NAME + " WHERE id = :id")
	Cursor selectById(long id);

	@Query("DELETE FROM " + DeviceSensor.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Update
	int update(DeviceSensor sensor);

}
