package ncxp.de.mobiledatacollection.viewmodel.util;

import android.arch.lifecycle.LifecycleOwner;

public class ToastMessage extends SingleLiveEvent<String> {
	public void observe(LifecycleOwner owner, final ToastObserver observer) {
		super.observe(owner, t -> {
			if (t == null) {
				return;
			}
			observer.onNewMessage(t);
		});
	}

	public interface ToastObserver {

		void onNewMessage(String toastMessage);
	}
}
