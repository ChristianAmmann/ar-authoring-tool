package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Survey.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = "id", childColumns = "studyId", onDelete = CASCADE))
public class Survey implements Parcelable {

	public static final String TABLE_NAME = "surveys";

	@PrimaryKey
	private long   id;
	private String projectDirectory;
	private String identifier;
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

	public String getProjectDirectory() {
		return projectDirectory;
	}

	public void setProjectDirectory(String projectDirectory) {
		this.projectDirectory = projectDirectory;
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

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	protected Survey(Parcel in) {
		id = in.readLong();
		projectDirectory = in.readString();
		identifier = in.readString();
		name = in.readString();
		description = in.readString();
		studyId = in.readLong();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(projectDirectory);
		dest.writeString(identifier);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeLong(studyId);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Survey> CREATOR = new Parcelable.Creator<Survey>() {
		@Override
		public Survey createFromParcel(Parcel in) {
			return new Survey(in);
		}

		@Override
		public Survey[] newArray(int size) {
			return new Survey[size];
		}
	};
}
