package cn.onlyhi.client.controller;


import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.dto.LoginUserCachePhp;
import cn.onlyhi.client.dto.MessageReturnDto;
import cn.onlyhi.client.po.*;
import cn.onlyhi.client.request.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.client.vo.AuthCodeVo;
import cn.onlyhi.client.vo.StudentVo;
import cn.onlyhi.client.vo.UserRegisterVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.annotation.RequestLimit;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.enums.MessageSendTemplateEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import static cn.onlyhi.common.enums.ClientEnum.DeviceType.PC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.MONITOR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.PATRIARCH;
import static cn.onlyhi.common.enums.ClientEnum.UserType.QC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TA;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.*;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;
import static cn.onlyhi.common.util.ClientUtil.getFourRandom;

/**
 * @author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/15.
 */
@ImportResource(value = {"classpath:/application-context-core.xml"})
@RestController
@RequestMapping("/client/user")
public class ClientUserController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUserController.class);

    @Autowired
    private TcTeacherService teacherService;
    @Autowired
    private UserFeedbackService userFeedbackService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private LeadsLogService leadsLogService;
    @Autowired
    private PatriarchService patriarchService;
    @Autowired
    private MessageSendTemplateService messageSendTemplateService;
    @Autowired
    private MessageSendRecordService messageSendRecordService;
    @Autowired
    private UserDeviceInformationService userDeviceInformationService;
    @Autowired
    private UserNeteaseimService userNeteaseimService;
    private static String signName = "【嗨课堂】";
    //是否需要状态报告，需要true，不需要false
    private static String report = "true";
    //@Autowired
    //private ThreadPoolTaskExecutor taskExecutor;
    //@Autowired
    //private MultiThreadProcessService multiThreadProcessService;
    @Value("${phpStaging.url}")
    private String url;
    /**
     * 发送验证码
     * 不改变原方法原则，废弃此方法
     * @param request
     * @return
     */
    @Deprecated
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001176, methodName = "sendAuthCode", description = "发送验证码", checkToken = false)
    public ResponseEntity<Response> sendAuthCode(PhoneRequest request) throws Exception {
        String phone = request.getPhone();
        String phoneAuthCodeKey = phone + "ClientAuthCode";
        String fourRandom = getFourRandom();
        int state;
        if ("17721432057".equals(phone)) {
            fourRandom = "8888";
            state = 0;
            LOGGER.info("**app..store..test**");
        } else {
            MessageSendTemplate messageSendTemplate = messageSendTemplateService.findByPurpose(MessageSendTemplateEnum.PURPOSE.APP.key);
            String messageContent = messageSendTemplate.getMessageContent();
            String content = messageContent.replace("#{authCode}", fourRandom);
            MessageReturnDto dto = messageService.sendMessage(phone, content);
            state = dto.getState();
            String stateValue = dto.getStateValue();
            //保存发送短信记录
            MessageSendRecord messageSendRecord = new MessageSendRecord();
            messageSendRecord.setMessageSendTemplateUuid(messageSendTemplate.getMessageSendTemplateUuid());
            messageSendRecord.setMessageContent(content);
            messageSendRecord.setSendPhone(phone);
            messageSendRecord.setSendStatus(state);
            messageSendRecord.setSendInfo(stateValue);
            messageSendRecordService.save(messageSendRecord);
        }
        if (state == -1) {
            return ResponseEntity.ok(Response.error(SERRVER_ERROR));
        } else if (state == 0) {    //发送成功
            redisService.set(phoneAuthCodeKey, fourRandom, 60 * 5);
            return ResponseEntity.ok(Response.success());
        } else {
            return ResponseEntity.ok(Response.error(SMS_SEND_FAIL));
        }
    }

    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001176, methodName = "sendAuthCode", description = "发送验证码", checkToken = false)
    //@PostMapping("/sendAuthCode")
    public ResponseEntity<Response> sendAuthCodeNew(PhoneRequest request) throws Exception {
        String code = request.getCode();
        String phone = request.getPhone();
        String phoneAuthCodeKey = phone + "ClientAuthCode";
        String fourRandom = getFourRandom();
        int state;

        Map<String, Object> msgMap = new HashMap<String, Object>();
        //Message message = null;
        if (null == code) {
            code = HttpSender.getSixRandom();
        }
        //String phoneContent = "您的验证码是"+code+"。此验证码用于昂立嗨课堂注册或找回密码。5分钟内有效。请勿泄漏并尽快输入验证。源自交大，值得信赖。";
        MessageSendTemplate messageSendTemplate = messageSendTemplateService.findByPurpose(MessageSendTemplateEnum.PURPOSE.APP.key);
        String messageContent = messageSendTemplate.getMessageContent();
        String content = messageContent.replace("#{authCode}", fourRandom);
        //MessageReturnDto dto = messageService.sendMessage(phone, content);
        //state = dto.getState();

        String mobile = phone;// 手机号码，多个号码使用","分割dd
        boolean needstatus = true;// 是否需要状态报告，需要true，不需要false
        String extno = null;// 扩展码
        Integer sendType = 2;
        //message = new Message( content, code, new Date(), sendType, 0, content, extno, mobile);

        String smsSingleRequestServerUrl = "http://smsbj1.253.com/msg/send/json";
        SmsSendRequest smsSingleRequest = new SmsSendRequest("N2363083", "Haiketang_2016", signName + content, phone, report);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        //message.setMsgId(smsSingleResponse.getMsgId());

        //保存发送短信记录
        MessageSendRecord messageSendRecord = new MessageSendRecord();
        messageSendRecord.setMessageSendTemplateUuid(messageSendTemplate.getMessageSendTemplateUuid());
        messageSendRecord.setMessageContent(content);
        messageSendRecord.setSendPhone(phone);
        messageSendRecord.setSendStatus(Integer.parseInt(smsSingleResponse.getCode()));
        if (StringUtils.isEmpty(smsSingleResponse.getErrorMsg())) {
            messageSendRecord.setSendInfo("success");
        } else {
            messageSendRecord.setSendInfo(smsSingleResponse.getErrorMsg());
        }
        messageSendRecordService.save(messageSendRecord);

        if ("0".equals(smsSingleResponse.getCode())) {
         /*   message.setSendInfo("发送成功");
            msgMap.put("result", "0");
            msgMap.put("message", code);*/
            AuthCodeVo authCodeVo = new AuthCodeVo();
            authCodeVo.setAuthCode(fourRandom);
            redisService.set(phoneAuthCodeKey, fourRandom, 60 * 5);
            return ResponseEntity.ok(Response.success(authCodeVo));
        } else {
     /*       message.setCode("");
            message.setStatus(Integer.parseInt(smsSingleResponse.getCode()));
            message.setSendInfo(smsSingleResponse.getErrorMsg());
            msgMap.put("result", "1");
            msgMap.put("message", smsSingleResponse.getErrorMsg());*/
            return ResponseEntity.ok(Response.error(SMS_SEND_FAIL));
        }
        //messageMapper.insertSelective(message);
        //return JSON.toJSONString(msgMap);
    }
    /**
     * 发送验证码
     * 调用php
     * @param request
     * @return
     */
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001176, methodName = "sendAuthCodeNewPhp", description = "发送验证码", checkToken = false)
    @PostMapping("/sendAuthCode")
    public ResponseEntity<Response> sendAuthCodeNewPhp(PhoneRequest request) throws Exception {
        Response phpResponse =null;
        try{
            String phpUrl = url.concat("client/user/sendAuthCode");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", request.getPhone());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("sendAuthCodePhp={}", phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001165, methodName = "resetpsdByJson", description = "重置密码")
    @PostMapping("/resetpsdByJson")
    public ResponseEntity<Response> resetpsdByJson(@RequestBody RestPsdRequest request) {
        return resetpsdComPhp(request);
    }

    @PostMapping("/resetpsd")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001166, methodName = "resetpsd", description = "重置密码")
    public ResponseEntity<Response> resetpsd(RestPsdRequest request) {
        return resetpsdComPhp(request);
    }

    /**
     * 重置密码
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> resetpsdCom(RestPsdRequest request) {
        String phone = request.getPhone();
        String authCodeString = getAuthCode(phone);
        if (StringUtils.isBlank(authCodeString)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        String authCode = request.getAuthCode();
        if (!authCode.equals(authCodeString)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }
        String password = request.getPassword();
        String token = request.getToken();
        LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
        String userType = loginUserCache.getUserType();
        if (STUDENT.name().equalsIgnoreCase(userType)) {    //学生
            Leads leads = leadsService.findLeadsByPhone(phone);
            if (leads == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            leads.setPassword(password);
            leadsService.update(leads);
        } else if (TEACHER.name().equalsIgnoreCase(userType)) { //教师
            TcTeacher teacher = teacherService.findTeacherByPhone(phone);
            if (teacher == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            teacherService.updatePasswordByUuid(teacher.getUuid(), password);
        } else {
            return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
        }
        loginUserCache.setPassword(password);
        redisService.setLoginUserCache(token, loginUserCache);

        return ResponseEntity.ok(Response.success());
    }
    /**
     * 重置密码
     * 调用php
     * @param request
     * @return
     */
    private ResponseEntity<Response> resetpsdComPhp(RestPsdRequest request) {
        Response phpResponse =null;
        try{
            //获取用户类型
            String userType=redisService.getLoginUserCache(request.getToken()).getUserType();
            String phpCacheToken = redisService.getLoginUserCache(request.getToken()).getToken();
            String phone =request.getPhone();
            String authCode =request.getAuthCode();
            String password =request.getPassword();
            String phpUrl = url.concat("client/user/resetpsdByJson");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone",phone);
            paramMap.put("authCode",authCode);
            paramMap.put("password",password);
            paramMap.put("userType",userType);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));
            LOGGER.info("重置密码 传给php {}", JSON.toJSONString(paramMap));

            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("resetpsdComPhp phpResult {}", phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 获取用户手机号
     *
     * @param request
     * @return
     * @throws Exception
     */
    //@GetMapping("/getUserPhone")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001164, methodName = "getUserPhone", description = "获取用户手机号")
    public ResponseEntity<Response> getUserPhone(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        return ResponseEntity.ok(Response.success(loginUserCache.getPhone()));
    }
    /**
     * 获取用户手机号
     * 调用php
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getUserPhone")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001164, methodName = "getUserPhonePhp", description = "获取用户手机号")
    public ResponseEntity<Response> getUserPhonePhp(BaseRequest request) {
        Response phpResponse =null;
        try {
            String phpCacheToken = redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl =url.concat("client/user/getUserPhone");
            Map<String, String> paramMap = new HashMap<>();
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("client/user/getUserPhone={}",phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }

    @PostMapping("/isRegisterByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000111, methodName = "isRegisterByJson", description = "判断账号是否注册", checkToken = false)
    public ResponseEntity<Response> isRegisterByJson(@RequestBody CheckRegisterRequest request) throws Exception {
        return isRegisterComm(request);
    }

    @PostMapping("/isRegister")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000112, methodName = "isRegister", description = "判断账号是否注册", checkToken = false)
    public ResponseEntity<Response> isRegister(CheckRegisterRequest request) throws Exception {
        return isRegisterComm(request);
    }

    /**
     * 判断账号是否注册
     */
    private ResponseEntity<Response> isRegisterComm(CheckRegisterRequest request) throws Exception {
        String phone = request.getPhone();
        String userType = request.getUserType();
        UserRegisterVo userRegisterVo = new UserRegisterVo();
        if (STUDENT.name().equalsIgnoreCase(userType)) {
            Leads leads = leadsService.findLeadsByPhone(phone);
            if (leads != null) {
                userRegisterVo.setRegisterFlag(true);
                userRegisterVo.setUserName(leads.getName());
            } else {
                userRegisterVo.setRegisterFlag(false);
                userRegisterVo.setUserName("");
            }
        } else if (TEACHER.name().equalsIgnoreCase(userType)) {
            TcTeacher teacher = teacherService.findTeacherByPhone(phone);
            if (teacher != null) {
                userRegisterVo.setRegisterFlag(true);
                userRegisterVo.setUserName(teacher.getTcName());
            } else {
                userRegisterVo.setRegisterFlag(false);
                userRegisterVo.setUserName("");
            }
        } else if (PATRIARCH.name().equalsIgnoreCase(userType)) {
            Patriarch patriarch = patriarchService.findByphone(phone);
            if (patriarch != null) {
                userRegisterVo.setRegisterFlag(true);
                userRegisterVo.setUserName("");
            } else {
                userRegisterVo.setRegisterFlag(false);
                userRegisterVo.setUserName("");
            }
        }
        return ResponseEntity.ok(Response.success(userRegisterVo));
    }


    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000132, methodName = "retrievePasswordByJson", description = "找回密码", checkToken = false)
    @PostMapping("/retrievePasswordByJson")
    public ResponseEntity<Response> retrievePasswordByJson(@RequestBody RetrievePasswordRequest request) {
        return retrievePasswordComPhp(request);
    }

    @PostMapping("/retrievePassword")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000133, methodName = "retrievePassword", description = "找回密码", checkToken = false)
    public ResponseEntity<Response> retrievePassword(RetrievePasswordRequest request) {
        return retrievePasswordComPhp(request);
    }
    /**
     * 找回密码
     * 调用php
     * @param request
     * @return
     */

    private ResponseEntity<Response> retrievePasswordComPhp(RetrievePasswordRequest request) {
        LoginUserCache loginUserCache = new LoginUserCache();
        String phpResult =null;
        String phone = request.getPhone();
        String password = request.getPassword();
        String authCode = request.getAuthCode();
        String deviceType = request.getDeviceType();
        String userType = request.getUserType();
        String deviceId = request.getDeviceId();
        
        try{
            String phpUrl = url.concat("client/user/retrievePassword");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone",phone);
            paramMap.put("password",password);
            paramMap.put("authCode",authCode);
            paramMap.put("deviceType",deviceType);
            paramMap.put("userType",userType.toUpperCase());
            paramMap.put("deviceId",deviceId);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            LOGGER.info("找回密码传给php={}",JSON.toJSONString(paramMap));
            phpResult= HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("找回密码 phpResult={}",phpResult);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        Response phpResponse = JSON.parseObject(phpResult, Response.class);
        if (Objects.equals(CodeEnum.SUCCESS.getCode(),phpResponse.getCode())) {
            deviceType = deviceType.toUpperCase();
            userType = userType.toUpperCase();
            //String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);
            loginUserCache.setDeviceId(deviceId);
            loginUserCache.setDeviceType(deviceType);
            loginUserCache.setUserType(userType);
            //loginUserCache.setToken(phpToken);
            loginUserCache.setPassword(password);
            loginUserCache.setPhone(phone);
            String userUuid = loginUserCache.getUserUuid();
            loginUserCache.setAgoraUid(getAgoraUid(userUuid, userType));

        /*    String indexToken = getIndexToken(phone, userType);
            if (redisService.exists(indexToken)) {
                String cacheToken = redisService.get(indexToken);
                if (!phpToken.equals(cacheToken)) {
                    redisService.rename(cacheToken, phpToken);
                }
            }
            redisService.set(indexToken, phpToken);
            redisService.setLoginUserCache(phpToken, loginUserCache);*/
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 找回密码
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> retrievePasswordCom(RetrievePasswordRequest request) {
        String phone = request.getPhone();
        String password = request.getPassword();
        String authCode = request.getAuthCode();
        String deviceType = request.getDeviceType();
        String userType = request.getUserType();
        String deviceId = request.getDeviceId();

        String authCodeString = getAuthCode(phone);
        if (StringUtils.isBlank(authCodeString)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        if (!authCode.equals(authCodeString)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }

        Response response = null;
        LoginUserCache loginUserCache = new LoginUserCache();
        if (STUDENT.name().equalsIgnoreCase(userType)) {    //学生
            Leads leads = leadsService.findLeadsByPhone(phone);
            if (leads != null) {
                leads.setPassword(password);
                leadsService.update(leads);
                loginUserCache.setUserUuid(leads.getUuid());
                loginUserCache.setUserName(leads.getName());
                response = Response.success();
            } else {
                response = Response.error(INVALID_MOBILE);
            }
        } else if (TEACHER.name().equalsIgnoreCase(userType)) {  //老师
            TcTeacher tcTeacher = teacherService.findTeacherByPhone(phone);
            if (tcTeacher != null) {
                teacherService.updatePasswordByUuid(tcTeacher.getUuid(), password);
                loginUserCache.setUserUuid(tcTeacher.getUuid());
                loginUserCache.setUserName(tcTeacher.getTcName());
                response = Response.success();
            } else {
                response = Response.error(INVALID_ACCOUNT);
            }
        } else if (PATRIARCH.name().equalsIgnoreCase(userType)) {//家长
            Patriarch patriarch = patriarchService.findByphone(phone);
            if (patriarch != null) {
                patriarchService.updatePasswordByUuid(patriarch.getPatriarchUuid(), password);
                loginUserCache.setUserUuid(patriarch.getPatriarchUuid());
                loginUserCache.setUserName(patriarch.getPatriarchName());
                response = Response.success();
            } else {
                response = Response.error(INVALID_ACCOUNT);
            }
        }
        if (CodeEnum.SUCCESS.getCode() == response.getCode()) {
            deviceType = deviceType.toUpperCase();
            userType = userType.toUpperCase();
            String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);
            loginUserCache.setDeviceId(deviceId);
            loginUserCache.setDeviceType(deviceType);
            loginUserCache.setUserType(userType);
            loginUserCache.setToken(newToken);
            loginUserCache.setPassword(password);
            loginUserCache.setPhone(phone);
            String userUuid = loginUserCache.getUserUuid();
            loginUserCache.setAgoraUid(getAgoraUid(userUuid, userType));
            /*LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(userUuid);
            if (leadsExt != null && StringUtils.isNotBlank(leadsExt.getEasemobUuid())) {
                loginUserCache.setRegisterIMFlag(true);
            }*/
            UserEasemob userEasemob = userEasemobService.findByUserUuid(userUuid);
            if (userEasemob != null) {
                loginUserCache.setRegisterIMFlag(true);
            }
            String indexToken = getIndexToken(phone, userType);
            if (redisService.exists(indexToken)) {
                String cacheToken = redisService.get(indexToken);
                if (!newToken.equals(cacheToken)) {
                    redisService.rename(cacheToken, newToken);
                }
            }
            redisService.set(indexToken, newToken);
            redisService.setLoginUserCache(newToken, loginUserCache);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/iosUserFeedback")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000134, methodName = "iosUserFeedback", description = "用户反馈")
    public ResponseEntity<Response> iosUserFeedback(@RequestBody UserFeedbackRequest request) {
        return userFeedbackComPhp(request);
    }

    @PostMapping("/userFeedback")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000135, methodName = "userFeedback", description = "用户反馈")
    public ResponseEntity<Response> userFeedback(UserFeedbackRequest request) {
        return userFeedbackComPhp(request);
    }

    /**
     * 用户反馈
     * 调用php 原则测试阶段保留原逻辑且调用执行php逻辑，只是返回结果用php，保证数据不丢失
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> userFeedbackComPhp(UserFeedbackRequest request) {
        Response phpResponse=null;
        try{
            String phpUrl = url.concat("client/user/iosUserFeedback");
            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("content", request.getContent());
            paramMap.put("osName", request.getOsName());
            paramMap.put("version", request.getVersion());
            if(loginUserCache.getUserType()!=null){
                paramMap.put("userType", loginUserCache.getUserType().toUpperCase());
            }
            paramMap.put("userUuid", loginUserCache.getUserUuid());
            paramMap.put("phone", loginUserCache.getPhone());
            LOGGER.info("client/user/iosUserFeedback 传给php={}", JSON.toJSONString(paramMap));
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("userFeedbackComPhp={}", phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }

    /**
     * 用户反馈
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> userFeedbackCom(UserFeedbackRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String phone = loginUserCache.getPhone();
        String userUuid = loginUserCache.getUserUuid();
        String deviceType = loginUserCache.getDeviceType();
        String userType = loginUserCache.getUserType();
        String content = request.getContent();
        String osName = request.getOsName();
        if (osName != null) {
            osName = osName.toUpperCase();
        }
        String version = request.getVersion();
        UserFeedback userFeedback = new UserFeedback();
        userFeedback.setUserFeedbackUuid(UUIDUtil.randomUUID2());
        userFeedback.setUserUuid(userUuid);
        userFeedback.setPhone(phone);
        userFeedback.setContent(content);
        userFeedback.setUserType(userType);
        if (PC.name().equals(deviceType)) {
            userFeedback.setOsName(osName);
        } else {
            userFeedback.setOsName(deviceType);
        }
        userFeedback.setVersion(version);
        userFeedbackService.save(userFeedback);
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/iosUpdatePassword")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000136, methodName = "iosUpdatePassword", description = "修改密码")
    public ResponseEntity<Response> iosUpdatePassword(@RequestBody UpdatePasswordRequest request) {
        return updatePasswordCom(request);
    }

    @PostMapping("/updatePassword")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000137, methodName = "updatePassword", description = "修改密码")
    public ResponseEntity<Response> updatePassword(UpdatePasswordRequest request) {
        return updatePasswordCom(request);
    }

    /**
     * 修改密码接口
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> updatePasswordCom(UpdatePasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();
        String timestamp = request.getTimestamp();

        LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
        String userType = loginUserCache.getUserType();
        String phone = loginUserCache.getPhone();
        if (STUDENT.name().equals(userType)) {    //学生
            Leads leads = leadsService.findLeadsByPhone(phone);
            if (leads == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            if (!SecurityUtil.hashSha512Hex(leads.getPassword() + timestamp).equals(oldPassword)) {
                return ResponseEntity.ok(Response.error(INVALID_OLD_USERNAME_PWD));
            }
            leads.setPassword(newPassword);
            leadsService.update(leads);
        } else if (PATRIARCH.name().equals(userType)) { //家长
            Patriarch patriarch = patriarchService.findByphone(phone);
            if (patriarch == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            if (!SecurityUtil.hashSha512Hex(patriarch.getPatriarchPassword() + timestamp).equals(oldPassword)) {
                return ResponseEntity.ok(Response.error(INVALID_OLD_USERNAME_PWD));
            }
            patriarchService.updatePasswordByUuid(patriarch.getPatriarchUuid(), newPassword);
        } else {  //老师
            TcTeacher tcTeacher = teacherService.findTeacherByPhone(phone);
            if (tcTeacher == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            if (!SecurityUtil.hashSha512Hex(tcTeacher.getPassword() + timestamp).equals(oldPassword)) {
                return ResponseEntity.ok(Response.error(INVALID_OLD_USERNAME_PWD));
            }
            teacherService.updatePasswordByUuid(tcTeacher.getUuid(), newPassword);
        }
        String deviceId = loginUserCache.getDeviceId();
        String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);

        loginUserCache.setToken(newToken);
        loginUserCache.setPassword(newPassword);
        String indexToken = getIndexToken(phone, userType);
        if (redisService.exists(indexToken)) {
            String cacheToken = redisService.get(indexToken);
            if (!newToken.equals(cacheToken)) {
                redisService.rename(cacheToken, newToken);
            }
        }
        redisService.set(indexToken, newToken);
        redisService.setLoginUserCache(newToken, loginUserCache);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * pc学生客户端注册
     *
     * @param request
     * @return
     */
    @PostMapping("/pcStudentRegisterByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001149, methodName = "pcStudentRegisterByJson", description = "pc学生客户端注册", checkToken = false)
    public ResponseEntity<Response> pcStudentRegisterByJson(@RequestBody RegisterRequest request) {
        String registerSource = "pc";
        return registerCom(request, registerSource);
    }

    @PostMapping("/registerByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000138, methodName = "registerByJson", description = "用户注册", checkToken = false)
    public ResponseEntity<Response> registerByJson(@RequestBody RegisterRequest request) {
        return registerCom(request, null);
    }

    @PostMapping("/register")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000139, methodName = "register", description = "用户注册", checkToken = false)
    public ResponseEntity<Response> register(RegisterRequest request) {
        return registerCom(request, null);
    }

    /**
     * 用户注册
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> registerCom(RegisterRequest request, String registerSource) {
        String phone = request.getPhone();
        String authCodecache = getAuthCode(phone);
        if (StringUtils.isBlank(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        String authCode = request.getAuthCode();
        if (!authCode.equals(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }
        /*String channelUuid;
        if (StringUtils.isBlank(registerSource)) {
            channelUuid = getChannelUuid();
        } else {
            channelUuid = getPcChannelUuid();
        }*/
        String adid;
        if (StringUtils.isBlank(registerSource)) {
            adid = request.getAdid();
            if (StringUtils.isBlank(adid)) {
                adid = "IL1L1HTUVX";
            }
        } else {
            adid = "FO3QXNGZK8";
        }
        String channelUuid = getChannelUuid(adid);
        String userName = request.getUserName();

        Date date = new Date();
        LeadsLog leadsLog = new LeadsLog();
        leadsLog.setName(userName);
        leadsLog.setPhone(phone);
        leadsLog.setChannelUuid(channelUuid);
        leadsLog.setSignupDate(date);
        leadsLogService.save(leadsLog);

        Leads leads = leadsService.findLeadsByPhone(phone);
        if (leads != null) {
            return ResponseEntity.ok(Response.error(MOBILE_HAS_REGISTERED));
        }
        String password = request.getPassword();
        leads = new Leads();
        leads.setUuid(UUIDUtil.randomUUID2());
        leads.setPhone(phone);
        leads.setPassword(password);
        leads.setName(userName);
        leads.setChannelUuid(channelUuid);
        leads.setSignupDate(date);
        leadsService.save(leads);
        return ResponseEntity.ok(Response.success());
    }


    @PostMapping("/getAuthCodeByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000140, methodName = "getAuthCodeByJson", description = "获取验证码", checkToken = false)
    public ResponseEntity<Response> getAuthCodeByJson(@RequestBody PhoneRequest request) throws Exception {
        return getAuthCodeComNew(request);
    }

    @PostMapping("/getAuthCode")
    @RequestLimit(count = 5, time = 10000)
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000141, methodName = "getAuthCode", description = "获取验证码", checkToken = false)
    public ResponseEntity<Response> getAuthCode(PhoneRequest request) throws Exception {
        return getAuthCodeComNew(request);
    }

    /**
     * 获取验证码
     *
     * @param request
     * @return
     */
    @Deprecated
    private ResponseEntity<Response> getAuthCodeCom(PhoneRequest request) throws Exception {
        String phone = request.getPhone();
        String phoneAuthCodeKey = phone + "ClientAuthCode";
        String fourRandom = getFourRandom();
        int state;
        if ("17721432057".equals(phone)) {
            fourRandom = "8888";
            state = 0;
            LOGGER.info("**app..store..test**");
        } else {
            MessageSendTemplate messageSendTemplate = messageSendTemplateService.findByPurpose(MessageSendTemplateEnum.PURPOSE.APP.key);
            String messageContent = messageSendTemplate.getMessageContent();
            String content = messageContent.replace("#{authCode}", fourRandom);
            MessageReturnDto dto = messageService.sendMessage(phone, content);
            state = dto.getState();
            String stateValue = dto.getStateValue();
            //保存发送短信记录
            MessageSendRecord messageSendRecord = new MessageSendRecord();
            messageSendRecord.setMessageSendTemplateUuid(messageSendTemplate.getMessageSendTemplateUuid());
            messageSendRecord.setMessageContent(content);
            messageSendRecord.setSendPhone(phone);
            messageSendRecord.setSendStatus(state);
            messageSendRecord.setSendInfo(stateValue);
            messageSendRecordService.save(messageSendRecord);
        }
        if (state == -1) {
            return ResponseEntity.ok(Response.error(SERRVER_ERROR));
        } else if (state == 0) {    //发送成功
            AuthCodeVo authCodeVo = new AuthCodeVo();
            authCodeVo.setAuthCode(fourRandom);
            redisService.set(phoneAuthCodeKey, fourRandom, 60 * 5);
            return ResponseEntity.ok(Response.success(authCodeVo));
        } else {
            return ResponseEntity.ok(Response.error(SMS_SEND_FAIL));
        }
    }

    private ResponseEntity<Response> getAuthCodeComNew(PhoneRequest request) throws Exception {
        String phone = request.getPhone();
        String phoneAuthCodeKey = phone + "ClientAuthCode";
        String fourRandom = getFourRandom();
        if ("17721432057".equals(phone)) {
            fourRandom = "8888";
            LOGGER.info("**app..store..test**");
        }
        MessageSendTemplate messageSendTemplate = messageSendTemplateService.findByPurpose(MessageSendTemplateEnum.PURPOSE.APP.key);
        String messageContent = messageSendTemplate.getMessageContent();
        String content = messageContent.replace("#{authCode}", fourRandom);

        String smsSingleRequestServerUrl = "http://smsbj1.253.com/msg/send/json";
        SmsSendRequest smsSingleRequest = new SmsSendRequest("N2363083", "Haiketang_2016", signName + content, phone, report);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        //保存发送短信记录
        MessageSendRecord messageSendRecord = new MessageSendRecord();
        messageSendRecord.setMessageSendTemplateUuid(messageSendTemplate.getMessageSendTemplateUuid());
        messageSendRecord.setMessageContent(content);
        messageSendRecord.setSendPhone(phone);
        messageSendRecord.setSendStatus(Integer.parseInt(smsSingleResponse.getCode()));
        if (StringUtils.isEmpty(smsSingleResponse.getErrorMsg())) {
            messageSendRecord.setSendInfo("success");
        } else {
            messageSendRecord.setSendInfo(smsSingleResponse.getErrorMsg());
        }
        messageSendRecordService.save(messageSendRecord);

        if ("0".equals(smsSingleResponse.getCode())) {
            AuthCodeVo authCodeVo = new AuthCodeVo();
            authCodeVo.setAuthCode(fourRandom);
            redisService.set(phoneAuthCodeKey, fourRandom, 60 * 5);
            return ResponseEntity.ok(Response.success(authCodeVo));
        } else {
            return ResponseEntity.ok(Response.error(SMS_SEND_FAIL));
        }
    }
    /**
     * 获取验证码
     * 调用php
     * @param request
     * @return
     */
    private ResponseEntity<Response> getAuthCodeComNewPhp(PhoneRequest request) throws Exception {
        String phone = request.getPhone();
        String phoneAuthCodeKey = phone + "ClientAuthCode";
        String fourRandom = getFourRandom();
        if ("17721432057".equals(phone)) {
            fourRandom = "8888";
            LOGGER.info("**app..store..test**");
        }
        MessageSendTemplate messageSendTemplate = messageSendTemplateService.findByPurpose(MessageSendTemplateEnum.PURPOSE.APP.key);
        String messageContent = messageSendTemplate.getMessageContent();
        String content = messageContent.replace("#{authCode}", fourRandom);

        String smsSingleRequestServerUrl = "http://smsbj1.253.com/msg/send/json";
        SmsSendRequest smsSingleRequest = new SmsSendRequest("N2363083", "Haiketang_2016", signName + content, phone, report);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        //保存发送短信记录
        MessageSendRecord messageSendRecord = new MessageSendRecord();
        messageSendRecord.setMessageSendTemplateUuid(messageSendTemplate.getMessageSendTemplateUuid());
        messageSendRecord.setMessageContent(content);
        messageSendRecord.setSendPhone(phone);
        messageSendRecord.setSendStatus(Integer.parseInt(smsSingleResponse.getCode()));
        if (StringUtils.isEmpty(smsSingleResponse.getErrorMsg())) {
            messageSendRecord.setSendInfo("success");
        } else {
            messageSendRecord.setSendInfo(smsSingleResponse.getErrorMsg());
        }
        messageSendRecordService.save(messageSendRecord);

        if ("0".equals(smsSingleResponse.getCode())) {
            AuthCodeVo authCodeVo = new AuthCodeVo();
            authCodeVo.setAuthCode(fourRandom);
            redisService.set(phoneAuthCodeKey, fourRandom, 60 * 5);
            return ResponseEntity.ok(Response.success(authCodeVo));
        } else {
            return ResponseEntity.ok(Response.error(SMS_SEND_FAIL));
        }
    }
    @PostMapping("/authLogin")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000142, methodName = "authLogin", description = "验证码登录", checkToken = false)
    public ResponseEntity<Response> authLogin(AuthCodeLoginRequest request) {
        return authLoginCom(request);
    }

    @PostMapping("/authLoginByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000143, methodName = "authLoginByJson", description = "验证码登录", checkToken = false)
    public ResponseEntity<Response> authLoginByJson(@RequestBody AuthCodeLoginRequest request) {
        return authLoginCom(request);
    }

    /**
     * 验证码登录
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> authLoginCom(AuthCodeLoginRequest request) {
        String phone = request.getPhone();
        String authCodecache = getAuthCode(phone);
        if (StringUtils.isBlank(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        String authCode = request.getAuthCode();
        if (!authCode.equals(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }
        String userType = request.getUserType();
        String deviceId = request.getDeviceId();
        String deviceType = request.getDeviceType();
        userType = userType.toUpperCase();
        deviceType = deviceType.toUpperCase();
        String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);
        String indexToken = getIndexToken(phone, userType);
        String cacheToken = redisService.get(indexToken);
        LoginUserCache loginUserCache;
        if (!newToken.equals(cacheToken)) {
            //重置token和用户信息
            loginUserCache = new LoginUserCache();
            loginUserCache.setPhone(phone);
            loginUserCache.setDeviceId(deviceId);
            loginUserCache.setDeviceType(deviceType);
            loginUserCache.setUserType(userType);
            loginUserCache.setToken(newToken);

            if (STUDENT.name().equals(userType)) {    //学生
                Leads leads = leadsService.findLeadsByPhone(phone);
                if (leads == null) {
                    return ResponseEntity.ok(Response.error(INVALID_MOBILE));
                }
                String leadsUuid = leads.getUuid();
                /*LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leadsUuid);
                if (leadsExt != null && StringUtils.isNotBlank(leadsExt.getEasemobUuid())) {
                    loginUserCache.setRegisterIMFlag(true);
                }*/
                UserEasemob userEasemob = userEasemobService.findByUserUuid(leadsUuid);
                if (userEasemob != null) {
                    loginUserCache.setRegisterIMFlag(true);
                }
                loginUserCache.setUserName(leads.getName());
                loginUserCache.setUserUuid(leads.getUuid());
                loginUserCache.setPassword(leads.getPassword());
            } else if (PATRIARCH.name().equals(userType)) { //家长
                Patriarch patriarch = patriarchService.findByphone(phone);
                if (patriarch == null) {
                    return ResponseEntity.ok(Response.error(INVALID_MOBILE));
                }
                loginUserCache.setUserUuid(patriarch.getPatriarchUuid());
                loginUserCache.setPassword(patriarch.getPatriarchPassword());
            } else if (TEACHER.name().equals(userType)) {  //教师
                TcTeacher tcTeacher = teacherService.findTeacherByPhone(phone);
                if (tcTeacher == null) {
                    return ResponseEntity.ok(Response.error(INVALID_MOBILE));
                }
                loginUserCache.setUserUuid(tcTeacher.getUuid());
                loginUserCache.setUserName(tcTeacher.getTcName());
                loginUserCache.setPassword(tcTeacher.getPassword());
            } else {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            //登录成功
            loginUserCache.setAgoraUid(getAgoraUid(loginUserCache.getUserUuid(), userType));
            //key =phone+userType value =phone + deviceId + userType
            redisService.set(indexToken, newToken);
            if (StringUtils.isNotBlank(cacheToken)) {
                redisService.rename(cacheToken, newToken);
            }
            redisService.setLoginUserCache(newToken, loginUserCache);
        } else {
            //延长过期时间
            redisService.expire(cacheToken);
            loginUserCache = redisService.getLoginUserCache(cacheToken);
            if (loginUserCache.getAgoraUid() == 0) {    //处理已缓存账号
                loginUserCache.setAgoraUid(getAgoraUid(loginUserCache.getUserUuid(), userType));
            }
        }
        loginUserCache.setPassword("");
        return ResponseEntity.ok(Response.success(loginUserCache));
    }

    @PostMapping("/iosLogin")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000144, methodName = "iosLogin", description = "密码登录", checkToken = false)
    public ResponseEntity<Response> iosLogin(@RequestBody LoginRequest request) {
        return loginComPhp(request);
    }
    /**
     * PC端调
     * @param request
     * @return
     */
    @PostMapping("/login")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000145, methodName = "login", description = "密码登录", checkToken = false)
    public ResponseEntity<Response> login(LoginRequest request) {
        return loginComPhp(request);
    }

    /**
     * 密码登录
     * 调用php 原则所有请求检查是旧用户或新用户，旧用户查旧库，新用户查新库，直至旧库的学生等信息消耗完
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> loginComPhp(LoginRequest request) {
        LoginUserCache loginUserCache =null;
        try{
            String password = request.getPassword();
            String phone = request.getPhone();
            String deviceId = request.getDeviceId();
            String deviceType = request.getDeviceType().toUpperCase();
            String userType = request.getUserType().toUpperCase();
            //String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);
            String indexToken = getIndexToken(phone, userType);
            String cacheToken = redisService.get(indexToken);
            //用于注册云信用户，存入user_neteaseim表的user_uuid方便app使用
            String useNeteaseimToken = phone;
            String phpUrl =url.concat("client/user/iosLogin");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", phone);
            paramMap.put("password", password);
            paramMap.put("deviceType", deviceType);
            paramMap.put("userType", userType);
            paramMap.put("timestamp", request.getTimestamp());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Accept", "application/json");
            LOGGER.info("loginComPhpphp={}", JSON.toJSONString(paramMap));
            //如果指定x-www-form-urlencoded 可以直接传paramMap
            //如果指定json  paramMap必须转成json
            //JSONObject paramMapJson = JSONObject.parseObject(paramMap.toString());
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);

            LOGGER.info("loginComPhp result={}", phpResult);
            Response phpLoginResponse = JSON.parseObject(phpResult, Response.class);
            if(!Objects.equals(SUCCESS.getCode(),phpLoginResponse.getCode())){
                return ResponseEntity.ok(phpLoginResponse);
            }
            JSONObject obj=(JSONObject) phpLoginResponse.getData();
            Object phpToken=null;
            Object userName=null;
            Object userUuid=null;
            if(obj!=null){
                userName=obj.get("userName");
                userUuid=obj.get("userUuid");
                phpToken=obj.get("token");
            }

            //重置token和用户信息
            loginUserCache = new LoginUserCache();
            loginUserCache.setPhone(phone);
            loginUserCache.setDeviceId(deviceId);
            loginUserCache.setDeviceType(deviceType);
            loginUserCache.setUserType(userType);

            if(phpToken!=null){
                loginUserCache.setToken(phpToken.toString());
            }

            loginUserCache.setUseNeteaseimToken(useNeteaseimToken);
            if(userName!=null){
                loginUserCache.setUserName(userName.toString());
            }
            if(userUuid!=null){
                loginUserCache.setUserUuid(userUuid.toString());
                loginUserCache.setAgoraUid(getAgoraUid(userUuid.toString(), userType));
            /*    if(userType.equals("STUDENT")){
                    LOGGER.info("学生登陆后返回声网Id={}",getAgoraUid(userUuid.toString(), userType));
                }else if(userType.equals("TEACHER")){
                    LOGGER.info("老师登陆后返回声网Id={}",getAgoraUid(userUuid.toString(), userType));
                }*/
            }else {
                LOGGER.error("php返回userUuid为空导致生成agoraUid为空导致进入教室缺少参数异常.");
            }
            try{
                if(phpToken!=null) {
                    redisService.set(indexToken, phpToken.toString());
                    if (StringUtils.isNotBlank(cacheToken) && redisService.exists(cacheToken)) {
                        redisService.rename(cacheToken, phpToken.toString());
                    }
                    redisService.setLoginUserCache(phpToken.toString(), loginUserCache);
                }
            }catch (Exception e){
                LOGGER.error(e.getMessage());
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(loginUserCache));
    }
    /**
     * 密码登录
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> loginCom(LoginRequest request) {
        LOGGER.info("密码登录");
        String password = request.getPassword();
        String deviceId = request.getDeviceId();
        String phone = request.getPhone();
        String userType = request.getUserType().toUpperCase();
        String deviceType = request.getDeviceType().toUpperCase();
        String timestamp = request.getTimestamp();
        String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);
        //用于注册云信用户，存入user_neteaseim表的user_uuid方便app使用
        String useNeteaseimToken = phone;
        String indexToken = getIndexToken(phone, userType);
        String cacheToken = redisService.get(indexToken);
        String cahcePassword = "";
        /* 因官网修改密码后没有更新redis中密码，导致用官网修改后的新密码在app登录失败，此段代码注释，每次都与数据库中密码直接判断
        if (StringUtils.isNotBlank(cacheToken) && cacheToken.equals(newToken) && redisService.exists(cacheToken)) {    //token在缓存中存在且在该设备登录过
            LoginUserCache loginUserVo = redisService.getLoginUserCache(cacheToken);
            if (SecurityUtil.hashSha512Hex(loginUserVo.getPassword() + timestamp).equals(password)) {   //比较密码
                //通过
                redisService.expire(cacheToken);
                loginUserVo.setPassword("");
                response = new Response(false, CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), loginUserVo);
            } else {
                response = new Response(true, CodeEnum.INVALID_USERNAME_PWD.getCode(), CodeEnum.INVALID_USERNAME_PWD.getMsg());
            }
        } else {*/
        //重置token和用户信息
        LoginUserCache loginUserCache = new LoginUserCache();
        loginUserCache.setPhone(phone);
        loginUserCache.setDeviceId(deviceId);
        loginUserCache.setDeviceType(deviceType);
        loginUserCache.setUserType(userType);
        loginUserCache.setToken(newToken);
        loginUserCache.setUseNeteaseimToken(useNeteaseimToken);

        if (STUDENT.name().equalsIgnoreCase(userType)) {    //学生
            Leads leads = leadsService.findLeadsByPhone(request.getPhone());
            if (leads == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            cahcePassword = leads.getPassword();
            if (!SecurityUtil.hashSha512Hex(cahcePassword + timestamp).equals(password)) {
                return ResponseEntity.ok(Response.error(INVALID_USERNAME_PWD));
            }
            String leadsUuid = leads.getUuid();
            /*LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leadsUuid);
            if (leadsExt != null && StringUtils.isNotBlank(leadsExt.getEasemobUuid())) {
                loginUserCache.setRegisterIMFlag(true);
            }*/
            /*user_easemob用户环信表不再用，用user_neteaseim用户网易云信IM表判断是否注册过云信*/
            // UserEasemob userEasemob = userEasemobService.findByUserUuid(leadsUuid);
            UserNeteaseim userNeteaseim = userNeteaseimService.findByUserUuid(phone);
            if (userNeteaseim != null) {
                loginUserCache.setRegisterIMFlag(true);
            }
            loginUserCache.setUserName(leads.getName());
            loginUserCache.setUserUuid(leadsUuid);
        } else if (PATRIARCH.name().equalsIgnoreCase(userType)) { //家长
            Patriarch patriarch = patriarchService.findByphone(phone);
            if (patriarch == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            cahcePassword = patriarch.getPatriarchPassword();
            if (!SecurityUtil.hashSha512Hex(cahcePassword + timestamp).equals(password)) {
                return ResponseEntity.ok(Response.error(INVALID_USERNAME_PWD));
            }
            loginUserCache.setUserUuid(patriarch.getPatriarchUuid());
        } else {  //教师
            TcTeacher tcTeacher = teacherService.findTeacherByPhone(phone);
            if (tcTeacher == null) {
                return ResponseEntity.ok(Response.error(INVALID_MOBILE));
            }
            cahcePassword = tcTeacher.getPassword();
            if (!SecurityUtil.hashSha512Hex(cahcePassword + timestamp).equals(password)) {
                return ResponseEntity.ok(Response.error(INVALID_USERNAME_PWD));
            }
            loginUserCache.setUserName(tcTeacher.getTcName());
            loginUserCache.setUserUuid(tcTeacher.getUuid());

            String defaultPassword = SecurityUtil.hashSha512Hex(phone + "&" + "123456" + ":onlyhi");
            if (defaultPassword.equals(cahcePassword)) {
                loginUserCache.setIsFirst(true);
            }
        }

        //登录成功
        loginUserCache.setAgoraUid(getAgoraUid(loginUserCache.getUserUuid(), userType));
        redisService.set(indexToken, newToken);
        if (StringUtils.isNotBlank(cacheToken) && redisService.exists(cacheToken)) {
            redisService.rename(cacheToken, newToken);
        }
        loginUserCache.setPassword(cahcePassword);
        redisService.setLoginUserCache(newToken, loginUserCache);
        loginUserCache.setPassword("");
        return ResponseEntity.ok(Response.success(loginUserCache));
    }

    @PostMapping("/logout")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000146, methodName = "logout", description = "注销")
    public ResponseEntity<Response> logout(@RequestBody BaseRequest request) {
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/iosGetStudentInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000147, methodName = "iosGetStudentInfo", description = "获取学生个人信息")
    public ResponseEntity<Response> iosGetStudentInfo(@RequestBody BaseRequest request) {
        return getStudentInfoCom(request);
    }

    @PostMapping("/getStudentInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000148, methodName = "getStudentInfo", description = "获取学生个人信息")
    public ResponseEntity<Response> getStudentInfo(BaseRequest request) {
        return getStudentInfoCom(request);
    }

    /**
     * 获取学生个人信息
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> getStudentInfoComPhp(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        Leads leads = leadsService.findLeadsByPhone(loginUserCache.getPhone());
        LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leads.getUuid());
        StudentVo studentVo = TransferUtil.transfer(leads, StudentVo.class);
        if (leadsExt != null) {
            studentVo.setIconurl(leadsExt.getIconurl());
        }
        return ResponseEntity.ok(Response.success(studentVo));
    }
    /**
     * 调用php
     * 获取学生个人信息
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> getStudentInfoCom(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        Leads leads = leadsService.findLeadsByPhone(loginUserCache.getPhone());
        LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leads.getUuid());
        StudentVo studentVo = TransferUtil.transfer(leads, StudentVo.class);
        if (leadsExt != null) {
            studentVo.setIconurl(leadsExt.getIconurl());
        }
        return ResponseEntity.ok(Response.success(studentVo));
    }
    /**
     * 获取教师信息
     *
     * @param request
     * @return
     */
    @PostMapping("/getTeacherInfoByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000149, methodName = "getTeacherInfoByJson", description = "获取教师信息", userTypes = TEACHER)
    public ResponseEntity<Response> getTeacherInfoByJson(@RequestBody BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        TcTeacher teacher = teacherService.findTeacherByPhone(loginUserCache.getPhone());
        teacher.setPassword("");
        return ResponseEntity.ok(Response.success(teacher));
    }

    //@PostMapping("/saveDeviceInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001181, methodName = "saveDeviceInfo", description = "保存设备信息")
    public ResponseEntity<Response> saveDeviceInfo(SaveDeviceInfoRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        LOGGER.info("saveDeviceInfo={}",request.getToken());
        UserDeviceInformation userDeviceInformation = TransferUtil.transfer(request, UserDeviceInformation.class);
        userDeviceInformation.setDeviceType(loginUserCache.getDeviceType());
        String userType = loginUserCache.getUserType();
        if (STUDENT.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 0);
        } else if (TEACHER.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 1);
        } else if (PATRIARCH.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 2);
        } else if (CC.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 3);
        } else if (CR.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 4);
        } else if (TA.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 5);
        } else if (MONITOR.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 6);
        } else if (QC.name().equals(userType)) {
            userDeviceInformation.setUserType((byte) 7);
        } else {
            userDeviceInformation.setUserType((byte) -1);
        }
        String loginName = loginUserCache.getLoginName();
        if (StringUtils.isBlank(loginName)) {
            loginName = loginUserCache.getPhone();
        }
        userDeviceInformation.setLoginAccount(loginName);
        userDeviceInformation.setCreateUserId(loginUserCache.getUserUuid());
        userDeviceInformation.setCreateDate(new Date());
        userDeviceInformationService.save(userDeviceInformation);
        return ResponseEntity.ok(Response.success());
    }

}
