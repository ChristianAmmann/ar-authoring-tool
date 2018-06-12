package ncxp.de.mobiledatacollection.model.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ncxp.de.mobiledatacollection.model.data.Configuration;

@Dao
public interface ConfigurationDao {

	@Insert
	long insert(Configuration configuration);

	@Insert
	long[] insertAll(Configuration[] configurations);

	@Query("SELECT * FROM " + Configuration.TABLE_NAME)
	Cursor selectAll();

	@Query("SELECT * FROM " + Configuration.TABLE_NAME + " WHERE id = :id")
	Cursor selectById(long id);

	@Query("DELETE FROM " + Configuration.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Update
	int update(Configuration configuration);
}
