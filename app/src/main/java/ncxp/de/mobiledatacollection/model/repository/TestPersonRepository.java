package ncxp.de.mobiledatacollection.model.repository;

import ncxp.de.mobiledatacollection.model.dao.TestPersonDao;
import ncxp.de.mobiledatacollection.model.data.TestPerson;

public class TestPersonRepository {
	private final TestPersonDao testPersonDao;

	public TestPersonRepository(TestPersonDao testPersonDao) {
		this.testPersonDao = testPersonDao;
	}

	public long saveTestPerson(TestPerson testPerson) {
		return testPersonDao.insert(testPerson);
	}
}
