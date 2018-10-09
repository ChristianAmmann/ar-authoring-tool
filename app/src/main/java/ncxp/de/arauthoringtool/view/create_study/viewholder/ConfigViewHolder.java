package ncxp.de.arauthoringtool.view.create_study.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import ncxp.de.arauthoringtool.R;

public class ConfigViewHolder extends RecyclerView.ViewHolder {

	private TextView configName;
	private TextView configDescription;
	private Switch   switchButton;

	public ConfigViewHolder(View itemView) {
		super(itemView);
		configName = itemView.findViewById(R.id.config_name);
		configDescription = itemView.findViewById(R.id.config_description);
		switchButton = itemView.findViewById(R.id.switch_button);
	}

	public TextView getConfigName() {
		return configName;
	}

	public TextView getConfigDescription() {
		return configDescription;
	}

	public Switch getSwitchButton() {
		return switchButton;
	}
}
