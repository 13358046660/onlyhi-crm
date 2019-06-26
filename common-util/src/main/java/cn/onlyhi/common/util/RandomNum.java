package cn.onlyhi.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成工具
 *
 * @author csy
 */
public class RandomNum {

    public static Map<String, String> stu_no = new HashMap<>();

    static {
        stu_no.put("小一", "11");
        stu_no.put("小二", "12");
        stu_no.put("小三", "13");
        stu_no.put("小四", "14");
        stu_no.put("小五", "15");
        stu_no.put("小六", "16");
        stu_no.put("预初", "20");
        stu_no.put("初一", "21");
        stu_no.put("初二", "22");
        stu_no.put("初三", "23");
        stu_no.put("初四", "24");
        stu_no.put("高一", "31");
        stu_no.put("高二", "32");
        stu_no.put("高三", "33");
        stu_no.put("语文", "01");
        stu_no.put("数学", "02");
        stu_no.put("英语", "03");
        stu_no.put("历史", "04");
        stu_no.put("地理", "05");
        stu_no.put("政治", "06");
        stu_no.put("物理", "07");
        stu_no.put("生物", "08");
        stu_no.put("化学", "09");
        stu_no.put("科学", "10");


    }

    /**
     * 生成指定长度的数字+字母随机字符串码
     *
     * @param len 指定长度
     * @return 随机字符串码
     * @author：zhangbing
     * @date：2016/11/02 17:24
     */
    public static String randomStrCode(int len) {
        int d;
        Double e;
        String b = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String c = "";
        for (d = 0; len > d; d += 1) {
            e = Math.random() * b.length();
            e = Math.floor(e);
            c += b.charAt((new Double(e)).intValue());
        }
        return c.toUpperCase();
    }

    /**
     * 随机生成指定长度的数字字符创
     *
     * @return
     */
    public static String randomNumberCode(int len) {
        int d;
        Double e;
        String b = "0123456789";
        String c = "";
        for (d = 0; len > d; d += 1) {
            e = Math.random() * b.length();
            e = Math.floor(e);
            c += b.charAt((new Double(e)).intValue());
        }
        return c.toUpperCase();
    }

    /**
     * 生成学号
     *
     * @return {@link String}
     */
    public static String generateStuNo(String grade, String subject) {
        String stuNo = stu_no.get(grade) + stu_no.get(subject) + randomNumberCode(6);
        return stuNo;
    }

}
