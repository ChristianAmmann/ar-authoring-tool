package ncxp.de.mobiledatacollection.datalogger.data.request;


import java.util.List;

import ncxp.de.mobiledatacollection.datalogger.data.DataBatch;

public class DataRequestResponse {

	private List<DataBatch> dataBatches;
	private long            startTimestamp;
	private long            endTimestamp;

	public DataRequestResponse() {
	}

	public DataRequestResponse(List<DataBatch> dataBatches) {
		this.dataBatches = dataBatches;
		endTimestamp = System.currentTimeMillis();
	}

	public List<DataBatch> getDataBatches() {
		return dataBatches;
	}

	public void setDataBatches(List<DataBatch> dataBatches) {
		this.dataBatches = dataBatches;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
}
