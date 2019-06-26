package cn.onlyhi.common.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by Administrator on 2017/3/16.
 */
public interface ClientEnum {

    enum DeviceType {
        ANDROID("ANDROID", "安卓系统"), IOS("IOS", "ios系统"), PC("PC", "windows系统"), APAD("APAD", "安卓pad"), IPAD("IPAD", "iosPad");

        public String key;
        public String value;

        DeviceType(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    enum UserType {
        STUDENT("STUDENT", "学生"),
        PATRIARCH("PATRIARCH", "家长"),
        TEACHER("TEACHER", "教师"),
        CC("CC", "cc"),
        CR("CR", "cr"),
        TA("TA", "技术支持"),
        MONITOR("MONITOR", "教学监课"),
        QC("QC", "质检监课");

        public String key;
        public String value;

        UserType(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private static List<String> enumKeyList = new ArrayList<>();

        public static List<String> getEnumKeyList() {
            if (enumKeyList.size() > 0) {
                return enumKeyList;
            }
            EnumStringObj enumObj;
            for (ClientEnum.UserType userType : ClientEnum.UserType.values()) {
                enumKeyList.add(userType.key);
            }
            return enumKeyList;
        }

        public static UserType getUserTypeByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (ClientEnum.UserType userType : ClientEnum.UserType.values()) {
                if (key.toUpperCase().equals(userType.key)) {
                    return userType;
                }
            }
            return null;
        }
    }

    enum CoursewareType {
        SYSTEM("SYSTEM", "系统课件"), TEACHER("TEACHER", "教师课件");

        public String key;
        public String value;

        CoursewareType(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * 性别 男女
     */
    enum Gender {
        FEMALE("女", 1),
        MALE("男", 0);

        public String key;
        public Integer value;

        Gender(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public static Integer getEnumValueByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (ClientEnum.Gender gender : ClientEnum.Gender.values()) {
                if (key.equals(gender.key)) {
                    return gender.value;
                }
            }
            return null;
        }
    }

    /**
     * 课程优惠类别
     */
    enum ActivityType {
        normal("课程优惠", "常规");
//        summer("暑期优惠", "暑秋联报");

        public String key;
        public String value;

        ActivityType(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public static String getEnumValueByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (ClientEnum.ActivityType activityType : ClientEnum.ActivityType.values()) {
                if (key.equals(activityType.key)) {
                    return activityType.value;
                }
            }
            return null;
        }

        private static List<EnumStringObj> enumObjList = new ArrayList<>();

        public static List<EnumStringObj> getEnumObjeList() {
            if (enumObjList.size() > 0) {
                return enumObjList;
            }
            EnumStringObj enumObj;
            for (ClientEnum.ActivityType activityType : ClientEnum.ActivityType.values()) {
                enumObj = new EnumStringObj();
                enumObj.setKey(activityType.key);
                enumObj.setValue(activityType.value);
                enumObjList.add(enumObj);
            }
            return enumObjList;
        }
    }

    /**
     * 0:退出房间; 1:进入房间
     */
    enum RecordType {
        ENTER(1, "进入房间"),
        OUT(0, "退出房间");

        public int key;
        public String value;

        RecordType(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    enum Sort {
        ASC("asc", "正序"),
        DESC("desc", "倒序");

        public String key;
        public String value;

        Sort(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public static String getEnumValueByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (ClientEnum.Sort sort : ClientEnum.Sort.values()) {
                if (key.equals(sort.key)) {
                    return sort.value;
                }
            }
            return null;
        }
    }
    /**课程包类型*/
    enum CoursePackageType {
        NORMAL("普通", 0),
        TSINGHUA("清北", 1),
        STAR("明星", 2);
        public String key;
        public Integer value;

        CoursePackageType(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }
    /**支付状态0:已关闭;    1:待支付;  2:已支付;*/
    enum PayStatus {
        CLOSED("已关闭", 0),
        TOBEPAID("待支付", 1),
        HAVETOPAY("已支付", 2);
        public String key;
        public Integer value;
        PayStatus(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }
    /**是否支付状态0：待审核(待支付)1：支付成功2：支付失败3:已被拆单;*/
    enum IsPay {
        TOBEPAID("待支付", 0),
        HAVETOPAID("支付成功", 1),
        PAYFAILURE("支付失败", 2),
        HASBEENSPLIT("已被拆单", 3);
        public String key;
        public Integer value;
        IsPay(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }
}
