package ncxp.de.arauthoringtool.view.studies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.model.data.DeviceSensor;
import ncxp.de.arauthoringtool.model.data.Study;

public class StudiesAdapter extends RecyclerView.Adapter<StudyViewHolder> {

	private List<Study>   studies;
	private StudyListener studyListener;

	public StudiesAdapter(List<Study> studies, StudyListener studyListener) {
		this.studies = studies;
		this.studyListener = studyListener;

	}

	@NonNull
	@Override
	public StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_study, parent, false);
		return new StudyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull StudyViewHolder holder, int position) {
		Study study = studies.get(position);
		holder.getStudyNameView().setText(study.getName());
		holder.getAmountOfTestPersonView().setText(R.string.amount_of_subjects);
		CharSequence subjectText = holder.getAmountOfTestPersonView().getText();
		String amountOfSubjects = study.getAmountOfSubjects() + " " + subjectText;
		holder.getAmountOfTestPersonView().setText(amountOfSubjects);
		holder.getMoreButton().setOnClickListener(view -> studyListener.onPopupMenuClick(view, study));
		holder.getDescriptionView().setText(study.getDescription());
		holder.getStartButton().setOnClickListener((view) -> studyListener.onStudyStartClick(study));
		LinearLayout expandableView = holder.getExpandableView();
		holder.getExpandArrowButton().setOnClickListener(view -> {
			int visibility = expandableView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
			if (visibility == View.GONE) {
				holder.getExpandArrowButton().setImageResource(R.drawable.chevron_up);
			} else {
				holder.getExpandArrowButton().setImageResource(R.drawable.chevron_down);
			}
			expandableView.setVisibility(visibility);
		});
		List<DeviceSensor> sensors = study.getSensors();
		StringBuilder text = new StringBuilder();
		List<String> names = new ArrayList<>();
		if (sensors != null) {
			names = sensors.stream().map(DeviceSensor::getName).collect(Collectors.toList());
		}
		for (String name : names) {
			text.append("\u2022 ").append(name).append("\n");
		}
		holder.getSensorsView().setText(text.toString());


	}

	@Override
	public int getItemCount() {
		return studies == null ? 0 : studies.size();
	}

	public void deleteItem(Study study) {
		int position = studies.indexOf(study);
		studies.remove(position);
		notifyItemRemoved(position);
	}

	public void addItems(List<Study> newStudies) {
		studies.addAll(newStudies);
		notifyDataSetChanged();
	}

	public void replaceItems(List<Study> newStudies) {
		studies = newStudies;
		notifyDataSetChanged();
	}


}
