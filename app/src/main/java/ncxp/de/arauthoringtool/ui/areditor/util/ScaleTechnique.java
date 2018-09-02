package ncxp.de.arauthoringtool.ui.areditor.util;

public enum ScaleTechnique {
	PINCH(0, "Geste Pinch"),
	WIDGET_3D(1, "3D-Widget"),
	NONE(2, "Keine");

	private int    position;
	private String name;

	ScaleTechnique(int position, String name) {
		this.position = position;
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	public static ScaleTechnique getTechnique(int position) {
		for (ScaleTechnique technique : ScaleTechnique.values()) {
			if (technique.getPosition() == position) {
				return technique;
			}
		}
		return null;
	}
}
