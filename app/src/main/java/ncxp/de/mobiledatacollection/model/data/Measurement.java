package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = Measurement.TABLE_NAME)
public class Measurement {

	public static final String TABLE_NAME = "Measurement";

	@PrimaryKey(autoGenerate = true)
	private long   id;
	private String name;

	public Measurement(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
