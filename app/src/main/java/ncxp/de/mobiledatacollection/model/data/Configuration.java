package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(tableName = Configuration.TABLE_NAME)
public class Configuration {

	public static final String TABLE_NAME = "study_configuration";

	private long               id;
	private List<DeviceSensor> sensors;
	private List<Survey>       surveys;
}
