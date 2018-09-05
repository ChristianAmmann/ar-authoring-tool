package ncxp.de.arauthoringtool.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.arauthoringtool.model.data.TestPerson;

@Dao
public interface TestPersonDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long insert(TestPerson testPerson);

	@Insert
	long[] insertAll(TestPerson[] testPerson);

	@Query("SELECT * FROM " + TestPerson.TABLE_NAME + " WHERE " + TestPerson.COLUMN_STUDY_ID + "=:studyId")
	List<TestPerson> selectTestPersonFromStudy(long studyId);

	@Query("DELETE FROM " + TestPerson.TABLE_NAME + " WHERE " + TestPerson.COLUMN_ID + "= :id")
	int deleteById(long id);

	@Query("SELECT count(*) FROM " + TestPerson.TABLE_NAME + " WHERE " + TestPerson.COLUMN_STUDY_ID + "= :id")
	int getAmountOfTestPersons(long id);
}
