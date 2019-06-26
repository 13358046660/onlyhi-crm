package cn.onlyhi.file.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/13.
 */
public class CourseUuidRequest2 extends BaseRequest {
    private String courseUuid;
    private String recordLogsDirPath;

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getRecordLogsDirPath() {
        return recordLogsDirPath;
    }

    public void setRecordLogsDirPath(String recordLogsDirPath) {
        this.recordLogsDirPath = recordLogsDirPath;
    }

    @Override
    public String toString() {
        return "CourseUuidRequest2{" +
                "courseUuid='" + courseUuid + '\'' +
                ", recordLogsDirPath='" + recordLogsDirPath + '\'' +
                "} " + super.toString();
    }
}
