package ncxp.de.mobiledatacollection.ui.study.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ncxp.de.mobiledatacollection.R;

public class SectionViewHolder extends RecyclerView.ViewHolder {

	private TextView sectionTitle;

	public SectionViewHolder(View itemView) {
		super(itemView);
		sectionTitle = itemView.findViewById(R.id.section_title);

	}

	public TextView getSectionTitle() {
		return sectionTitle;
	}
}
