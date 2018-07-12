package ncxp.de.mobiledatacollection.ui.onboarding;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ncxp.de.mobiledatacollection.R;

public class OnboardingJavascriptSurveyFragment extends Fragment {

	public static OnboardingJavascriptSurveyFragment newInstance() {
		return new OnboardingJavascriptSurveyFragment();
	}

	private Button copyButton;
	private Button openSiteButton;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_onboarding_javascript, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Button copyButton = view.findViewById(R.id.copy_button);
		Button openSiteButton = view.findViewById(R.id.open_button);
		copyButton.setOnClickListener(viewClicked -> {
			ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("Survey Javavscript", getString(R.string.survey_javascript));
			clipboard.setPrimaryClip(clip);
		});
		openSiteButton.setOnClickListener(clickedView -> {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.soscisurvey_base_url)));
			getActivity().startActivity(browserIntent);
		});
	}
}
