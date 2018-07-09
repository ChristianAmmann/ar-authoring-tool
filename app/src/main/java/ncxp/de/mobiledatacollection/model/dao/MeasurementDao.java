package ncxp.de.mobiledatacollection.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.Measurement;

@Dao
public interface MeasurementDao {

	@Insert
	long insert(Measurement measurement);

	@Insert
	long[] insertAll(Measurement[] measurements);

	@Query("SELECT * FROM " + Measurement.TABLE_NAME)
	LiveData<List<Measurement>> selectAll();
}
