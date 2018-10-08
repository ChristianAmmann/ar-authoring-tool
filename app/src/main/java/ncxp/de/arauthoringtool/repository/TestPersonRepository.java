package ncxp.de.arauthoringtool.repository;

import java.util.List;

import ncxp.de.arauthoringtool.model.dao.TestPersonDao;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.TestPerson;

public class TestPersonRepository {
	private final TestPersonDao testPersonDao;

	public TestPersonRepository(TestPersonDao testPersonDao) {
		this.testPersonDao = testPersonDao;
	}

	public long saveTestPerson(TestPerson testPerson) {
		return testPersonDao.insert(testPerson);
	}

	public List<TestPerson> getTestPersonForStudy(Study study) {
		return testPersonDao.selectTestPersonFromStudy(study.getId());
	}

	public int getAmountOfTestPersons(Study study) {
		return testPersonDao.getAmountOfTestPersons(study.getId());
	}

	public void removeTestPerson(TestPerson testPerson) {
		testPersonDao.deleteById(testPerson.getId());
	}
}
