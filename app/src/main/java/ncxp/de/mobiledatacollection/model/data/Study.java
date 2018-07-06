package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = Study.TABLE_NAME)
public class Study implements Parcelable {

	public static final String TABLE_NAME = "Study";

	@PrimaryKey(autoGenerate = true)
	private long   id;
	private String name;
	private String description;

	public Study() {
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(long id) {
		this.id = id;
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

	protected Study(Parcel in) {
		id = in.readLong();
		name = in.readString();
		description = in.readString();
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
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Study> CREATOR = new Parcelable.Creator<Study>() {
		@Override
		public Study createFromParcel(Parcel in) {
			return new Study(in);
		}

		@Override
		public Study[] newArray(int size) {
			return new Study[size];
		}
	};
}


