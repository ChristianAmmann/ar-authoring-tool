package ncxp.de.arauthoringtool;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ncxp.de.arauthoringtool.model.dao.ArObjectDao;
import ncxp.de.arauthoringtool.model.dao.ArSceneDao;
import ncxp.de.arauthoringtool.model.dao.DataDao;
import ncxp.de.arauthoringtool.model.dao.DeviceSensorDao;
import ncxp.de.arauthoringtool.model.dao.StudyDao;
import ncxp.de.arauthoringtool.model.dao.StudyDeviceSensorJoinDao;
import ncxp.de.arauthoringtool.model.dao.SurveyDao;
import ncxp.de.arauthoringtool.model.dao.TestPersonDao;
import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.ArObject;
import ncxp.de.arauthoringtool.model.data.Data;
import ncxp.de.arauthoringtool.model.data.DeviceSensor;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.StudyDeviceSensorJoin;
import ncxp.de.arauthoringtool.model.data.Survey;
import ncxp.de.arauthoringtool.model.data.TestPerson;

@Database(entities = {
		Study.class,
		Survey.class,
		TestPerson.class,
		DeviceSensor.class,
		Data.class,
		ARScene.class,
		ArObject.class,
		StudyDeviceSensorJoin.class}, version = 1, exportSchema = false)
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

	public abstract ArObjectDao arImageToObjectRelationDao();

	public static StudyDatabase getInstance(final Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context, StudyDatabase.class, DATABASE_NAME).build();
		}
		return instance;
	}
}
