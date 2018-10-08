package ncxp.de.arauthoringtool.view.areditor.util;

public enum SelectionTechnique {
	RAYCASTING(0, "Tippen"),
	CROSSHAIR(2, "Fadenkreuz"),
	NONE(1, "Selektion unterbinden");

	private int    position;
	private String name;

	SelectionTechnique(int position, String name) {
		this.position = position;
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	public static SelectionTechnique getTechnique(int position) {
		for (SelectionTechnique technique : SelectionTechnique.values()) {
			if (technique.getPosition() == position) {
				return technique;
			}
		}
		return null;
	}
}
