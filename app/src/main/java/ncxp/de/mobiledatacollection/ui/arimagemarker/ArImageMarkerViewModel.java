package ncxp.de.mobiledatacollection.ui.arimagemarker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ncxp.de.mobiledatacollection.datalogger.SensorBackgroundService;
import ncxp.de.mobiledatacollection.model.data.ARScene;
import ncxp.de.mobiledatacollection.model.data.ArImageToObjectRelation;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.repository.ArSceneRepository;
import ncxp.de.mobiledatacollection.sceneform.ObjectARImageNode;


public class ArImageMarkerViewModel extends AndroidViewModel {
	private static final String FILE_TYPE = ".png";

	private MutableLiveData<List<Thumbnail>> modelsThumbnails;
	private ArSceneRepository                arSceneRepository;
	private ARScene                          arScene;
	private Study                            study;
	private SensorBackgroundService          sensorBackgroundService;
	private boolean                          bound = false;
	private Map<String, Node>                augmentedImageMap;
	private MutableLiveData<Node>            currentSelectedNode;
	private EditorState                      state;


	public ArImageMarkerViewModel(@NonNull Application application, ArSceneRepository arSceneRepository) {
		super(application);
		this.arSceneRepository = arSceneRepository;
		this.modelsThumbnails = new MutableLiveData<>();
		augmentedImageMap = new HashMap<>();
		modelsThumbnails.postValue(new ArrayList<>());
		currentSelectedNode = new MutableLiveData<>();
		Intent intent = new Intent(getApplication(), SensorBackgroundService.class);
		getApplication().bindService(intent, connection, Context.BIND_AUTO_CREATE);
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

	public void save(String title, String description) {
		List<ArImageToObjectRelation> relations = convertTo(augmentedImageMap);
		ARScene scene = new ARScene();
		scene.setName(title);
		scene.setDescription(description);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			long arSceneId = arSceneRepository.saveArScene(scene);
			arSceneRepository.saveArImageToObjects(arSceneId, relations);
			scene.setId(arSceneId);
			setArScene(scene);
		});
	}

	public void update(ARScene scene) {
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

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			SensorBackgroundService.SensorBackgroundBinder binder = (SensorBackgroundService.SensorBackgroundBinder) service;
			sensorBackgroundService = binder.getService();
			bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bound = false;
		}
	};

	@Override
	protected void onCleared() {
		super.onCleared();
		getApplication().unbindService(connection);
		bound = false;
	}


	public void startSensorService() {
		if (study != null) {
			sensorBackgroundService.initialize(study);

		}
		sensorBackgroundService.start();
	}

	public void stopSensorService() {
		sensorBackgroundService.stop();
	}

	public void abortSensorService() {
		sensorBackgroundService.abort();
	}

	public Study getStudy() {
		return study;
	}

	public Map<String, Node> getAugmentedImageMap() {
		return augmentedImageMap;
	}

	public void setAugmentedImageMap(Map<String, Node> augmentedImageMap) {
		this.augmentedImageMap = augmentedImageMap;
	}

	public boolean containsAugmentedImage(AugmentedImage augmentedImage) {
		return getAugmentedImageMap().containsKey(augmentedImage.getName());
	}

	public void addARObject(String name, Node node) {
		augmentedImageMap.put(name, node);
	}

	public boolean containsARObject() {
		return augmentedImageMap.values().stream().anyMatch(node -> node instanceof ObjectARImageNode);
	}

	public MutableLiveData<Node> getCurrentSelectedNode() {
		return currentSelectedNode;
	}

	public void setCurrentSelectedNode(Node currentSelectedNode) {
		this.currentSelectedNode.postValue(currentSelectedNode);
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public EditorState getState() {
		return state;
	}

	public void setState(EditorState state) {
		this.state = state;
	}
}
