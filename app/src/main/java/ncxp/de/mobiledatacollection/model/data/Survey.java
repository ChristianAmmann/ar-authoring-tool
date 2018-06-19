package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Survey.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId", onDelete = CASCADE))
public class Survey {

	public static final String TABLE_NAME = "surveys";

	@PrimaryKey
	private final long   id;
	private       int    platformId;
	private       String name;
	private       String description;
	private final long   studyId;

	public Survey(long id, long studyId, int platformId, String name, String description) {
		this.id = id;
		this.studyId = studyId;
		this.platformId = platformId;
		this.name = name;
		this.description = description;
	}

	public long getId() {
		return id;
	}


	public long getStudyId() {
		return studyId;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
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
}
