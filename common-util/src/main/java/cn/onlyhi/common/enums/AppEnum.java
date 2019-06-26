package cn.onlyhi.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author shitongtong
 * <p>
 * Created by Administrator on 2017/3/16.
 */
public interface AppEnum {

    /**
     * 设备类型(phone or pad)
     */
    enum DeviceType {
        ANDROID("ANDROID", 1),
        IOS("IOS", 1),
        APAD("APAD", 2),
        IPAD("IPAD", 2),
        WEIXIN("WEIXIN", 3);

        public String key;
        public Integer value;

        DeviceType(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public static Integer getEnumValueByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (AppEnum.DeviceType deviceType : AppEnum.DeviceType.values()) {
                if (key.equals(deviceType.key)) {
                    return deviceType.value;
                }
            }
            return null;
        }
    }

}
