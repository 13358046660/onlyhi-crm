package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/8.
 */
public class ClassAppraiseStarVo {
    private String classAppraiseStarUuid;
    private String content;
    private boolean selected;

    public String getClassAppraiseStarUuid() {
        return classAppraiseStarUuid;
    }

    public void setClassAppraiseStarUuid(String classAppraiseStarUuid) {
        this.classAppraiseStarUuid = classAppraiseStarUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "ClassAppraiseStarVo{" +
                "classAppraiseStarUuid='" + classAppraiseStarUuid + '\'' +
                ", content='" + content + '\'' +
                ", selected=" + selected +
                '}';
    }
}
