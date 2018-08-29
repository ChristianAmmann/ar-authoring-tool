package ncxp.de.mobiledatacollection.ui.areditor.util;

public enum SelectionTechnique {
	RAYCASTING(0, "Raycasting"),
	CROSSHAIR(1, "Fadenkreuz"),
	NONE(2, "Keine");

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
