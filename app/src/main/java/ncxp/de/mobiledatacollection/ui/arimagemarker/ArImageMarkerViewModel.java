package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.google.ar.sceneform.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ncxp.de.mobiledatacollection.model.data.ARScene;
import ncxp.de.mobiledatacollection.model.data.ArImageToObjectRelation;
import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.sceneform.ObjectARImageNode;


public class ArImageMarkerViewModel extends AndroidViewModel {
	private static final String FILE_TYPE = ".png";

	private MutableLiveData<List<Thumbnail>> modelsThumbnails;
	private ArSceneRepository                arSceneRepository;
	private ARScene                          arScene;

	public ArImageMarkerViewModel(@NonNull Application application, ArSceneRepository arSceneRepository) {
		super(application);
		this.arSceneRepository = arSceneRepository;
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

	public void save(String title, String description, Map<String, Node> augmentedImageMap) {
		List<ArImageToObjectRelation> relations = convertTo(augmentedImageMap);
		ARScene scene = new ARScene();
		scene.setName(title);
		scene.setDescription(description);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			long arSceneId = arSceneRepository.saveArScene(scene);
			arSceneRepository.saveArImageToObjects(arSceneId, relations);
		});
	}

	public void update(ARScene scene, Map<String, Node> augmentedImageMap) {
		List<ArImageToObjectRelation> relations = convertTo(augmentedImageMap);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			long arSceneId = arSceneRepository.saveArScene(scene);
			arSceneRepository.saveArImageToObjects(arSceneId, relations);
		});
	}

	public List<ArImageToObjectRelation> convertTo(Map<String, Node> augmentedImageMap) {
		Map<String, ObjectARImageNode> map = augmentedImageMap.entrySet()
															  .stream()
															  .filter(x -> x.getValue() instanceof ObjectARImageNode)
															  .collect(Collectors.toMap(Map.Entry::getKey, x -> (ObjectARImageNode) x.getValue()));
		List<ArImageToObjectRelation> relations = new ArrayList<>();
		map.entrySet().forEach(x -> relations.add(new ArImageToObjectRelation(x.getKey(), x.getValue().getFileName())));
		return relations;
	}

	public void setArScene(ARScene arScene) {
		this.arScene = arScene;
	}

	public ARScene getArScene() {
		return this.arScene;
	}

	public boolean containsArSceneObject(String augmentedImageName) {
		if (arScene != null) {
			return arScene.getArImageObjects().stream().anyMatch(relation -> relation.getArMarkerId().equals(augmentedImageName));
		}
		return false;
	}

	public ArImageToObjectRelation getArSceneObjectFileName(String augmentedImageName) {
		Optional<ArImageToObjectRelation> first = arScene.getArImageObjects().stream().filter(relation -> relation.getArMarkerId().equals(augmentedImageName)).findFirst();
		return first.orElse(null);
	}
}
