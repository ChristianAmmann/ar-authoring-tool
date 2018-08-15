package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = ARScene.TABLE_NAME)
public class ARScene implements Parcelable {

	public static final String TABLE_NAME         = "ARScene";
	public static final String COLUMN_ID          = "id";
	public static final String COLUMN_NAME        = "name";
	public static final String COLUMN_DESCRIPTION = "description";


	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private long                          id;
	@ColumnInfo(name = COLUMN_NAME)
	private String                        name;
	@ColumnInfo(name = COLUMN_DESCRIPTION)
	private String                        description;
	@Ignore
	private List<ArImageToObjectRelation> arImageObjects;


	public ARScene() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ArImageToObjectRelation> getArImageObjects() {
		return arImageObjects;
	}

	public void setArImageObjects(List<ArImageToObjectRelation> arImageObjects) {
		this.arImageObjects = arImageObjects;
	}

	protected ARScene(Parcel in) {
		id = in.readLong();
		name = in.readString();
		description = in.readString();
		if (in.readByte() == 0x01) {
			arImageObjects = new ArrayList<>();
			in.readList(arImageObjects, ArImageToObjectRelation.class.getClassLoader());
		} else {
			arImageObjects = null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(description);
		if (arImageObjects == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeList(arImageObjects);
		}
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<ARScene> CREATOR = new Parcelable.Creator<ARScene>() {
		@Override
		public ARScene createFromParcel(Parcel in) {
			return new ARScene(in);
		}

		@Override
		public ARScene[] newArray(int size) {
			return new ARScene[size];
		}
	};
}
