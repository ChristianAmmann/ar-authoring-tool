package ncxp.de.arauthoringtool.viewmodel;

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

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.ArObject;
import ncxp.de.arauthoringtool.model.data.Study;
import ncxp.de.arauthoringtool.model.data.TestPerson;
import ncxp.de.arauthoringtool.model.data.TestPersonState;
import ncxp.de.arauthoringtool.model.repository.ArSceneRepository;
import ncxp.de.arauthoringtool.model.repository.TestPersonRepository;
import ncxp.de.arauthoringtool.sceneform.ArNode;
import ncxp.de.arauthoringtool.sensorlogger.SensorBackgroundService;
import ncxp.de.arauthoringtool.ui.areditor.Thumbnail;
import ncxp.de.arauthoringtool.ui.areditor.util.EditorState;
import ncxp.de.arauthoringtool.ui.areditor.util.RotationTechnique;
import ncxp.de.arauthoringtool.ui.areditor.util.ScaleTechnique;
import ncxp.de.arauthoringtool.ui.areditor.util.SelectionTechnique;


public class ArEditorViewModel extends AndroidViewModel {
	private static final String FILE_TYPE = ".png";

	private MutableLiveData<List<Thumbnail>>    modelsThumbnails;
	private ArSceneRepository                   arSceneRepository;
	private TestPersonRepository                testPersonRepository;
	private ARScene                             arScene;
	private Study                               study;
	private SensorBackgroundService             sensorBackgroundService;
	private boolean                             bound                = false;
	private List<ArNode>                        arNodes;
	private MutableLiveData<Node>               currentSelectedNode;
	private EditorState                         editorState;
	private TestPersonState                     testPersonState;
	private MutableLiveData<SelectionTechnique> selectionTechnique;
	private MutableLiveData<RotationTechnique>  rotationTechnique;
	private MutableLiveData<ScaleTechnique>     scaleTechnique;
	private boolean                             comingFromStudyModus = false;
	private String                              currentImageSelection;


	public ArEditorViewModel(@NonNull Application application, ArSceneRepository arSceneRepository, TestPersonRepository testPersonRepository) {
		super(application);
		this.arSceneRepository = arSceneRepository;
		this.testPersonRepository = testPersonRepository;
		this.modelsThumbnails = new MutableLiveData<>();
		arNodes = new ArrayList<>();
		modelsThumbnails.postValue(new ArrayList<>());
		currentSelectedNode = new MutableLiveData<>();
		Intent intent = new Intent(getApplication(), SensorBackgroundService.class);
		getApplication().bindService(intent, connection, Context.BIND_AUTO_CREATE);
		selectionTechnique = new MutableLiveData<>();
		selectionTechnique.postValue(SelectionTechnique.RAYCASTING);
		rotationTechnique = new MutableLiveData<>();
		rotationTechnique.postValue(RotationTechnique.TWO_FINGER);
		scaleTechnique = new MutableLiveData<>();
		scaleTechnique.postValue(ScaleTechnique.PINCH);
		this.testPersonState = TestPersonState.STOPPED;
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

	public void save(String title, String description) {
		List<ArObject> arObjects = convertTo(arNodes);
		ARScene scene = new ARScene();
		scene.setName(title);
		scene.setDescription(description);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			long arSceneId = arSceneRepository.saveArScene(scene);
			arSceneRepository.saveArObjects(arSceneId, arObjects);
			scene.setId(arSceneId);
			setArScene(scene);
		});
	}

	public void update(ARScene scene) {
		List<ArObject> arObjects = convertTo(arNodes);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			long arSceneId = arSceneRepository.saveArScene(scene);
			arSceneRepository.saveArObjects(arSceneId, arObjects);
		});
	}

	public List<ArObject> convertTo(List<ArNode> arNodes) {
		List<ArObject> arObjects = new ArrayList<>();
		arNodes.forEach(node -> {
			Vector3 scale = node.getLocalScale();
			Quaternion rotation = node.getLocalRotation();
			arObjects.add(new ArObject(node.getFileName(), scale, rotation));
		});
		return arObjects;
	}

	public void setArScene(ARScene arScene) {
		this.arScene = arScene;
	}

	public ARScene getArScene() {
		return this.arScene;
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

	public void createTestpersonAndStartService() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			TestPerson person = new TestPerson();
			person.setStudyId(study.getId());
			long id = testPersonRepository.saveTestPerson(person);
			person.setId(id);
			study.setCurrentSubject(person);
			startSensorService();
		});
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

	public List<ArNode> getArNodes() {
		return arNodes;
	}

	public void setArNodes(List<ArNode> arNodes) {
		this.arNodes = arNodes;
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

	public EditorState getEditorState() {
		return editorState;
	}

	public void setEditorState(EditorState editorState) {
		this.editorState = editorState;
	}

	public void setSelectionTechnique(SelectionTechnique selectionTechnique) {
		this.selectionTechnique.postValue(selectionTechnique);
	}

	public void setRotationTechnique(RotationTechnique rotationTechnique) {
		this.rotationTechnique.postValue(rotationTechnique);
	}

	public void setScaleTechnique(ScaleTechnique scaleTechnique) {
		this.scaleTechnique.postValue(scaleTechnique);
	}

	public MutableLiveData<SelectionTechnique> getSelectionTechnique() {
		return selectionTechnique;
	}

	public MutableLiveData<RotationTechnique> getRotationTechnique() {
		return rotationTechnique;
	}

	public MutableLiveData<ScaleTechnique> getScaleTechnique() {
		return scaleTechnique;
	}

	public void resetInteractionTechnique() {
		this.selectionTechnique.postValue(SelectionTechnique.RAYCASTING);
		this.scaleTechnique.postValue(ScaleTechnique.PINCH);
		this.rotationTechnique.postValue(RotationTechnique.TWO_FINGER);
	}

	public boolean isComingFromStudyModus() {
		return comingFromStudyModus;
	}

	public void setComingFromStudyModus(boolean comingFromStudyModus) {
		this.comingFromStudyModus = comingFromStudyModus;
	}

	public void saveTestperson(TestPerson person) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			person.setArSceneName(arScene.getName());
			person.setTechnqiues(selectionTechnique.getValue().getName() + ", " + rotationTechnique.getValue().getName() + ", " + scaleTechnique.getValue().getName());
			testPersonRepository.saveTestPerson(person);
		});
	}

	public TestPersonState getTestPersonState() {
		return testPersonState;
	}

	public void setTestPersonState(TestPersonState testPersonState) {
		this.testPersonState = testPersonState;
	}

	public String getCurrentImageSelection() {
		return currentImageSelection;
	}

	public void setCurrentImageSelection(String currentImageSelection) {
		this.currentImageSelection = currentImageSelection;
	}
}
