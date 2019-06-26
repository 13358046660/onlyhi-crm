package cn.onlyhi.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/12.
 */
public class SystemLogEnum {

    /**
     * 请求访问状态
     */
    public enum STATUS {
        DELETE(0, "无效"),
        NORMAL(1, "正常"),
        EXCEPTION(2, "异常");

        public int key;
        public String value;

        STATUS(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * 项目模块
     */
    public enum MODULE {
        CLIENT(10000001, "客户端"),

        UPLOADFILE(10000007, "文件上传"),
        AGENT(10000008, "招师代理"),
        CC(10000009, "cc"),
        CR(10000010, "cr"),
        AUTH(10000011, "用户权限"),
        TEACHDEPT(10000012, "教学"),
        SR(10000013, "sr"),
        FINANCE(10000014, "财务"),
        KPI(10000015, "kpi"),
        MARKETOPT(10000016, "市场与运营"),
        ORDER(10000017, "订单"),
        TEACHER(10000018, "师资");

        public int key;
        public String value;


        MODULE(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static String getEnumValueByKey(int key) {
            for (SystemLogEnum.MODULE module : SystemLogEnum.MODULE.values()) {
                if (key == module.key) {
                    return module.value;
                }
            }
            return null;
        }

        private static List<EnumObj> enumObjList = new ArrayList<>();

        public static List<EnumObj> getEnumObjeList() {
            if (enumObjList.size() > 0) {
                return enumObjList;
            }
            EnumObj enumObj = new EnumObj();
            for (SystemLogEnum.MODULE module : SystemLogEnum.MODULE.values()) {
                enumObj = new EnumObj();
                enumObj.setKey(module.key);
                enumObj.setValue(module.value);
                enumObjList.add(enumObj);
            }
            return enumObjList;
        }
    }
}
