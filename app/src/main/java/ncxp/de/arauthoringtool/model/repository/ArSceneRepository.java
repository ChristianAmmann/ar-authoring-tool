package ncxp.de.arauthoringtool.model.repository;

import java.util.List;

import ncxp.de.arauthoringtool.model.dao.ArImageToObjectRelationDao;
import ncxp.de.arauthoringtool.model.dao.ArSceneDao;
import ncxp.de.arauthoringtool.model.data.ARScene;
import ncxp.de.arauthoringtool.model.data.ArImageToObjectRelation;

public class ArSceneRepository {

	private final ArSceneDao                 arSceneDao;
	private final ArImageToObjectRelationDao arImageToObjectRelationDao;

	public ArSceneRepository(ArSceneDao arSceneDao, ArImageToObjectRelationDao arImageToObjectRelationDao) {
		this.arSceneDao = arSceneDao;
		this.arImageToObjectRelationDao = arImageToObjectRelationDao;
	}

	public List<ARScene> getArScenes() {
		List<ARScene> arScenes = arSceneDao.selectAll();
		arScenes.forEach(scene -> scene.setArImageObjects(arImageToObjectRelationDao.selectAll(scene.getId())));
		return arScenes;
	}


	public long saveArScene(ARScene arScene) {
		return arSceneDao.insert(arScene);
	}

	public void deleteArScene(ARScene arScene) {
		arSceneDao.deleteById(arScene.getId());
		arImageToObjectRelationDao.deleteByArScene(arScene.getId());
	}

	public void saveArImageToObjects(long arSceneId, List<ArImageToObjectRelation> relations) {
		relations.stream().forEach(relation -> relation.setArSceneId(arSceneId));
		ArImageToObjectRelation[] arImageToObjectRelations = new ArImageToObjectRelation[relations.size()];
		arImageToObjectRelations = relations.toArray(arImageToObjectRelations);
		arImageToObjectRelationDao.insertAll(arImageToObjectRelations);
	}
}
