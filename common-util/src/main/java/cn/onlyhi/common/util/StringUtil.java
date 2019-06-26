package cn.onlyhi.common.util;

import java.util.Random;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/12.
 */
public class StringUtil {

    /**
     * 判断字符串是否为字符串“null”
     *
     * @param parameter 参数
     * @return 不等于'null'则返回true，反之则返回false
     */
    public static boolean isNotStringNull(String parameter) {
        return !(parameter.toLowerCase().equals("null"));
    }

    /**
     * 若content长度大于length则从开始截取返回length长度的content
     * 若content长度不大于length则返回content
     *
     * @param content
     * @param length
     * @return
     */
    public static String capture(String content, int length) {
        if (content == null) {
            return null;
        }
        if (content.length() > length) {
            return content.substring(0, length);
        }
        return content;
    }

    /**
     * 若content长度大于2048则从开始截取返回length长度的content
     * 若content长度不大于2048则返回content
     *
     * @param content
     * @return
     */
    public static String capture(String content) {
        return capture(content, 2048);
    }


    /**
     * 获取指定长度的随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }

        for (char ch = 'a'; ch <= 'z'; ++ch) {
            tmp.append(ch);
        }
        char[] symbols = tmp.toString().toCharArray();
        Random random = new Random();
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

}
