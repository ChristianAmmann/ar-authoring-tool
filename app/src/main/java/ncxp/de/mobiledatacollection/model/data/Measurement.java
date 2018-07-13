package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = Measurement.TABLE_NAME)
public class Measurement {

	public static final String TABLE_NAME  = "Measurement";
	public static final String COLUMN_ID   = "id";
	public static final String COLUMN_NAME = "name";

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private long   id;
	@ColumnInfo(name = COLUMN_NAME)
	private String name;

	public Measurement() {
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
