package ncxp.de.mobiledatacollection.ui.studies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ncxp.de.mobiledatacollection.model.data.Study;

@Getter
@Setter
public class StudiesViewModel extends ViewModel {

	private LiveData<List<Study>> studies;
}
