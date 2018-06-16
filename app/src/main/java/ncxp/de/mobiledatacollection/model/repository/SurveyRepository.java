package ncxp.de.mobiledatacollection.model.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.SurveyDao;
import ncxp.de.mobiledatacollection.model.data.Survey;

public class SurveyRepository {

	private final SurveyDao surveyDao;

	public SurveyRepository(SurveyDao surveyDao) {
		this.surveyDao = surveyDao;
	}

	public LiveData<List<Survey>> getSurveys() {
		return surveyDao.selectAll();
	}

	public LiveData<Survey> getSurvey(long id) {
		return surveyDao.selectById(id);
	}
}
