package ncxp.de.arauthoringtool.viewmodel;

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

import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.ArObject;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.view.areditor.Thumbnail;
import ncxp.de.arauthoringtool.view.areditor.util.EditorState;

public class MappingViewModel extends AndroidViewModel {

	private Study                            study;
	private ARScene                          arScene;
	private MutableLiveData<List<Thumbnail>> thumbnails;
	private EditorState                      editorState;

	public MappingViewModel(@NonNull Application application) {
		super(application);
		this.thumbnails = new MutableLiveData<>();
		thumbnails.postValue(new ArrayList<>());
	}

	public LiveData<List<Thumbnail>> getThumbnails() {
		return this.thumbnails;
	}

	public void init() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(this::loadThumbnails);
	}

	private void loadThumbnails() {
		List<Thumbnail> loadingThumbnails = new ArrayList<>();
		for (ArObject arObject : arScene.getArImageObjects()) {
			String file = arObject.getImageName().replace("sfb", "png");
			Drawable drawable = getDrawable(file);
			if (drawable != null) {
				loadingThumbnails.add(new Thumbnail(drawable, file));
			}
		}
		thumbnails.postValue(loadingThumbnails);
	}

	private Drawable getDrawable(String file) {
		InputStream inputStream;
		Drawable drawable = null;
		try {
			inputStream = this.getApplication().getAssets().open(file);
			drawable = Drawable.createFromStream(inputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}


	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public ARScene getArScene() {
		return arScene;
	}

	public void setArScene(ARScene arScene) {
		this.arScene = arScene;
	}

	public EditorState getState() {
		return editorState;
	}

	public void setState(EditorState editorState) {
		this.editorState = editorState;
	}
}
