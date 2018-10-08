package ncxp.de.arauthoringtool.repository;

import java.util.List;

import ncxp.de.arauthoringtool.model.dao.ArObjectDao;
import ncxp.de.arauthoringtool.model.dao.ArSceneDao;
import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.ArObject;

public class ArSceneRepository {

	private final ArSceneDao  arSceneDao;
	private final ArObjectDao arObjectDao;

	public ArSceneRepository(ArSceneDao arSceneDao, ArObjectDao arObjectDao) {
		this.arSceneDao = arSceneDao;
		this.arObjectDao = arObjectDao;
	}

	public List<ARScene> getArScenes() {
		List<ARScene> arScenes = arSceneDao.selectAll();
		arScenes.forEach(scene -> scene.setArImageObjects(arObjectDao.selectAll(scene.getId())));
		return arScenes;
	}


	public long saveArScene(ARScene arScene) {
		return arSceneDao.insert(arScene);
	}

	public void deleteArScene(ARScene arScene) {
		arSceneDao.deleteById(arScene.getId());
		arObjectDao.deleteByArScene(arScene.getId());
	}

	public long[] saveArObjects(long arSceneId, List<ArObject> arObjects) {
		arObjects.stream().forEach(relation -> relation.setArSceneId(arSceneId));
		ArObject[] arObjectArray = new ArObject[arObjects.size()];
		arObjectArray = arObjects.toArray(arObjectArray);
		return arObjectDao.insertAll(arObjectArray);
	}
}
