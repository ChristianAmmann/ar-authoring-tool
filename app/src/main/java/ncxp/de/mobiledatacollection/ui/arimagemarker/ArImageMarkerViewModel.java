package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ArImageMarkerViewModel extends AndroidViewModel {
	private static final String FILE_TYPE = ".png";

	MutableLiveData<List<Thumbnail>> modelsThumbnails;

	public ArImageMarkerViewModel(@NonNull Application application) {
		super(application);
		this.modelsThumbnails = new MutableLiveData<>();
		modelsThumbnails.postValue(new ArrayList<>());
	}

	public LiveData<List<Thumbnail>> getThumbnails() {
		return this.modelsThumbnails;
	}

	public void init() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(this::loadThumbnails);
	}

	private void loadThumbnails() {
		List<Thumbnail> thumbnails = new ArrayList<>();
		try {
			String[] models = getApplication().getAssets().list("");
			for (String file : models) {
				if (!file.toLowerCase().endsWith(FILE_TYPE)) {
					continue;
				}
				Drawable drawable = getDrawable(file);
				if (drawable != null) {
					thumbnails.add(new Thumbnail(drawable, file));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		modelsThumbnails.postValue(thumbnails);
	}

	private Drawable getDrawable(String file) {
		InputStream inputStream = null;
		Drawable drawable = null;
		try {
			inputStream = this.getApplication().getAssets().open(file);
			drawable = Drawable.createFromStream(inputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}

}
