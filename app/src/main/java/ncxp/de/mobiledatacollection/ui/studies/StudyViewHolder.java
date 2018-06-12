package ncxp.de.mobiledatacollection.ui.studies;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ncxp.de.mobiledatacollection.R;

public class StudyViewHolder extends RecyclerView.ViewHolder {

	private TextView studyNameView;
	private TextView amountOfTestPersonView;


	public StudyViewHolder(@NonNull View itemView) {
		super(itemView);
		studyNameView = itemView.findViewById(R.id.study_name);
		amountOfTestPersonView = itemView.findViewById(R.id.amount_of_testperson);
	}

	public TextView getStudyNameView() {
		return studyNameView;
	}

	public TextView getAmountOfTestPersonView() {
		return amountOfTestPersonView;
	}
}
