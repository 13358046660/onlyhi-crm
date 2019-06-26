package cn.onlyhi.client.aspect;


import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.SystemLog;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.service.SystemLogService;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.SystemLogEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.IpUtil;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.StringUtil;
import cn.onlyhi.common.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import static cn.onlyhi.common.enums.CodeEnum.INVALID_TOKEN;
import static cn.onlyhi.common.enums.CodeEnum.PARAMETER_ERROR;
import static cn.onlyhi.common.enums.CodeEnum.SERRVER_ERROR;
import static cn.onlyhi.common.util.ClientUtil.getToken;
import static cn.onlyhi.common.util.ClientUtil.jsonParamsToRequest;
import static cn.onlyhi.common.util.ClientUtil.setToken;
import static cn.onlyhi.common.util.ClientUtil.validate;


/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/5.
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SystemLogService systemLogService;

    @CrossOrigin
    @Around("within(cn.onlyhi.client.controller..*) && @annotation(logRecordAnnotation)")
    public ResponseEntity<Response> around(ProceedingJoinPoint proceedingJoinPoint, LogRecordAnnotation logRecordAnnotation) throws Throwable {
        LOGGER.info("日志异常统一处理...");
        SystemLog systemLog = logTransfer(logRecordAnnotation);
        try {
            String headerToken = httpServletRequest.getHeader("token");
            //LOGGER.info("headerToken={}", headerToken);

            String requestUrl = httpServletRequest.getRequestURL().toString();
            String clientIp = IpUtil.getLocalIp(httpServletRequest);
            Object[] args = proceedingJoinPoint.getArgs();
            String parameters = Arrays.toString(args);
            parameters = StringUtil.capture(parameters);
            systemLog.setRequestUrl(requestUrl);
            systemLog.setRequestParameters(parameters);
            systemLog.setClientIp(clientIp);

            Object obj = args[0];
            Object controllerObj = proceedingJoinPoint.getTarget();
            Method method = controllerObj.getClass().getDeclaredMethod(proceedingJoinPoint.getSignature().getName(), obj.getClass());
            Annotation[] parameterAnnotation = method.getParameterAnnotations()[0];
            if (parameterAnnotation.length == 0 || !(parameterAnnotation[0] instanceof RequestBody)) {
                jsonParamsToRequest(httpServletRequest, obj);
            }
            if (StringUtils.isNotBlank(headerToken)) {
                setToken(obj, headerToken);
            }
            String token = getToken(obj);
            LOGGER.info("请求url【{}】,请求参数【{}】", requestUrl, obj);
            if (systemLog.getCheckToken()) {
                if (token == null) {
                    String mesage = "token不可为空!";
                    systemLog.setStatus(SystemLogEnum.STATUS.EXCEPTION.key);
                    systemLog.setRemark("异常");
                    systemLog.setExceptionMessage(mesage);
                    ResponseEntity<Response> responseEntity = ResponseEntity.ok(Response.error(PARAMETER_ERROR.getCode(), mesage));
                    systemLog.setResponseMessage(responseEntity.toString());
                    saveSystemLog(systemLog);
                    LOGGER.info("ResponseMessage=={}",systemLog.getResponseMessage());
                    return responseEntity;
                }
                LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
                if (loginUserCache == null) {
                    systemLog.setRemark("无效token");
                    ResponseEntity<Response> responseEntity = ResponseEntity.ok(Response.error(INVALID_TOKEN));
                    systemLog.setResponseMessage(responseEntity.toString());
                    saveSystemLog(systemLog);
                    LOGGER.info("ResponseMessage=={}",systemLog.getResponseMessage());
                    return responseEntity;
                }
                systemLog.setDeviceType(loginUserCache.getDeviceType());
                systemLog.setUserType(loginUserCache.getUserType());
                systemLog.setPhone(loginUserCache.getPhone());
                systemLog.setCreateUid(loginUserCache.getUserUuid());
                systemLog.setOperator(loginUserCache.getUserName());

            }
            if (token != null) {
                LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
                if (loginUserCache != null) {
                    String userType = loginUserCache.getUserType();
                    String phone = loginUserCache.getPhone();
                    systemLog.setDeviceType(loginUserCache.getDeviceType());
                    systemLog.setUserType(userType);
                    systemLog.setPhone(phone);
                    systemLog.setCreateUid(loginUserCache.getUserUuid());
                    systemLog.setOperator(loginUserCache.getUserName());
                    LOGGER.info("用户【" + loginUserCache.getUserName() + "】 ，身份【" + userType + "】");
                    String indexToken = phone + userType;
                    redisService.expire(indexToken);
                    redisService.expire(loginUserCache.getToken());
                }
            }

            if (obj instanceof BaseRequest) {
                validate(obj);
            }
            Object result = proceedingJoinPoint.proceed();
            systemLog.setRemark("正常");
            systemLog.setResponseMessage(StringUtil.capture(result.toString(), 1024));
            saveSystemLog(systemLog);
            LOGGER.info("ResponseMessage=={}",systemLog.getResponseMessage());
            return (ResponseEntity<Response>) result;
        } catch (Exception e) {
            LOGGER.error("异常：", e);
            systemLog.setStatus(SystemLogEnum.STATUS.EXCEPTION.key);
            systemLog.setRemark("异常");
            String message = e.getMessage();
            if (message == null) {
                message = "空指针异常！";
            } else {
                message = StringUtil.capture(message);
            }
            systemLog.setExceptionMessage(message);
            Response response;
            if (e instanceof ValidationException) {
                response = Response.error(PARAMETER_ERROR.getCode(), e.getMessage());
            } else {
                response = Response.error(SERRVER_ERROR);
            }
            ResponseEntity<Response> responseEntity = ResponseEntity.ok(response);
            systemLog.setResponseMessage(StringUtil.capture(responseEntity.toString(), 1024));
            saveSystemLog(systemLog);
            LOGGER.info("ResponseMessage=={}",systemLog.getResponseMessage());
            return responseEntity;
        }
    }

    private void saveSystemLog(SystemLog systemLog) {
        try {
            systemLogService.save(systemLog);
        } catch (Exception e) {
            LOGGER.info("日志保存失败，异常：{}" + e);
            LOGGER.error(e.getMessage());
        }
    }

    private SystemLog logTransfer(LogRecordAnnotation logRecordAnnotation) {
        if (logRecordAnnotation == null) {
            return null;
        }
        SystemLog systemLog = new SystemLog();
        systemLog.setSystemLogUuid(UUIDUtil.randomUUID2());
        int moduleCode = logRecordAnnotation.moduleCode();
        String moduleName = logRecordAnnotation.moduleName();
        int pageCode = logRecordAnnotation.pageCode();
        String pageName = logRecordAnnotation.pageName();
        int methodCode = logRecordAnnotation.methodCode();
        String methodName = logRecordAnnotation.methodName();
        String description = logRecordAnnotation.description();
        boolean checkToken = logRecordAnnotation.checkToken();

        systemLog.setModuleCode(moduleCode);
        systemLog.setModuleName(moduleName);
        systemLog.setPageCode(pageCode);
        systemLog.setPageName(pageName);
        systemLog.setMethodCode(methodCode);
        systemLog.setMethodName(methodName);
        systemLog.setDescription(description);
        systemLog.setCheckToken(checkToken);

        return systemLog;
    }


}
