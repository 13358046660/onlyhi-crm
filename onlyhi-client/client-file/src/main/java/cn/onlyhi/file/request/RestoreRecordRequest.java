package cn.onlyhi.file.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author wqz
 * <p>
 * Created by wqz on 2018/07/27.
 */
public class RestoreRecordRequest extends BaseRequest {
    /**
     * 指定恢复的课程日期对应的课程进入房间记录
     */
    private String restoreDate;
    /**
     *  标识恢复类型，1昨天的，2指定日期的，3指定日期且课程id的 4全部
     */
    private Integer restoreType;
    private String courseUuid;
    private String courseDate;

    @Override
    public String toString() {
        return "RestoreRecordRequest{" +
                "restoreDate='" + restoreDate + '\'' +
                ", restoreType=" + restoreType +
                ", courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                '}';
    }

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public Integer getRestoreType() {
        return restoreType;
    }

    public void setRestoreType(Integer restoreType) {
        this.restoreType = restoreType;
    }

    public String getRestoreDate() {
        return restoreDate;
    }

    public void setRestoreDate(String restoreDate) {
        this.restoreDate = restoreDate;
    }
}
