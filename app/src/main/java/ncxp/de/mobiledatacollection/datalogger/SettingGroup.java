package ncxp.de.mobiledatacollection.datalogger;

import ncxp.de.mobiledatacollection.R;

public enum SettingGroup {

	SENSOR_CONFIGURATION(R.string.group_others_sensor_settings),
	VIDEO_AUDIO(R.string.group_others_video_audio),
	OTHERS(R.string.group_others_miscellaneous);

	private int groupId;

	SettingGroup(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}
}
