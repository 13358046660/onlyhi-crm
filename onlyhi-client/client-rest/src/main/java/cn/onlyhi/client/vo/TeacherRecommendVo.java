package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/19.
 */
public class TeacherRecommendVo {
    private String teacherRecommendUuid;
    private String name;
    private String image;
    private String teachingAge;
    private String graduateClass;
    private String improveScore;
    private String introduction;
    private String sort;

    public String getTeacherRecommendUuid() {
        return teacherRecommendUuid;
    }

    public void setTeacherRecommendUuid(String teacherRecommendUuid) {
        this.teacherRecommendUuid = teacherRecommendUuid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTeachingAge() {
        return teachingAge;
    }

    public void setTeachingAge(String teachingAge) {
        this.teachingAge = teachingAge;
    }

    public String getGraduateClass() {
        return graduateClass;
    }

    public void setGraduateClass(String graduateClass) {
        this.graduateClass = graduateClass;
    }

    public String getImproveScore() {
        return improveScore;
    }

    public void setImproveScore(String improveScore) {
        this.improveScore = improveScore;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Override
    public String toString() {
        return "TeacherRecommendVo{" +
                "teacherRecommendUuid='" + teacherRecommendUuid + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", teachingAge='" + teachingAge + '\'' +
                ", graduateClass='" + graduateClass + '\'' +
                ", improveScore='" + improveScore + '\'' +
                ", introduction='" + introduction + '\'' +
                ", sort='" + sort + '\'' +
                '}';
    }
}
