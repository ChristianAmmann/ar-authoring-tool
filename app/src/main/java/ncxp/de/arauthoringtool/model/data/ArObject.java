package ncxp.de.arauthoringtool.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

@Entity(tableName = ArObject.TABLE_NAME)
public class ArObject implements Parcelable {

	public static final String TABLE_NAME         = "ARObjects";
	public static final String COLUMN_ID          = "id";
	public static final String COLUMN_AR_SCENE_ID = "arSceneId";
	public static final String COLUMN_IMAGE_NAME  = "imageName";
	public static final String COLUMN_SCALE_X     = "scaleX";
	public static final String COLUMN_SCALE_Y     = "scaleY";
	public static final String COLUMN_SCALE_Z     = "scaleZ";
	public static final String COLUMN_ROTATION_X  = "rotationX";
	public static final String COLUMN_ROTATION_Y  = "rotationY";
	public static final String COLUMN_ROTATION_Z  = "rotationZ";
	public static final String COLUMN_ROTATION_W  = "rotationW";

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private long   arObjectId;
	@ColumnInfo(name = COLUMN_AR_SCENE_ID)
	@NonNull
	private long   arSceneId;
	@ColumnInfo(name = COLUMN_IMAGE_NAME)
	@NonNull
	private String imageName;
	@ColumnInfo(name = COLUMN_SCALE_X)
	private float  scaleX;
	@ColumnInfo(name = COLUMN_SCALE_Y)
	private float  scaleY;
	@ColumnInfo(name = COLUMN_SCALE_Z)
	private float  scaleZ;
	@ColumnInfo(name = COLUMN_ROTATION_X)
	private float  rotationX;
	@ColumnInfo(name = COLUMN_ROTATION_Y)
	private float  rotationY;
	@ColumnInfo(name = COLUMN_ROTATION_Z)
	private float  rotationZ;
	@ColumnInfo(name = COLUMN_ROTATION_W)
	private float  rotationW;


	public ArObject() {
	}

	@Ignore
	public ArObject(String imageName, Vector3 scale, Quaternion rotation) {
		this.imageName = imageName;
		this.scaleX = scale.x;
		this.scaleY = scale.y;
		this.scaleZ = scale.z;
		this.rotationX = rotation.x;
		this.rotationY = rotation.y;
		this.rotationZ = rotation.z;
		this.rotationW = rotation.w;
	}

	@Ignore
	public ArObject(long arObjectId, String imageName, Vector3 scale, Quaternion rotation) {
		this.arObjectId = arObjectId;
		this.imageName = imageName;
		this.scaleX = scale.x;
		this.scaleY = scale.y;
		this.scaleZ = scale.z;
		this.rotationX = rotation.x;
		this.rotationY = rotation.y;
		this.rotationZ = rotation.z;
		this.rotationW = rotation.w;
	}

	public long getArObjectId() {
		return arObjectId;
	}

	public void setArObjectId(long arObjectId) {
		this.arObjectId = arObjectId;
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

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getScaleZ() {
		return scaleZ;
	}

	public void setScaleZ(float scaleZ) {
		this.scaleZ = scaleZ;
	}

	public float getRotationX() {
		return rotationX;
	}

	public void setRotationX(float rotationX) {
		this.rotationX = rotationX;
	}

	public float getRotationY() {
		return rotationY;
	}

	public void setRotationY(float rotationY) {
		this.rotationY = rotationY;
	}

	public float getRotationZ() {
		return rotationZ;
	}

	public void setRotationZ(float rotationZ) {
		this.rotationZ = rotationZ;
	}

	public float getRotationW() {
		return rotationW;
	}

	public void setRotationW(float rotationW) {
		this.rotationW = rotationW;
	}

	public Quaternion getRotation() {
		return new Quaternion(rotationX, rotationY, rotationZ, rotationW);
	}

	public Vector3 getScale() {
		return new Vector3(scaleX, scaleY, scaleZ);
	}

	protected ArObject(Parcel in) {
		arObjectId = in.readLong();
		arSceneId = in.readLong();
		imageName = in.readString();
		scaleX = in.readFloat();
		scaleY = in.readFloat();
		scaleZ = in.readFloat();
		rotationX = in.readFloat();
		rotationY = in.readFloat();
		rotationZ = in.readFloat();
		rotationW = in.readFloat();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(arObjectId);
		dest.writeLong(arSceneId);
		dest.writeString(imageName);
		dest.writeFloat(scaleX);
		dest.writeFloat(scaleY);
		dest.writeFloat(scaleZ);
		dest.writeFloat(rotationX);
		dest.writeFloat(rotationY);
		dest.writeFloat(rotationZ);
		dest.writeFloat(rotationW);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<ArObject> CREATOR = new Parcelable.Creator<ArObject>() {
		@Override
		public ArObject createFromParcel(Parcel in) {
			return new ArObject(in);
		}

		@Override
		public ArObject[] newArray(int size) {
			return new ArObject[size];
		}
	};
}
