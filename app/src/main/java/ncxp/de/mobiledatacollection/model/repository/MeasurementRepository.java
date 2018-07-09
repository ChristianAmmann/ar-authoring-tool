package ncxp.de.mobiledatacollection.model.repository;

import ncxp.de.mobiledatacollection.model.dao.MeasurementDao;
import ncxp.de.mobiledatacollection.model.data.Measurement;

public class MeasurementRepository {
	private final MeasurementDao measurementDao;


	public MeasurementRepository(MeasurementDao measurementDao) {
		this.measurementDao = measurementDao;
	}

	public long saveMeasurement(Measurement measurement) {
		return measurementDao.insert(measurement);
	}
}
