package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.Data;

@Dao
public interface DataDao {

	@Insert
	long insert(Data data);

	@Insert
	long[] insertAll(Data[] data);

	@Query("SELECT * FROM " + Data.TABLE_NAME + " WHERE " + Data.COLUMN_TEST_PERSON_ID + "=:testPersonId")
	List<Data> selectDataFromTestPerson(long testPersonId);
}
