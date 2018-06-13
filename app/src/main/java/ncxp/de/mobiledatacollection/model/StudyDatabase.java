package ncxp.de.mobiledatacollection.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

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
				new Study(1, "Title der Studie 1"),
				new Study(2, "Title der Studie 2"),
				new Study(3, "Title der Studie 3"),
				new Study(4, "Title der Studie 4"),
				new Study(5, "Title der Studie 5"),
				new Study(6, "Title der Studie 6"),
				new Study(7, "Title der Studie 7"),
				new Study(8, "Title der Studie 8"),
				new Study(9, "Title der Studie 9"),
				new Study(10, "Title der Studie 10"),
				new Study(11, "Title der Studie 11"),
				new Study(12, "Title der Studie 12")};
	}
}
