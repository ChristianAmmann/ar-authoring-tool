package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.TestPerson;

@Dao
public interface TestPersonDao {

	@Insert
	long insert(TestPerson testPerson);

	@Insert
	long[] insertAll(TestPerson[] testPerson);

	@Query("SELECT * FROM " + TestPerson.TABLE_NAME)
	List<TestPerson> selectAll();

	@Query("SELECT * FROM " + TestPerson.TABLE_NAME + " WHERE id = :id")
	TestPerson selectById(long id);

	@Query("DELETE FROM " + TestPerson.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Update
	int update(TestPerson testPerson);

}
