package ncxp.de.mobiledatacollection.model;

import android.content.Context;

import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import ncxp.de.mobiledatacollection.model.dao.ConfigurationDao;
import ncxp.de.mobiledatacollection.model.dao.DeviceSensorDao;
import ncxp.de.mobiledatacollection.model.dao.StudyDao;
import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.dao.TestPersonDao;
import ncxp.de.mobiledatacollection.model.data.Configuration;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.data.TestPerson;

@Database(entities = {Study.class, Configuration.class, DeviceSensor.class, Survey.class, TestPerson.class}, version = 1, exportSchema = false)
public abstract class StudyDatabase extends RoomDatabase {

	private static StudyDatabase instance;

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

	public static StudyDatabase getInstance(final Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context, StudyDatabase.class, "studies.db").addCallback(new Callback() {
				@Override
				public void onCreate(@NonNull SupportSQLiteDatabase db) {
					super.onCreate(db);
					Executors.newSingleThreadScheduledExecutor().execute(() -> getInstance(context).study().insertAll(populateData()));
				}
			}).build();
		}
		return instance;
	}

	private static Study[] populateData() {
		return new Study[]{
				new Study(1, "title1"), new Study(2, "title2"), new Study(3, "title3"), new Study(4, "title4"), new Study(5, "title5")};
	}
}
