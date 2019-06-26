package cn.onlyhi.client.vo;

/**
 * @Author ywj
 * <p>
 */
public class ViewTeacherAppraiseVo {
    private String appraiseContent;
    private String createTime;

    public String getAppraiseContent() {
        return appraiseContent;
    }

    public void setAppraiseContent(String appraiseContent) {
        this.appraiseContent = appraiseContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ViewTeacherAppraiseVo{" +
                "appraiseContent='" + appraiseContent + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
