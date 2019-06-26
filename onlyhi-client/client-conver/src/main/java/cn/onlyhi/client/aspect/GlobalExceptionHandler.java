package cn.onlyhi.client.aspect;

import cn.onlyhi.common.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/22.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = MultipartException.class)
    public ResponseEntity<Response> handlerSizeLimitExceededException() throws Exception {
        String msg = "上传文件过大，单个文件不超过30M，总文件大小不超过60M";
        LOGGER.info(msg);
        return ResponseEntity.ok(Response.errorCustom(msg));
    }
}
