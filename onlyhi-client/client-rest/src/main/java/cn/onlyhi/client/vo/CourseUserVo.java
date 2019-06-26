package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/20.
 */
public class CourseUserVo {
    private Integer agoraUid;
    private String name;

    public Integer getAgoraUid() {
        return agoraUid;
    }

    public void setAgoraUid(Integer agoraUid) {
        this.agoraUid = agoraUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CourseUserVo{" +
                "agoraUid=" + agoraUid +
                ", name='" + name + '\'' +
                '}';
    }
}
