package ncxp.de.arauthoringtool.sensorlogger;

import ncxp.de.arauthoringtool.R;

public enum SensorGroup {
	MOTION(R.string.group_motion_sensor),
	POSITION(R.string.group_position_sensor),
	ENVIROMENT(R.string.group_environment_sensor);

	private int groupId;

	SensorGroup(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}
}
