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
@Entity(tableName = Survey.TABLE_NAME)
public class Survey {

	public static final String TABLE_NAME = "survies";

	@PrimaryKey
	private long   id;
	private int    platformId;
	private String name;

}
