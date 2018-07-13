package ncxp.de.mobiledatacollection.model.repository;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.DataDao;
import ncxp.de.mobiledatacollection.model.data.Data;
import ncxp.de.mobiledatacollection.model.data.TestPerson;

public class DataRepository {
	private final DataDao dataDao;

	public DataRepository(DataDao dataDao) {
		this.dataDao = dataDao;
	}

	public long saveData(Data data) {
		return dataDao.insert(data);
	}

	public List<Data> getDataFromTestPerson(TestPerson person) {
		return dataDao.selectDataFromTestPerson(person.getId());
	}
}
