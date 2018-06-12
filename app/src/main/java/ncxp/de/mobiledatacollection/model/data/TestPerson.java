package ncxp.de.mobiledatacollection.model.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = TestPerson.TABLE_NAME)
public class TestPerson {

	public static final String TABLE_NAME = "subjects";

	@PrimaryKey
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
