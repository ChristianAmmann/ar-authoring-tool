package ncxp.de.mobiledatacollection.model.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
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
