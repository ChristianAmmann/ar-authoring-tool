package ncxp.de.mobiledatacollection.ui.studies.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ncxp.de.mobiledatacollection.R;
import ncxp.de.mobiledatacollection.model.data.Data;
import ncxp.de.mobiledatacollection.model.data.DeviceSensor;
import ncxp.de.mobiledatacollection.model.data.Study;
import ncxp.de.mobiledatacollection.model.data.Survey;
import ncxp.de.mobiledatacollection.model.data.TestPerson;
import ncxp.de.mobiledatacollection.model.repository.DataRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyDeviceSensorJoinRepository;
import ncxp.de.mobiledatacollection.model.repository.StudyRepository;
import ncxp.de.mobiledatacollection.model.repository.SurveyRepository;
import ncxp.de.mobiledatacollection.model.repository.TestPersonRepository;
import ncxp.de.mobiledatacollection.util.CSVWriter;

public class StudiesViewModel extends AndroidViewModel {

	public static final String DIRECTORY = "MobileDataCollection";

	private MutableLiveData<List<Study>>    studies;
	private StudyRepository                 studyRepo;
	private StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository;
	private TestPersonRepository            testPersonRepo;
	private DataRepository                  dataRepo;
	private SurveyRepository                surveyRepo;
	private ToastMessage                    toastMessage = new ToastMessage();

	public StudiesViewModel(Application context,
							StudyRepository studyRepo,
							StudyDeviceSensorJoinRepository studyDeviceSensorJoinRepository,
							SurveyRepository surveyRepo,
							TestPersonRepository testPersonRepo,
							DataRepository dataRepo) {
		super(context);
		this.studyRepo = studyRepo;
		this.studyDeviceSensorJoinRepository = studyDeviceSensorJoinRepository;
		this.surveyRepo = surveyRepo;
		this.testPersonRepo = testPersonRepo;
		this.dataRepo = dataRepo;
		studies = new MutableLiveData<>();
	}

	public LiveData<List<Study>> getStudies() {
		return this.studies;
	}

	public ToastMessage getToastMessage() {
		return toastMessage;
	}


	public void init() {
		loadStudies();
	}

	private void loadStudies() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<Study> fetchedData = studyRepo.getStudies();
			fetchedData.forEach(study -> {
				List<DeviceSensor> deviceSensorsForStudy = studyDeviceSensorJoinRepository.getDeviceSensorsForStudy(study);
				study.setSensors(deviceSensorsForStudy);
				List<Survey> surveys = surveyRepo.getSurveysFromStudy(study);
				study.setSurveys(surveys);
			});
			studies.postValue(fetchedData);
		});
	}

	public void deleteStudy(Study study) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			studyRepo.deleteStudy(study);
			List<Study> currentStudies = studies.getValue();
			currentStudies.remove(study);
			studies.postValue(currentStudies);
		});
	}

	public void refreshStudies() {
		loadStudies();
	}

	public void exportStudyAsCSV(Study study) {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(() -> {
			List<TestPerson> testPersonForStudy = testPersonRepo.getTestPersonForStudy(study);
			testPersonForStudy.forEach(person -> {
				List<Data> dataFromTestPerson = dataRepo.getDataFromTestPerson(person);
				person.setDataList(dataFromTestPerson);
			});
			List<Survey> surveysFromStudy = surveyRepo.getSurveysFromStudy(study);
			String fileName = writeCsv(study, surveysFromStudy, testPersonForStudy);
			toastMessage.postValue(getApplication().getString(R.string.export_toast, "/" + DIRECTORY + "/" + fileName));
		});
	}

	private String writeCsv(Study study, List<Survey> surveys, List<TestPerson> testPeople) {
		File exportDir = new File(Environment.getExternalStorageDirectory(), DIRECTORY);
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		File studyDir = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY, study.getName());
		if (!studyDir.exists()) {
			studyDir.mkdirs();
		}
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd_");
		Date date = new Date();
		String time = simpleDate.format(date);
		String fileName = time + study.getName() + ".csv";
		File file = new File(studyDir, fileName);

		try {
			boolean created = file.createNewFile();
			CSVWriter writer = new CSVWriter(new FileWriter(file));
			writeHeader(writer);
			writeBody(writer, study, surveys, testPeople);
			writer.close();
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void writeHeader(CSVWriter writer) {
		String[] header = Stream.of(Study.getCSVHeader(), Survey.getCSVHeader(), TestPerson.getCSVHeader(), Data.getCSVHeader()).flatMap(Stream::of).toArray(String[]::new);
		writer.writeNext(header);
	}

	private void writeBody(CSVWriter writer, Study study, List<Survey> surveys, List<TestPerson> testPeople) {
		int amountOfLines = getAmountOfCsvLines(surveys, testPeople);
		String[] body = new String[0];
		String[] studyCsvValues = study.getCsvValues();
		List<Data> datas = testPeople.stream().map(TestPerson::getDataList).flatMap(t -> t.stream()).collect(Collectors.toList());
		for (int i = 0; i < amountOfLines; i++) {

			String[] surveyCsvValues = {"", "", "", "", ""};
			if (surveys.size() > i && surveys.get(i) != null) {
				surveyCsvValues = surveys.get(i).getCsvValues();
			}
			String[] testPersonCsvValues = {"", ""};
			if (testPeople.size() > i && testPeople.get(i) != null) {
				TestPerson testPerson = testPeople.get(i);
				testPersonCsvValues = testPerson.geCsvValues();
			}
			String[] dataCsvValues = {"", "", "", "", ""};
			if (datas.size() > i && datas.get(i) != null) {
				dataCsvValues = datas.get(i).getCsvBody();
			}

			body = Stream.of(body, studyCsvValues, surveyCsvValues, testPersonCsvValues, dataCsvValues).flatMap(Stream::of).toArray(String[]::new);
			writer.writeNext(body);
			body = new String[0];
			studyCsvValues = new String[]{"", "", "", "", "", "", ""};
		}
	}


	private int getAmountOfCsvLines(List<Survey> surveys, List<TestPerson> testPeople) {
		int amountOfLines = Math.max(surveys.size(), testPeople.size());
		OptionalInt max = testPeople.stream().map(testPerson -> testPerson.getDataList().size()).mapToInt(Integer::intValue).max();
		if (max.isPresent()) {
			amountOfLines = Math.max(max.getAsInt(), amountOfLines);
		}
		return amountOfLines;
	}
}
