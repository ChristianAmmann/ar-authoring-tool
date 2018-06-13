package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = Configuration.TABLE_NAME)
public class Configuration {

	public static final String TABLE_NAME = "study_configuration";

	@PrimaryKey
	private long id;
	/*@TypeConverter(DataConverter.class)
	private List<DeviceSensor> sensors;
	@TypeConverter(DataConverter.class)
	private List<Survey>       surveys;*/

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
