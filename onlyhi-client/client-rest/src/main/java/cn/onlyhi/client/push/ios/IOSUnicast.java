package cn.onlyhi.client.push.ios;


import cn.onlyhi.client.push.IOSNotification;

public class IOSUnicast extends IOSNotification {
    public IOSUnicast(String appkey, String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
//			this.setPredefinedKeyValue("type", "unicast");
        this.setPredefinedKeyValue("type", "listcast");
    }

    public void setDeviceToken(String token) throws Exception {
        setPredefinedKeyValue("device_tokens", token);
    }
}
