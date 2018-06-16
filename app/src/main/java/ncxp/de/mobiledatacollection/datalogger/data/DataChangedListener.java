package ncxp.de.mobiledatacollection.datalogger.data;

public interface DataChangedListener {

    public void onDataChanged(DataBatch dataBatch, String sourceNodeId);

}
