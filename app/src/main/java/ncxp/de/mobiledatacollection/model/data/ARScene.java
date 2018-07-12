package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = ARScene.TABLE_NAME)
public class ARScene {

	public static final String TABLE_NAME = "ARScene";

	@PrimaryKey(autoGenerate = true)
	private int    id;
	private String name;
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
