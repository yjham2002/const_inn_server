package server.response;

public class ProcessResult {

    private boolean isSuccess = true;
    private boolean wasList = false;
    private int total = 0;
    private int processed = 0;

    public ProcessResult(boolean isSuccess, boolean wasList, int total, int processed) {
        this.isSuccess = isSuccess;
        this.wasList = wasList;
        this.total = total;
        this.processed = processed;
    }

    public ProcessResult(){}

    public ProcessResult(boolean isSuccess, int total, int processed) {
        this(isSuccess, true, total, processed);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isWasList() {
        return wasList;
    }

    public void setWasList(boolean wasList) {
        this.wasList = wasList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }
}
