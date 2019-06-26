package cn.onlyhi.client.websocket;

import cn.onlyhi.common.constants.Constants;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.onlyhi.common.util.ClientUtil.getUrlParams;

@Component
public class MyTextWebSocketHandler extends TextWebSocketHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(MyTextWebSocketHandler.class);
    public static final Map<String, WebSocketSession> userSocketSessionMap = new ConcurrentHashMap<>();

    /**
     * 建立连接后
     */
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String sessionId = session.getId();
        Map<String, Object> map = getUrlParams(query);
        Object tokenObj = map.get(Constants.WEBSOCKET_TOKEN);
        Object typeObj = map.get(Constants.WEBSOCKET_TYPE);
        if (tokenObj != null && typeObj != null) {
            userSocketSessionMap.put(tokenObj + Constants.NONCE + typeObj, session);
        }
        LOGGER.info("Connection allowed token={" + tokenObj + "},type={" + typeObj + "},sessionId={" + sessionId + "},sessionSize={" + userSocketSessionMap.values().size() + "}");
    }

    /**
     * 消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
     */
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message.getPayloadLength() == 0) {
            return;
        }
        session.sendMessage(message);
        LOGGER.info("handleMessage: {}", message.getPayload().toString());
    }

    /**
     * 消息传输错误处理
     */
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        removeSession(session);
    }

    /**
     * 关闭连接后
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        removeSession(session);
        LOGGER.info("afterConnectionClosed = " + session + " closeStatus= " + closeStatus.getReason() + ",code=" + closeStatus.getCode());
    }

    private void removeSession(WebSocketSession session) {
        try {
            Set<Entry<String, WebSocketSession>> entrySet = userSocketSessionMap.entrySet();
            for (Entry<String, WebSocketSession> entry : entrySet) {
                if (entry.getValue().equals(session)) {
                    userSocketSessionMap.remove(entry.getKey());
                }
            }
            LOGGER.info("removeSession == session=" + session + ",sessionSize=" + userSocketSessionMap.values().size());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     * @throws IOException
     */
    public void broadcast(final TextMessage message) throws IOException {
        Iterator<Entry<String, WebSocketSession>> it = userSocketSessionMap.entrySet().iterator();

        // 多线程群发
        while (it.hasNext()) {

            final Entry<String, WebSocketSession> entry = it.next();

            if (entry.getValue().isOpen()) {
                // entry.getValue().sendMessage(message);
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            if (entry.getValue().isOpen()) {
                                entry.getValue().sendMessage(message);
                            }
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }

                }).start();
            }

        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param key
     * @param message
     * @throws IOException
     */
    public static void sendMessageToUser(String key, String message) throws IOException {
        TextMessage textMessage = new TextMessage(message);
        WebSocketSession session = userSocketSessionMap.get(key);
        LOGGER.info("send textMessage {} to {}", JSONObject.toJSONString(textMessage), key);
        if (session != null && session.isOpen()) {
            session.sendMessage(textMessage);
        }
    }

}
