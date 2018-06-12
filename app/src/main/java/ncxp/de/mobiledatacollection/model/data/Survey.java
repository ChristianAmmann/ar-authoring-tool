package ncxp.de.mobiledatacollection.model.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = Survey.TABLE_NAME)
public class Survey {

	public static final String TABLE_NAME = "survies";

	@PrimaryKey
	private long   id;
	private int    platformId;
	private String name;

	public long getId() {
		return id;
	}

	public int getPlatformId() {
		return platformId;
	}

	public String getName() {
		return name;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public void setName(String name) {
		this.name = name;
	}
}
