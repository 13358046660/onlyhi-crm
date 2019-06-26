package cn.onlyhi.file.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/10/25.
 */
public class RecordRequest extends BaseRequest{
    private String recordId;
    private String commChannelId;
    private String courseUuid;
    private String idleLimitSec;
    private String signallingChannelId;
    private String recordRole;
    private String recordRoleUid;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getCommChannelId() {
        return commChannelId;
    }

    public void setCommChannelId(String commChannelId) {
        this.commChannelId = commChannelId;
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getIdleLimitSec() {
        return idleLimitSec;
    }

    public void setIdleLimitSec(String idleLimitSec) {
        this.idleLimitSec = idleLimitSec;
    }

    public String getSignallingChannelId() {
        return signallingChannelId;
    }

    public void setSignallingChannelId(String signallingChannelId) {
        this.signallingChannelId = signallingChannelId;
    }

    public String getRecordRole() {
        return recordRole;
    }

    public void setRecordRole(String recordRole) {
        this.recordRole = recordRole;
    }

    public String getRecordRoleUid() {
        return recordRoleUid;
    }

    public void setRecordRoleUid(String recordRoleUid) {
        this.recordRoleUid = recordRoleUid;
    }

    @Override
    public String toString() {
        return "RecordRequest{" +
                "recordId='" + recordId + '\'' +
                ", commChannelId='" + commChannelId + '\'' +
                ", courseUuid='" + courseUuid + '\'' +
                ", idleLimitSec='" + idleLimitSec + '\'' +
                ", signallingChannelId='" + signallingChannelId + '\'' +
                ", recordRole='" + recordRole + '\'' +
                ", recordRoleUid='" + recordRoleUid + '\'' +
                "} " + super.toString();
    }
}
