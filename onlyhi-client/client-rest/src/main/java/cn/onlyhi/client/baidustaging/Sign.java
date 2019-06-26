package cn.onlyhi.client.baidustaging;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Sign{

   public static String createBaseSign(Map<String, String> map, String key){
        
        TreeMap<String, String> treemap = new TreeMap<String, String>();
        // treemap 按照key字典顺序排序
        treemap.putAll(map);
        Set<Entry<String, String>> entries = treemap.entrySet();
        
        String re = "";
        for (Entry<String, String> entry : entries) {
            re += entry.getKey() + "=" + entry.getValue() + "&";
        }
        re += "key=" + key;

        System.out.println("beforeMd5:  " + re);
        return md5(re, "utf-8");
    }


     public static String md5(String s, String charset) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput;
            if (charset == null || charset.trim().equals("")) {
                btInput = s.getBytes();
            } else {
                btInput = s.getBytes(charset);
            }

            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (UnsupportedEncodingException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return null;
    }


}
