package ncxp.de.mobiledatacollection.model.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.Survey;

public class SurveyRepository {

	private final SurveyDao surveyDao;

	public SurveyRepository(SurveyDao surveyDao) {
		this.surveyDao = surveyDao;
	}

	public LiveData<Survey> getSurvey(long id) {
		return surveyDao.selectById(id);
	}

	public List<Survey> getSurveysFromStudy(Study study) {
		return surveyDao.getSurveysFromStudy(study.getId());
	}

	public void saveSurveys(List<Survey> surveys) {
		Survey[] surveysArray = new Survey[surveys.size()];
		surveysArray = surveys.toArray(surveysArray);
		surveyDao.insertAll(surveysArray);
	}
}
