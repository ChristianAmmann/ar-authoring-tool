package ncxp.de.arauthoringtool.repository;


import android.arch.lifecycle.LiveData;

import java.util.List;

import ncxp.de.arauthoringtool.model.dao.StudyDao;
import ncxp.de.arauthoringtool.model.data.Study;

public class StudyRepository {

	private final StudyDao studyDao;

	public StudyRepository(StudyDao studyDao) {
		this.studyDao = studyDao;
	}

	public List<Study> getStudies() {
		return studyDao.selectAll();
	}

	public LiveData<Study> getStudy(long id) {
		return studyDao.selectById(id);
	}

	public long saveStudy(Study study) {
		return studyDao.insert(study);
	}

	public long updateStudy(Study study) {
		return studyDao.update(study);
	}

	public void deleteStudy(Study study) {
		studyDao.deleteById(study.getId());
	}
}
