package ncxp.de.arauthoringtool.ui.areditor.util;

public enum RotationTechnique {
	TWO_FINGER(0, "Rotationsgeste"),
	WIDGET_3D(1, "3D-Widget"),
	NONE(2, "Rotation unterbinden");

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
