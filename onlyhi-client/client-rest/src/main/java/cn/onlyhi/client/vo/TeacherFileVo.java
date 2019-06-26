package cn.onlyhi.client.vo;

/**
 *
 * @author shitongtong
 * Created by shitongtong on 2018/01/29
 */
public class TeacherFileVo {

    /**
     * 用途  1头像 2 身份证正面 3 身份证反面 4 银行卡正面 5 银行卡反面 6 兼职协议第一页 7 兼职协议第二页 8  信息登记表
     */
    private Integer purpose;

    /**
     * 文件地址路径
     */
    private String fileAddress;

    /**
     * 原始文件名
     */
    private String fileOriginalName;

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    public Integer getPurpose() {
        return purpose;
    }

    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }

    public String getFileAddress() {
        return fileAddress;
    }

    public void setFileAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    @Override
    public String toString() {
        return "TeacherFileVo{" +
                "purpose=" + purpose +
                ", fileAddress='" + fileAddress + '\'' +
                ", fileOriginalName='" + fileOriginalName + '\'' +
                '}';
    }
}