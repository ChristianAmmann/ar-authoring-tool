package ncxp.de.arauthoringtool.model.repository;

import java.util.List;

import ncxp.de.arauthoringtool.model.dao.StudyDeviceSensorJoinDao;
import ncxp.de.arauthoringtool.model.data.DeviceSensor;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.StudyDeviceSensorJoin;

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

	public int updateStudyDeviceSensorJoin(StudyDeviceSensorJoin studyDeviceSensorJoin) {
		return deviceSensorJoinDao.update(studyDeviceSensorJoin);
	}

	public int removeStudyDeviceSensorJoin(StudyDeviceSensorJoin studyDeviceSensorJoin) {
		return deviceSensorJoinDao.delete(studyDeviceSensorJoin);
	}
}
