package cn.onlyhi.client.dto;

/**
 * 入参
 * @Author wqz
 * <p>导出课耗生成excel
 * Created by wqz on 2018/10/25
 */
public class StatisticsClassVo extends BaseVo{
    /**
     *  学生或老师姓名
     */
    private String name;
    /**
     *  学生或老师电话
     */
    private String phone;
    /**
     *  课程开始日期
     */
    private String courseStartDate;
    /**
     *  课程结束日期
     */
    private String courseEndDate;
    /**
     *  是否导出1是
     */
    private String isExport;
    private boolean downloadFlag = true;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getIsExport() {
        return isExport;
    }

    public void setIsExport(String isExport) {
        this.isExport = isExport;
    }

    public boolean isDownloadFlag() {
        return downloadFlag;
    }

    public void setDownloadFlag(boolean downloadFlag) {
        this.downloadFlag = downloadFlag;
    }
}
