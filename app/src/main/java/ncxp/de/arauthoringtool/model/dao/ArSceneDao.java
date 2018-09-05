package ncxp.de.arauthoringtool.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ncxp.de.arauthoringtool.model.data.ARScene;

@Dao
public interface ArSceneDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long insert(ARScene arScene);

	@Insert
	long[] insertAll(ARScene[] arScenes);

	@Query("SELECT * FROM " + ARScene.TABLE_NAME)
	List<ARScene> selectAll();

	@Query("DELETE FROM " + ARScene.TABLE_NAME + " WHERE " + ARScene.COLUMN_ID + "= :id")
	int deleteById(long id);

	@Update
	int update(ARScene arScene);
}
