package ncxp.de.mobiledatacollection.model.dao;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ncxp.de.mobiledatacollection.model.data.Study;

@Dao
public interface StudyDao {

	@Insert
	long insert(Study study);

	@Insert
	long[] insertAll(Study[] studies);

	@Query("SELECT * FROM " + Study.TABLE_NAME)
	LiveData<List<Study>> selectAll();

	@Query("SELECT * FROM " + Study.TABLE_NAME + " WHERE id = :id")
	LiveData<Study> selectById(long id);

	@Query("DELETE FROM " + Study.TABLE_NAME + " WHERE id = :id")
	int deleteById(long id);

	@Update
	int update(Study study);

}
