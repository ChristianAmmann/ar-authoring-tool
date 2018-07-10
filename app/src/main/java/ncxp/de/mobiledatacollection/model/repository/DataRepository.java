package ncxp.de.mobiledatacollection.model.repository;

import ncxp.de.mobiledatacollection.model.dao.DataDao;
import ncxp.de.mobiledatacollection.model.data.Data;

public class DataRepository {
	private final DataDao dataDao;

	public DataRepository(DataDao dataDao) {
		this.dataDao = dataDao;
	}

	public long saveData(Data data) {
		return dataDao.insert(data);
	}
}
