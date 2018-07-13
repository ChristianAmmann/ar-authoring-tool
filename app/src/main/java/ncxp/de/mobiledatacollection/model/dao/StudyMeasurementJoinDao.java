package ncxp.de.mobiledatacollection.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ncxp.de.mobiledatacollection.model.data.Measurement;
import ncxp.de.mobiledatacollection.model.data.StudyMeasurementJoin;

@Dao
public interface StudyMeasurementJoinDao {

	@Insert
	void insert(StudyMeasurementJoin studyMeasurementJoin);

	@Query("SELECT * FROM " + Measurement.TABLE_NAME + " INNER JOIN " + StudyMeasurementJoin.TABLE_NAME + " ON " + Measurement.COLUMN_ID + "=" + StudyMeasurementJoin
			.COLUMN_MEASUREMENT_ID + " WHERE " + StudyMeasurementJoin.COLUMN_STUDY_ID + "=:id")
	List<Measurement> getMeasurementsForStudy(long id);


}
