package ncxp.de.mobiledatacollection.ui.onboarding.survey;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ncxp.de.mobiledatacollection.R;

public class OnboardingImportSurveyFragment extends Fragment {

	public static OnboardingImportSurveyFragment newInstance() {
		return new OnboardingImportSurveyFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_onboarding_import_survey, container, false);
	}
}
