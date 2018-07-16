package ncxp.de.mobiledatacollection.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ncxp.de.mobiledatacollection.model.dao.ArSceneDao;
import ncxp.de.mobiledatacollection.model.dao.DataDao;
import ncxp.de.mobiledatacollection.model.dao.DeviceSensorDao;
import ncxp.de.mobiledatacollection.model.dao.MeasurementDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDeviceSensorJoinDao;
import ncxp.de.mobiledatacollection.model.dao.StudyMeasurementJoinDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.dao.TestPersonDao;
import ncxp.de.mobiledatacollection.model.data.ARScene;
import ncxp.de.mobiledatacollection.model.data.Data;
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
		Measurement.class,
		Data.class,
		ARScene.class,
		StudyDeviceSensorJoin.class,
		StudyMeasurementJoin.class,}, version = 1, exportSchema = false)
public abstract class StudyDatabase extends RoomDatabase {

	public static final String DATABASE_NAME = "studies.db";

	private static StudyDatabase instance;

	public abstract StudyDao study();

	public abstract SurveyDao survey();

	public abstract TestPersonDao testPerson();

	public abstract DataDao dataDao();

	public abstract DeviceSensorDao deviceSensor();

	public abstract ArSceneDao arSceneDao();

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
