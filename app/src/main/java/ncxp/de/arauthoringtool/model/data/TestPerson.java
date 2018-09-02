package ncxp.de.arauthoringtool.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = TestPerson.TABLE_NAME, foreignKeys = @ForeignKey(entity = Study.class, parentColumns = Study.COLUMN_ID, childColumns = TestPerson.COLUMN_STUDY_ID, onDelete =
		CASCADE))
public class TestPerson implements Parcelable {

	public static final String TABLE_NAME      = "Subject";
	public static final String COLUMN_ID       = "id";
	public static final String COLUMN_STUDY_ID = "study_id";

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private Long            id;
	@ColumnInfo(name = COLUMN_STUDY_ID)
	private Long            studyId;
	@Ignore
	private TestPersonState state;
	@Ignore
	private List<Data>      dataList;

	public TestPerson() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStudyId() {
		return studyId;
	}

	public void setStudyId(Long studyId) {
		this.studyId = studyId;
	}


	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}

	public static String[] getCSVHeader() {
		return new String[]{COLUMN_ID};
	}

	public String[] geCsvValues() {
		return new String[]{id.toString()};
	}


	protected TestPerson(Parcel in) {
		id = in.readByte() == 0x00 ? null : in.readLong();
		studyId = in.readByte() == 0x00 ? null : in.readLong();
		state = (TestPersonState) in.readValue(TestPersonState.class.getClassLoader());
		if (in.readByte() == 0x01) {
			dataList = new ArrayList<Data>();
			in.readList(dataList, Data.class.getClassLoader());
		} else {
			dataList = null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (id == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeLong(id);
		}
		if (studyId == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeLong(studyId);
		}
		dest.writeValue(state);
		if (dataList == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeList(dataList);
		}
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<TestPerson> CREATOR = new Parcelable.Creator<TestPerson>() {
		@Override
		public TestPerson createFromParcel(Parcel in) {
			return new TestPerson(in);
		}

		@Override
		public TestPerson[] newArray(int size) {
			return new TestPerson[size];
		}
	};
}
