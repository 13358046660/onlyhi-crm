package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/20.
 */
public class SaveTeacherRecommendRequest extends BaseRequest {
    private String name;
    private String image;
    private String teachingAge;
    private String graduateClass;
    private String improveScore;
    private String description;
    private Integer sort;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SaveTeacherRecommendRequest{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", teachingAge='" + teachingAge + '\'' +
                ", graduateClass='" + graduateClass + '\'' +
                ", improveScore='" + improveScore + '\'' +
                ", description='" + description + '\'' +
                ", sort=" + sort +
                "} " + super.toString();
    }
}
