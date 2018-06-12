package ncxp.de.mobiledatacollection.model.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ncxp.de.mobiledatacollection.model.data.Survey;

@Dao
public interface SurveyDao {

	@Insert
	long insert(Survey survey);

	@Insert
	long[] insertAll(Survey[] surveys);

	@Query("SELECT * FROM " + Survey.TABLE_NAME)
	Cursor selectAll();

	@Query("SELECT * FROM " + Survey.TABLE_NAME + " WHERE id = :id")
	Cursor selectById(long id);

	@Query("DELETE FROM " + Survey.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Update
	int update(Survey survey);
}
