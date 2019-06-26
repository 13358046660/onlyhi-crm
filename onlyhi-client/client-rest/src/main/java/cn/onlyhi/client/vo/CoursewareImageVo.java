package cn.onlyhi.client.vo;

public class CoursewareImageVo {
    private String coursewareImageUuid;
    private String coursewareUuid;
    private String imageUrl;
    private Integer width;
    private Integer height;
    private String imageMd5;
    /**图片大小*/
    private Integer imageLength;
    /**
     * 图片名称
     */
    private String imageName;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getImageLength() {
        return imageLength;
    }

    public void setImageLength(Integer imageLength) {
        this.imageLength = imageLength;
    }

    public String getCoursewareImageUuid() {
        return coursewareImageUuid;
    }

    public void setCoursewareImageUuid(String coursewareImageUuid) {
        this.coursewareImageUuid = coursewareImageUuid;
    }

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getImageMd5() {
        return imageMd5;
    }

    public void setImageMd5(String imageMd5) {
        this.imageMd5 = imageMd5;
    }

    @Override
    public String toString() {
        return "CoursewareImageVo{" +
                "coursewareImageUuid='" + coursewareImageUuid + '\'' +
                ", coursewareUuid='" + coursewareUuid + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", imageMd5='" + imageMd5 + '\'' +
                '}';
    }
}