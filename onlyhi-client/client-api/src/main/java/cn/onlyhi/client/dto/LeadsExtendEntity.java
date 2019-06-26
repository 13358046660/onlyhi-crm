package cn.onlyhi.client.dto;

import cn.onlyhi.client.po.Leads;

/**
 * @Author wqz
 * <p>学生头像及基础信息实体
 * Created by wqz on 2018/5/7
 */
public class LeadsExtendEntity extends Leads {
    /**
     *  头像链接
     */
    private String iconurl;
    /**
     *  头像名称
     */
    private String iconname;

    public String getIconname() {
        return iconname;
    }

    public void setIconname(String iconname) {
        this.iconname = iconname;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }
}
