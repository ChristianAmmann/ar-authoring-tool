package ncxp.de.arauthoringtool.view.areditor.util;

public enum ScaleTechnique {
	PINCH(0, "Skalierungsgeste"),
	WIDGET_3D(1, "3D-Widget"),
	NONE(2, "Skalierung unterbinden");

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
