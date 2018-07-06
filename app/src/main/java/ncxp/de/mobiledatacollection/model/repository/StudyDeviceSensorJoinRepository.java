package ncxp.de.mobiledatacollection.model.repository;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.StudyDeviceSensorJoinDao;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.StudyDeviceSensorJoin;

public class StudyDeviceSensorJoinRepository {

	private final StudyDeviceSensorJoinDao deviceSensorJoinDao;

	public StudyDeviceSensorJoinRepository(StudyDeviceSensorJoinDao deviceSensorJoinDao) {
		this.deviceSensorJoinDao = deviceSensorJoinDao;
	}

	public void saveStudyDeviceSensorJoin(StudyDeviceSensorJoin studyDeviceSensorJoin) {
		deviceSensorJoinDao.insert(studyDeviceSensorJoin);
	}

	public List<DeviceSensor> getDeviceSensorsForStudy(Study study) {
		return deviceSensorJoinDao.getDeviceSensorsForStudy(study.getId());
	}
}
