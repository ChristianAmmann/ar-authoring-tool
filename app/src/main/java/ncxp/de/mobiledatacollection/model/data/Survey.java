package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = Survey.TABLE_NAME)
public class Survey {

	public static final String TABLE_NAME = "surveys";

	@PrimaryKey
	private long   id;
	private int    platformId;
	private String name;
	private String description;

	public Survey(long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
