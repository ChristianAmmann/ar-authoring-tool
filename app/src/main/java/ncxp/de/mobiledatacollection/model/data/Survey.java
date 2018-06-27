package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Survey.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId", onDelete = CASCADE))
public class Survey {

	public static final String TABLE_NAME = "surveys";

	@PrimaryKey
	private long   id;
	private String platformId;
	private String name;
	private String description;
	private long   studyId;

	public Survey() {
	}

	public long getId() {
		return id;
	}

	public long getStudyId() {
		return studyId;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getName() {
		return name;
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

	public void setId(long id) {
		this.id = id;
	}

	public void setStudyId(long studyId) {
		this.studyId = studyId;
	}
}
