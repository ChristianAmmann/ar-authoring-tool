package ncxp.de.arauthoringtool.ui.onboarding.areditor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ncxp.de.arauthoringtool.R;

public class OnboardingArEditorPlacingStepThreeFragment extends Fragment {

	public static OnboardingArEditorPlacingStepThreeFragment newInstance() {
		return new OnboardingArEditorPlacingStepThreeFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_onboarding_areditor_placing_step_three, container, false);
	}
}
