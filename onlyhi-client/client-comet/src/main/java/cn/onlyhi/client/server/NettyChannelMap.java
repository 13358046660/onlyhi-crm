package cn.onlyhi.client.server;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/4.
 */
public class NettyChannelMap {
    //channelId : SocketChannel
    private static Map<String, SocketChannel> map = new ConcurrentHashMap<>();

    public static void add(String channelId, SocketChannel socketChannel) {
        map.put(channelId, socketChannel);
    }

    public static SocketChannel get(String channelId) {
        return map.get(channelId);
    }

    public static void remove(String channelId) {
        map.remove(channelId);
    }

    public static String getId(SocketChannel socketChannel) {
        Set<Map.Entry<String, SocketChannel>> entrySet = map.entrySet();
        for (Map.Entry<String, SocketChannel> entry : entrySet) {
            if (entry.getValue() == socketChannel) {
                return entry.getKey();
            }
        }
        return "";
    }

    public static void removeBySocketChannel(SocketChannel socketChannel) {
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() == socketChannel) {
                map.remove(entry.getKey());
            }
        }
    }

    public static String print() {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, SocketChannel>> entrySet = map.entrySet();
        for (Map.Entry<String, SocketChannel> entry : entrySet) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append(";");
        }
        return sb.toString();
    }
}
