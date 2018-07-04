package ncxp.de.mobiledatacollection.model.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.DeviceSensorDao;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;

public class DeviceSensorRepository {

	private final DeviceSensorDao deviceSensorDao;


	public DeviceSensorRepository(DeviceSensorDao deviceSensorDao) {
		this.deviceSensorDao = deviceSensorDao;
	}

	public LiveData<List<DeviceSensor>> getDeviceSensors() {
		return deviceSensorDao.selectAll();
	}

	public long saveDeviceSensor(DeviceSensor deviceSensor) {
		return deviceSensorDao.insert(deviceSensor);
	}

}
