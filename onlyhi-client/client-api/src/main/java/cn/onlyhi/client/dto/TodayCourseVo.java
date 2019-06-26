package cn.onlyhi.client.dto;

/**
 * 入参
 * @Author wqz
 * <p>客户端-员工技术支持登陆后课程列表检索
 *  导出课耗生成excel
 * Created by wqz on 2018/5/4
 */
public class TodayCourseVo extends BaseVo{
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

    @Override
    public String toString() {
        return "TodayCourseVo{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", courseStartDate='" + courseStartDate + '\'' +
                ", courseEndDate='" + courseEndDate + '\'' +
                '}';
    }
}
