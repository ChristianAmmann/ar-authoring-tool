package ncxp.de.mobiledatacollection.model.repository;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.StudyMeasurementJoinDao;
import ncxp.de.mobiledatacollection.model.data.Measurement;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.StudyMeasurementJoin;

public class StudyMeasurementJoinRepository {

	private final StudyMeasurementJoinDao studyMeasurementJoinDao;

	public StudyMeasurementJoinRepository(StudyMeasurementJoinDao studyMeasurementJoinDao) {
		this.studyMeasurementJoinDao = studyMeasurementJoinDao;
	}

	public void saveStudyMeasurementJoin(StudyMeasurementJoin studyMeasurementJoin) {
		studyMeasurementJoinDao.insert(studyMeasurementJoin);
	}

	public List<Measurement> getMeasurementsForStudy(Study study) {
		return studyMeasurementJoinDao.getMeasurementsForStudy(study.getId());
	}
}
