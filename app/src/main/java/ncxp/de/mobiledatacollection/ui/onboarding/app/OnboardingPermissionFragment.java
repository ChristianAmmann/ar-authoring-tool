package ncxp.de.mobiledatacollection.ui.onboarding.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ncxp.de.mobiledatacollection.R;

public class OnboardingPermissionFragment extends Fragment {

	private static final int       PERMISSION_KEY = 346;
	private              ImageView cameraCheck;
	private              ImageView memoryCheck;
	private              ImageView microfonCheck;

	public static OnboardingPermissionFragment newInstance() {
		return new OnboardingPermissionFragment();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_onboarding_app_permissions, container, false);
		cameraCheck = view.findViewById(R.id.camera_permission_check);
		memoryCheck = view.findViewById(R.id.extern_memory_check);
		microfonCheck = view.findViewById(R.id.microfon_permission_check);
		setupView();
		return view;
	}

	private void setupView() {
		boolean requestPermissions = false;
		if (checkCameraPermission()) {
			cameraCheck.setImageDrawable(getActivity().getDrawable(R.drawable.check_circle));
			cameraCheck.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
		} else {
			requestPermissions = true;
		}
		if (checkMemoryPermission()) {
			memoryCheck.setImageDrawable(getActivity().getDrawable(R.drawable.check_circle));
			memoryCheck.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
		} else {
			requestPermissions = true;
		}
		if (checkMicrofonPermission()) {
			microfonCheck.setImageDrawable(getActivity().getDrawable(R.drawable.check_circle));
			microfonCheck.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_green_light));
		} else {
			requestPermissions = true;
		}
		if (requestPermissions) {
			requestPermissions();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setupView();
	}

	private boolean checkCameraPermission() {
		return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

	private boolean checkMemoryPermission() {
		return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
	}

	private boolean checkMicrofonPermission() {
		return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
	}

	private void requestPermissions() {
		ActivityCompat.requestPermissions(getActivity(),
										  new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
										  PERMISSION_KEY);

	}
}
