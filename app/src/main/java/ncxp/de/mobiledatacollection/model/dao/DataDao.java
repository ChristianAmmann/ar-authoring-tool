package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import ncxp.de.mobiledatacollection.model.data.Data;

@Dao
public interface DataDao {

	@Insert
	long insert(Data data);

	@Insert
	long[] insertAll(Data[] data);
}
