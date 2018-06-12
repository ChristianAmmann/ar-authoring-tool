package ncxp.de.mobiledatacollection.model.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ncxp.de.mobiledatacollection.model.data.TestPerson;

@Dao
public interface TestPersonDao {

	@Insert
	long insert(TestPerson testPerson);

	@Insert
	long[] insertAll(TestPerson[] testPerson);

	@Query("SELECT * FROM " + TestPerson.TABLE_NAME)
	Cursor selectAll();

	@Query("SELECT * FROM " + TestPerson.TABLE_NAME + " WHERE id = :id")
	Cursor selectById(long id);

	@Query("DELETE FROM " + TestPerson.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Update
	int update(TestPerson testPerson);

}
