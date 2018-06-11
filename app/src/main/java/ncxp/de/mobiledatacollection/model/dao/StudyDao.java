package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import ncxp.de.mobiledatacollection.model.data.Study;

@Dao
public interface StudyDao {

	@Insert
	long insert(Study study);

	@Insert
	long[] insertAll(Study[] studies);

	@Query("SELECT * FROM " + Study.TABLE_NAME)
	Cursor selectAll();

	@Query("SELECT * FROM " + Study.TABLE_NAME + " WHERE id = :id")
	Cursor selectById(long id);

	@Query("DELETE FROM " + Study.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Update
	int update(Study study);

}
