package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = Study.TABLE_NAME)
public class Study {

	public static final String TABLE_NAME = "studies";

	@PrimaryKey
	private long   id;
	private String name;

}
