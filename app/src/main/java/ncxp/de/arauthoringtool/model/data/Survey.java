package ncxp.de.arauthoringtool.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = Survey.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = Study.COLUMN_ID, childColumns = Survey.COLUMN_STUDY_ID, onDelete = CASCADE))
public class Survey implements Parcelable {

	public static final String TABLE_NAME               = "Survey";
	public static final String COLUMN_ID                = "id";
	public static final String COLUMN_PROJECT_DIRECTORY = "project_directory";
	public static final String COLUMN_IDENTIFIER        = "identifier";
	public static final String COLUMN_NAME              = "name";
	public static final String COLUMN_DESCRIPTION       = "description";
	public static final String COLUMN_STUDY_ID          = "study_id";

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private Long   id;
	@ColumnInfo(name = COLUMN_PROJECT_DIRECTORY)
	private String projectDirectory;
	@ColumnInfo(name = COLUMN_IDENTIFIER)
	private String identifier;
	@ColumnInfo(name = COLUMN_NAME)
	private String name;
	@ColumnInfo(name = COLUMN_DESCRIPTION)
	private String description;
	@ColumnInfo(name = COLUMN_STUDY_ID)
	private Long   studyId;

	public Survey() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProjectDirectory() {
		return projectDirectory;
	}

	public void setProjectDirectory(String projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public Long getStudyId() {
		return studyId;
	}

	public void setStudyId(Long studyId) {
		this.studyId = studyId;
	}

	public String[] getCsvValues() {
		return new String[]{id.toString(), name, description, projectDirectory, identifier};
	}

	public static String[] getCSVHeader() {
		return new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_PROJECT_DIRECTORY, COLUMN_IDENTIFIER};
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
