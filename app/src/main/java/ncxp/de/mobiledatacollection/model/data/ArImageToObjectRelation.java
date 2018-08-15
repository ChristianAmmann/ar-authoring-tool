package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = ArImageToObjectRelation.TABLE_NAME, primaryKeys = {ArImageToObjectRelation.COLUMN_AR_SCENE_ID, ArImageToObjectRelation.COLUMN_IMAGE_NAME})
public class ArImageToObjectRelation implements Parcelable {

	public static final String TABLE_NAME          = "ARImageToObject";
	public static final String COLUMN_AR_SCENE_ID  = "arSceneId";
	public static final String COLUMN_AR_MARKER_ID = "arMarkerId";
	public static final String COLUMN_IMAGE_NAME   = "imageName";

	@ColumnInfo(name = COLUMN_AR_SCENE_ID)
	@NonNull
	private long   arSceneId;
	@ColumnInfo(name = COLUMN_IMAGE_NAME)
	@NonNull
	private String imageName;
	@ColumnInfo(name = COLUMN_AR_MARKER_ID)
	private String arMarkerId;


	public ArImageToObjectRelation() {
	}

	@Ignore
	public ArImageToObjectRelation(String arMarkerId, String imageName) {
		this.arMarkerId = arMarkerId;
		this.imageName = imageName;
	}

	public String getArMarkerId() {
		return arMarkerId;
	}

	public void setArMarkerId(String arMarkerId) {
		this.arMarkerId = arMarkerId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public long getArSceneId() {
		return arSceneId;
	}

	public void setArSceneId(long arSceneId) {
		this.arSceneId = arSceneId;
	}

	protected ArImageToObjectRelation(Parcel in) {
		arSceneId = in.readLong();
		imageName = in.readString();
		arMarkerId = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(arSceneId);
		dest.writeString(imageName);
		dest.writeString(arMarkerId);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<ArImageToObjectRelation> CREATOR = new Parcelable.Creator<ArImageToObjectRelation>() {
		@Override
		public ArImageToObjectRelation createFromParcel(Parcel in) {
			return new ArImageToObjectRelation(in);
		}

		@Override
		public ArImageToObjectRelation[] newArray(int size) {
			return new ArImageToObjectRelation[size];
		}
	};
}
