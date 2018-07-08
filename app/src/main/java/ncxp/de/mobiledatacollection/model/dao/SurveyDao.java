package ncxp.de.mobiledatacollection.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.Survey;

@Dao
public interface SurveyDao {

	@Insert
	long insert(Survey survey);

	@Insert
	long[] insertAll(Survey[] surveys);

	@Query("SELECT * FROM " + Survey.TABLE_NAME)
	LiveData<List<Survey>> selectAll();

	@Query("SELECT * FROM " + Survey.TABLE_NAME + " WHERE id = :id")
	LiveData<Survey> selectById(long id);

	@Query("DELETE FROM " + Survey.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Query("SELECT * FROM " + Survey.TABLE_NAME + " WHERE studyId=:id")
	List<Survey> getSurveysFromStudy(long id);

	@Update
	int update(Survey survey);
}
