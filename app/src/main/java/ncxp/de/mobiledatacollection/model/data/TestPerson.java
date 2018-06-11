package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(tableName = TestPerson.TABLE_NAME)
public class TestPerson {

	public static final String TABLE_NAME = "subjects";

	@PrimaryKey
	private long id;
}
