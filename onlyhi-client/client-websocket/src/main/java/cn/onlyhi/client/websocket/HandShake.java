
package cn.onlyhi.client.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static cn.onlyhi.common.constants.Constants.WEBSOCKET_TOKEN;
import static cn.onlyhi.common.constants.Constants.WEBSOCKET_TYPE;

/**
 * Socket建立连接（握手）和断开
 */
public class HandShake extends HttpSessionHandshakeInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandShake.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                // 使用token和type区分WebSocketHandler，以便定向发送消息
                String token = (String) session.getAttribute(WEBSOCKET_TOKEN);
                String type = (String) session.getAttribute(WEBSOCKET_TYPE);
                attributes.put(WEBSOCKET_TOKEN, token);
                attributes.put(WEBSOCKET_TYPE, type);
                LOGGER.info("beforeHandshake token={},type={}", token, type);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        // TODO Auto-generated method stub
        super.afterHandshake(request, response, wsHandler, ex);
    }

}
