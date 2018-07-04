package ncxp.de.mobiledatacollection.model.repository;

import ncxp.de.mobiledatacollection.model.dao.StudyDeviceSensorJoinDao;
import ncxp.de.mobiledatacollection.model.data.StudyDeviceSensorJoin;

public class StudyDeviceSensorJoinRepository {

	private final StudyDeviceSensorJoinDao deviceSensorJoinDao;

	public StudyDeviceSensorJoinRepository(StudyDeviceSensorJoinDao deviceSensorJoinDao) {
		this.deviceSensorJoinDao = deviceSensorJoinDao;
	}

	public void saveStudyDeviceSensorJoin(StudyDeviceSensorJoin studyDeviceSensorJoin) {
		deviceSensorJoinDao.insert(studyDeviceSensorJoin);
	}
}
