package cn.onlyhi.client.common;

import java.io.Serializable;

/**
 * Message基类：
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/4.
 */
//必须实现序列,serialVersionUID 一定要有,否者在netty消息序列化反序列化会有问题，接收不到消息！！！
public class BaseMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private int type;   //1:ping    2:login 3:断线重连 4:在上课
    /**客户端生成的设备id*/
    private String deviceId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
