package ncxp.de.mobiledatacollection.ui.areditor.util;

public enum RotationTechnique {
	TWO_FINGER(0, "Geste Two Finger"),
	WIDGET_3D(1, "3D-Widget"),
	NONE(2, "Keine");

	private int    position;
	private String name;

	RotationTechnique(int position, String name) {
		this.position = position;
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	public static RotationTechnique getTechnique(int position) {
		for (RotationTechnique technique : RotationTechnique.values()) {
			if (technique.getPosition() == position) {
				return technique;
			}
		}
		return null;
	}
}
