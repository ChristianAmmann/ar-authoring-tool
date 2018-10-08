package ncxp.de.arauthoringtool.view.study.adapter;

public class OptionItem {
	private String     name;
	private String     description;
	private boolean    isActive = false;
	private OptionType type;

	public OptionItem(String name, String description, OptionType type) {
		this.name = name;
		this.description = description;
		this.type = type;
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public OptionType getType() {
		return type;
	}

	public void setType(OptionType type) {
		this.type = type;
	}
}
