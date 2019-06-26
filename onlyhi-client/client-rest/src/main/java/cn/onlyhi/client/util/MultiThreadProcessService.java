package cn.onlyhi.client.util;

import cn.onlyhi.client.controller.BaseController;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.Patriarch;
import cn.onlyhi.client.po.TcTeacher;
import cn.onlyhi.client.po.UserNeteaseim;
import cn.onlyhi.client.request.LoginRequest;
import cn.onlyhi.client.service.PatriarchService;
import cn.onlyhi.client.service.TcTeacherService;
import cn.onlyhi.client.service.UserNeteaseimService;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static cn.onlyhi.common.enums.ClientEnum.UserType.PATRIARCH;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.CodeEnum.INVALID_MOBILE;
import static cn.onlyhi.common.enums.CodeEnum.INVALID_USERNAME_PWD;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;

@Service
public class MultiThreadProcessService extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    private UserNeteaseimService userNeteaseimService;
    @Autowired
    private PatriarchService patriarchService;
    @Autowired
    private TcTeacherService teacherService;

    /**
     * 默认处理流程耗时1000ms
     */
    public ResponseEntity<Response> processSomething(LoginRequest request) {
        LOGGER.debug("MultiThreadProcessService-processSomething" + Thread.currentThread() + "......start");
        try {
            Thread.sleep(1000);
            String password = request.getPassword();
            String deviceId = request.getDeviceId();
            String phone = request.getPhone();
            String userType = request.getUserType().toUpperCase();
            String deviceType = request.getDeviceType().toUpperCase();
            String timestamp = request.getTimestamp();
            String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);
            String useNeteaseimToken = phone;//用于注册云信用户，存入user_neteaseim表的user_uuid方便app使用
            String indexToken = getIndexToken(phone, userType);
            String cacheToken = redisService.get(indexToken);
            String cahcePassword = "";

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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("MultiThreadProcessService-processSomething" + Thread.currentThread() + "......end");
        return ResponseEntity.ok(Response.success());
    }
}