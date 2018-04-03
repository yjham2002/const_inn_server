package utils.kopas;

public class KopasReturn {

    private int code;
    private String msg;
    private ResultForm result;

    public KopasReturn() {
    }

    public KopasReturn(int code) {
        this.code = code;
    }

    public KopasReturn(int code, String msg, String project, int action) {
        this.code = code;
        this.msg = msg;
        this.result = new ResultForm(project, action);
    }

    public KopasReturn(int code, String msg, ResultForm result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public class ResultForm{
        private String project;
        private int action;
        private int status;

        @Override
        public String toString() {
            return "ResultForm{" +
                    "project='" + project + '\'' +
                    ", action=" + action +
                    ", status=" + status +
                    '}';
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public ResultForm() {
        }

        public ResultForm(String project) {
            this.project = project;
        }

        public ResultForm(int action) {
            this.action = action;
        }

        public ResultForm(String project, int action) {
            this.project = project;
            this.action = action;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultForm getResult() {
        return result;
    }

    public void setResult(ResultForm result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "KopasReturn{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}
