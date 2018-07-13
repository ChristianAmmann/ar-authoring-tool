package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.TestPerson;

@Dao
public interface TestPersonDao {

	@Insert
	long insert(TestPerson testPerson);

	@Insert
	long[] insertAll(TestPerson[] testPerson);

	@Query("SELECT * FROM " + TestPerson.TABLE_NAME + " WHERE " + TestPerson.COLUMN_STUDY_ID + "=:studyId")
	List<TestPerson> selectTestPersonFromStudy(long studyId);
}
