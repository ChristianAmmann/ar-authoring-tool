package ncxp.de.arauthoringtool.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.arauthoringtool.model.data.ArImageToObjectRelation;

@Dao
public interface ArImageToObjectRelationDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	long[] insertAll(ArImageToObjectRelation[] arImageToObjectRelations);

	@Query("DELETE FROM " + ArImageToObjectRelation.TABLE_NAME + " WHERE " + ArImageToObjectRelation.COLUMN_AR_SCENE_ID + "= :arSceneId")
	int deleteByArScene(long arSceneId);

	@Query("SELECT * FROM " + ArImageToObjectRelation.TABLE_NAME + " WHERE " + ArImageToObjectRelation.COLUMN_AR_SCENE_ID + "= :arSceneId")
	List<ArImageToObjectRelation> selectAll(long arSceneId);
}
