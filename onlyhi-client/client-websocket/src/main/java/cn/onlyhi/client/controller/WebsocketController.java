package cn.onlyhi.client.controller;

import cn.onlyhi.client.request.MessageRequest;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.constants.Constants;
import cn.onlyhi.common.util.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static cn.onlyhi.client.websocket.MyTextWebSocketHandler.sendMessageToUser;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/3.
 */
@RestController
@RequestMapping("/websocket")
public class WebsocketController {

    /**
     * 发送消息
     *
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/sendMessage")
    @LogRecordAnnotation(moduleCode = 100004, moduleName = "websocket", methodCode = 1000041, methodName = "sendMessage", description = "发送消息")
    public ResponseEntity<Response> sendMessage(MessageRequest request) throws IOException {
        String token = request.getToken();
        String type = request.getType();
        String message = request.getMessage();
        String key = token + Constants.NONCE + type;
        sendMessageToUser(key, message);
        return ResponseEntity.ok(Response.success());
    }
}
