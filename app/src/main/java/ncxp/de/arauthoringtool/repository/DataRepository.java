package ncxp.de.arauthoringtool.repository;

import java.util.List;

import ncxp.de.arauthoringtool.model.dao.DataDao;
import ncxp.de.arauthoringtool.model.data.Data;
import ncxp.de.arauthoringtool.model.data.TestPerson;

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
