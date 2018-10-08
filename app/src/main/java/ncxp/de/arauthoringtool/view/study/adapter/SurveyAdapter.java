package ncxp.de.arauthoringtool.view.study.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ncxp.de.arauthoringtool.R;
import ncxp.de.arauthoringtool.model.data.Survey;
import ncxp.de.arauthoringtool.view.study.OptionSurveyListener;
import ncxp.de.arauthoringtool.view.study.viewholder.SurveyViewHolder;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyViewHolder> {

	private List<Survey>         surveys;
	private OptionSurveyListener optionListener;

	public SurveyAdapter(List<Survey> surveys, OptionSurveyListener optionListener) {
		this.surveys = surveys;
		this.optionListener = optionListener;
	}

	@NonNull
	@Override
	public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_survey, parent, false);
		return new SurveyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
		Survey survey = surveys.get(position);
		holder.getSurveyName().setText(survey.getName());
		holder.getSurveyDescription().setText(survey.getDescription());
		holder.getMoreButton().setOnClickListener(view -> optionListener.onPopupMenuClick(view, survey));
		holder.getTestButton().setOnClickListener(view -> optionListener.onTestButtonClick(view, survey));
	}

	@Override
	public int getItemCount() {
		return surveys != null ? surveys.size() : 0;
	}

	public void addItems(List<Survey> newSurveys) {
		surveys.addAll(newSurveys);
		notifyDataSetChanged();
	}

	public void addItem(Survey survey) {
		surveys.add(survey);
		notifyItemInserted(surveys.size());
	}

	public void deleteItem(Survey survey) {
		int position = surveys.indexOf(survey);
		surveys.remove(position);
		notifyItemRemoved(position);
	}

	public void updateItem(Survey survey) {
		int position = surveys.indexOf(survey);
		notifyItemChanged(position);
	}

	public void replaceItems(List<Survey> surveys) {
		this.surveys = surveys;
		notifyDataSetChanged();
	}
}
