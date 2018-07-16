package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = ARScene.TABLE_NAME)
public class ARScene {

	public static final String TABLE_NAME         = "ARScene";
	public static final String COLUMN_ID          = "id";
	public static final String COLUMN_NAME        = "name";
	public static final String COLUMN_DESCRIPTION = "description";


	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = COLUMN_ID)
	private int    id;
	@ColumnInfo(name = COLUMN_NAME)
	private String name;
	@ColumnInfo(name = COLUMN_DESCRIPTION)
	private String description;
	@Ignore
	Map<Integer, String> arTagImageMap;

	public ARScene() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public Map<Integer, String> getArTagImageMap() {
		return arTagImageMap;
	}

	public void setArTagImageMap(Map<Integer, String> arTagImageMap) {
		this.arTagImageMap = arTagImageMap;
	}
}
