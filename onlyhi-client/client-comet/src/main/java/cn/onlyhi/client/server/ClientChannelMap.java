package cn.onlyhi.client.server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/4.
 */
public class ClientChannelMap {
    //userMark : channelId
    private static Map<String, String> map = new ConcurrentHashMap<>();

    public static void add(String userMark, String channelId) {
        map.put(userMark, channelId);
    }

    public static String get(String userMark) {
        return map.get(userMark);
    }

    public static void remove(String userMark) {
        map.remove(userMark);
    }

    public static void removeById(String channelId) {
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() == channelId) {
                map.remove(entry.getKey());
            }
        }
    }

    public static String print() {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append(";");
        }
        return sb.toString();
    }
}
