package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(tableName = DeviceSensor.TABLE_NAME)
public class DeviceSensor {

	public static final String TABLE_NAME = "sensors";

	@PrimaryKey
	private long   id;
	private String name;
}
