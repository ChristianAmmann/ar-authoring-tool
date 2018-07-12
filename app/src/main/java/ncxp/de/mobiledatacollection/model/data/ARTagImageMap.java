package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;

@Entity
public class ARTagImageMap {

	private int    arTagId;
	private String imageName;

	public ARTagImageMap() {
	}

	public int getArTagId() {
		return arTagId;
	}

	public void setArTagId(int arTagId) {
		this.arTagId = arTagId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
