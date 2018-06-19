package ncxp.de.mobiledatacollection.datalogger;

import ncxp.de.mobiledatacollection.R;

public enum SensorGroup {
	MOTION(R.string.group_motion_sensor),
	POSITION(R.string.group_position_sensor),
	ENVIROMENT(R.string.group_environment_sensor),
	OTHER(R.string.group_other_sensor);

	private int groupId;

	SensorGroup(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}
}
