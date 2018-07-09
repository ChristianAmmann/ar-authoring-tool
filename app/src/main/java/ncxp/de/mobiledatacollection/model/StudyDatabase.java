package ncxp.de.mobiledatacollection.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ncxp.de.mobiledatacollection.model.dao.DeviceSensorDao;
import ncxp.de.mobiledatacollection.model.dao.MeasurementDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDeviceSensorJoinDao;
import ncxp.de.mobiledatacollection.model.dao.StudyMeasurementJoinDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.dao.TestPersonDao;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Measurement;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.StudyDeviceSensorJoin;
import ncxp.de.mobiledatacollection.model.data.StudyMeasurementJoin;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.data.TestPerson;

@Database(entities = {
		Study.class,
		Survey.class,
		TestPerson.class,
		DeviceSensor.class,
		StudyDeviceSensorJoin.class,
		Measurement.class,
		StudyMeasurementJoin.class}, version = 1, exportSchema = false)
public abstract class StudyDatabase extends RoomDatabase {

	public static final String DATABASE_NAME = "studies.db";

	private static StudyDatabase instance;

	@SuppressWarnings("WeakerAccess")
	public abstract StudyDao study();

	@SuppressWarnings("WeakerAccess")
	public abstract SurveyDao survey();

	@SuppressWarnings("WeakerAccess")
	public abstract TestPersonDao testPerson();

	public abstract DeviceSensorDao deviceSensor();

	public abstract StudyDeviceSensorJoinDao studyDeviceSensorJoinDao();

	public abstract StudyMeasurementJoinDao studyMeasurementJoinDao();

	public abstract MeasurementDao measurement();

	public static StudyDatabase getInstance(final Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context, StudyDatabase.class, DATABASE_NAME).build();
		}
		return instance;
	}
}
