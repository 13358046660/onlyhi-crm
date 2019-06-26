package cn.onlyhi.common.util;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/11/16.
 */
public class CmdExecResult {
    private boolean execStatus;
    private String successInfo;
    private String errorInfo;

    public boolean isExecStatus() {
        return execStatus;
    }

    public void setExecStatus(boolean execStatus) {
        this.execStatus = execStatus;
    }

    public String getSuccessInfo() {
        return successInfo;
    }

    public void setSuccessInfo(String successInfo) {
        this.successInfo = successInfo;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
