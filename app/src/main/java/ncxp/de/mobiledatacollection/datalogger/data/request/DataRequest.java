package ncxp.de.mobiledatacollection.datalogger.data.request;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class DataRequest implements Serializable {

	public static final long UPDATE_INTERVAL_DEFAULT = TimeUnit.SECONDS.toMillis(1);
	public static final long UPDATE_INTERVAL_FAST    = 50;
	public static final long UPDATE_INTERVAL_NORMAL  = 100;
	public static final long UPDATE_INTERVAL_SLOW    = 500;
	public static final int  TIMESTAMP_NOT_SET       = -1;

	private String sourceNodeId;
	private String dataSource;
	private long   updateInteval;
	private long   startTimestamp;
	private long   endTimestamp;

	public DataRequest() {
		updateInteval = UPDATE_INTERVAL_DEFAULT;
		startTimestamp = System.currentTimeMillis();
		endTimestamp = TIMESTAMP_NOT_SET;
	}

	public DataRequest(String sourceNodeId) {
		this();
		this.sourceNodeId = sourceNodeId;
	}

	/**
	 * Getter & Setter
	 */
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public long getUpdateInteval() {
		return updateInteval;
	}

	public void setUpdateInteval(long updateInteval) {
		this.updateInteval = updateInteval;
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

	public String getSourceNodeId() {
		return sourceNodeId;
	}

	public void setSourceNodeId(String sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}
}
