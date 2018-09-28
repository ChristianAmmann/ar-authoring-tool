package ncxp.de.arauthoringtool.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.arauthoringtool.model.data.ArObject;

@Dao
public interface ArObjectDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long[] insertAll(ArObject[] arImageToObjectRelations);

	@Query("DELETE FROM " + ArObject.TABLE_NAME + " WHERE " + ArObject.COLUMN_AR_SCENE_ID + "= :arSceneId")
	int deleteByArScene(long arSceneId);

	@Query("SELECT * FROM " + ArObject.TABLE_NAME + " WHERE " + ArObject.COLUMN_AR_SCENE_ID + "= :arSceneId")
	List<ArObject> selectAll(long arSceneId);
}
