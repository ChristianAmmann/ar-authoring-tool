package ncxp.de.mobiledatacollection.model.repository;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.TestPersonDao;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.TestPerson;

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

	public void removeTestPerson(TestPerson testPerson) {
		testPersonDao.deleteById(testPerson.getId());
	}
}
