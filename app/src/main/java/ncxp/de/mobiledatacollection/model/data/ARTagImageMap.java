package ncxp.de.mobiledatacollection.model.data;

import android.arch.persistence.room.Entity;

@Entity
public class ARTagImageMap {

	private int    arMarkerId;
	private String imageName;

	public ARTagImageMap() {
	}

	public int getArMarkerId() {
		return arMarkerId;
	}

	public void setArMarkerId(int arMarkerId) {
		this.arMarkerId = arMarkerId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
