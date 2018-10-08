package ncxp.de.arauthoringtool.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ncxp.de.arauthoringtool.model.dao.SurveyDao;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.Survey;

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

	public void updateSurveys(List<Survey> surveys) {
		surveys.forEach(surveyDao::insert);
	}

	public void removeSurvey(Survey survey) {
		surveyDao.deleteById(survey.getId());
	}
}
