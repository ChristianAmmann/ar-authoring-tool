package ncxp.de.mobiledatacollection.model.repository;

import java.util.List;

import ncxp.de.mobiledatacollection.model.dao.ArSceneDao;
import ncxp.de.mobiledatacollection.model.data.ARScene;

public class ArSceneRepository {

	private final ArSceneDao arSceneDao;

	public ArSceneRepository(ArSceneDao arSceneDao) {
		this.arSceneDao = arSceneDao;
	}

	public List<ARScene> getArScenes() {
		return arSceneDao.selectAll();
	}


	public long saveArScene(ARScene arScene) {
		return arSceneDao.insert(arScene);
	}

	public void deleteArScene(ARScene arScene) {
		arSceneDao.deleteById(arScene.getId());
	}
}
