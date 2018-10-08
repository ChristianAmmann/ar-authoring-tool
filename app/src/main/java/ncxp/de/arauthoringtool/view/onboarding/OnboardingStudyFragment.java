package ncxp.de.arauthoringtool.view.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ncxp.de.arauthoringtool.R;

public class OnboardingStudyFragment extends Fragment {

	public static OnboardingStudyFragment newInstance() {
		return new OnboardingStudyFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_onboarding_app_study, container, false);
	}
}