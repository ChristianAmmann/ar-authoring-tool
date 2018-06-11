package ncxp.de.mobiledatacollection.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ncxp.de.mobiledatacollection.model.dao.ConfigurationDao;
import ncxp.de.mobiledatacollection.model.dao.DeviceSensorDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.dao.TestPersonDao;
import ncxp.de.mobiledatacollection.model.data.Configuration;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.Survey;

@Database(entities = {Study.class, Configuration.class, DeviceSensor.class, Survey.class}, version = 1)
public abstract class StudyDatabase extends RoomDatabase {

	@SuppressWarnings("WeakerAccess")
	public abstract StudyDao study();

	@SuppressWarnings("WeakerAccess")
	public abstract SurveyDao survey();

	@SuppressWarnings("WeakerAccess")
	public abstract DeviceSensorDao sensor();

	@SuppressWarnings("WeakerAccess")
	public abstract ConfigurationDao configuration();

	@SuppressWarnings("WeakerAccess")
	public abstract TestPersonDao testPerson();

	/** The only instance */
	private static StudyDatabase instance;

	public static synchronized StudyDatabase getInstance(Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context.getApplicationContext(), StudyDatabase.class, "studies").build();
		}
		return instance;
	}
}
