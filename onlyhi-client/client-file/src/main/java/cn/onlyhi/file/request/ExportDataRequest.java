package cn.onlyhi.file.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/12.
 */
public class ExportDataRequest extends BaseRequest {
    private String exportDate;
    private String courseStartDate;
    private String courseEndDate;
    private boolean downloadFlag = true;

    public String getExportDate() {
        return exportDate;
    }

    public void setExportDate(String exportDate) {
        this.exportDate = exportDate;
    }

    public boolean isDownloadFlag() {
        return downloadFlag;
    }

    public void setDownloadFlag(boolean downloadFlag) {
        this.downloadFlag = downloadFlag;
    }

    public String getCourseStartDate() {
        return courseStartDate;
    }

    public void setCourseStartDate(String courseStartDate) {
        this.courseStartDate = courseStartDate;
    }

    public String getCourseEndDate() {
        return courseEndDate;
    }

    public void setCourseEndDate(String courseEndDate) {
        this.courseEndDate = courseEndDate;
    }

    @Override
    public String toString() {
        return "ExportDataRequest{" +
                "exportDate='" + exportDate + '\'' +
                ", courseStartDate='" + courseStartDate + '\'' +
                ", courseEndDate='" + courseEndDate + '\'' +
                ", downloadFlag=" + downloadFlag +
                '}';
    }
}
