package cn.onlyhi.client.controller;

import cn.onlyhi.client.config.NeteaseConfig;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.UserNeteaseim;
import cn.onlyhi.client.response.NeteaseResponse;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.service.UserNeteaseimService;
import cn.onlyhi.client.vo.NeteaseRegisterVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.CheckSumBuilder;
import cn.onlyhi.common.util.HttpUtil;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.StringUtil;
import cn.onlyhi.common.util.UUIDUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cn.onlyhi.common.enums.ClientEnum.UserType.PATRIARCH;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;

/**
 * 网易云信IM接口
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/3.
 */
@RestController
@RequestMapping("/client/netease")
public class UserNeteaseimController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserNeteaseimController.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserNeteaseimService userNeteaseimService;
    @Autowired
    private NeteaseConfig neteaseConfig;

    /**
     * 用户注册网易云信
     *
     * @param request
     * @return
     */
    @PostMapping("/register")
    @LogRecordAnnotation(moduleCode = 100003, moduleName = "网易云信IM接口", methodCode = 1000031, methodName = "register", description = "用户注册网易云信")
    public ResponseEntity<Response> register(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String phone=loginUserCache.getUseNeteaseimToken();//当前登录用户的手机号
        UserNeteaseim neteaseim = userNeteaseimService.findByUserUuid(userUuid);
        NeteaseRegisterVo vo = new NeteaseRegisterVo();
       if (neteaseim != null) {
            vo.setNeteaseToken(neteaseim.getNeteaseToken());
            vo.setNeteaseAccid(neteaseim.getNeteaseAccid());
            return ResponseEntity.ok(Response.success(vo));
        }

        String userType = loginUserCache.getUserType();
        String userName = loginUserCache.getUserName();
        String url = "https://api.netease.im/nimserver/user/create.action";
        Map<String, String> paramMap = new HashMap<>();
        String accid = null;
        if (STUDENT.name().equals(userType) || TEACHER.name().equals(userType) || PATRIARCH.name().equals(userType)) {
            accid = loginUserCache.getPhone();
        } else {
            accid = loginUserCache.getLoginName();
        }
        paramMap.put("accid", accid);
        paramMap.put("name", userName);
        Map<String, String> headerMap = new HashMap<>();
        String appKey = neteaseConfig.getAppKey();
        String appSecret = neteaseConfig.getAppSecret();
        String nonce = StringUtil.getRandomString(128);
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);
        headerMap.put("AppKey", appKey);
        headerMap.put("Nonce", nonce);
        headerMap.put("CurTime", curTime);
        headerMap.put("CheckSum", checkSum);
        headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        String neteaseResult = HttpUtil.sendPost(url, paramMap, headerMap);
        LOGGER.info("neteaseResult={}", neteaseResult);
        NeteaseResponse neteaseResponse = JSON.parseObject(neteaseResult, NeteaseResponse.class);
        if (neteaseResponse.getCode() != 200) {
            return ResponseEntity.ok(Response.errorCustom(neteaseResponse.getDesc()));
        }
        String neteaseToken = neteaseResponse.getInfo().getToken();
        UserNeteaseim userNeteaseim = new UserNeteaseim();
        userNeteaseim.setUserNeteaseimUuid(UUIDUtil.randomUUID2());
        //userNeteaseim.setUserUuid(userUuid);
        userNeteaseim.setUserUuid(phone);
        userNeteaseim.setUserType(userType);
        userNeteaseim.setNeteaseAccid(accid);
        userNeteaseim.setNeteaseName(userName);
        userNeteaseim.setNeteaseToken(neteaseToken);
        userNeteaseim.setCreateUid(userUuid);
        userNeteaseimService.save(userNeteaseim);

        vo.setNeteaseToken(neteaseToken);
        vo.setNeteaseAccid(accid);
        return ResponseEntity.ok(Response.success(vo));
    }
    /**
     * 更新并获取新token
     * 1.webserver更新网易云通信ID的token，同时返回新的token；
     2.一般用于网易云通信ID修改密码，找回密码或者第三方有需求获取新的token。
     * @param request
     * @return
     */
    @PostMapping("/updateNeteaseimToken")
    @LogRecordAnnotation(moduleCode = 100003, moduleName = "网易云信IM接口", methodCode = 1000032, methodName = "register", description = "网易云信更新并获取新token")
    public ResponseEntity<Response> updateNeteaseimToken(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String phone=loginUserCache.getUseNeteaseimToken();//当前登录用户的手机号
        UserNeteaseim neteaseim = userNeteaseimService.findByUserUuid(phone);
        NeteaseRegisterVo vo = new NeteaseRegisterVo();
        if (neteaseim == null) {
            return ResponseEntity.ok(Response.error(CodeEnum.IMUSER_NO_EXIST));
        }else{
            String userType = loginUserCache.getUserType();
            String url = "https://api.netease.im/nimserver/user/refreshToken.action";
            Map<String, String> paramMap = new HashMap<>();
            String accid = null;
            if (STUDENT.name().equals(userType) || TEACHER.name().equals(userType) || PATRIARCH.name().equals(userType)) {
                accid = loginUserCache.getPhone();
            } else {
                accid = loginUserCache.getLoginName();
            }
            paramMap.put("accid", accid);

            Map<String, String> headerMap = new HashMap<>();
            String appKey = neteaseConfig.getAppKey();
            String appSecret = neteaseConfig.getAppSecret();
            String nonce = StringUtil.getRandomString(128);
            String curTime = String.valueOf((new Date()).getTime() / 1000L);
            String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);

            headerMap.put("AppKey", appKey);
            headerMap.put("Nonce", nonce);
            headerMap.put("CurTime", curTime);
            headerMap.put("CheckSum", checkSum);
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            String neteaseResult = HttpUtil.sendPost(url, paramMap, headerMap);
            NeteaseResponse neteaseResponse = JSON.parseObject(neteaseResult, NeteaseResponse.class);
            if (neteaseResponse.getCode() != 200) {
                return ResponseEntity.ok(Response.errorCustom(neteaseResponse.getDesc()));
            }
            String neteaseToken = neteaseResponse.getInfo().getToken();
            UserNeteaseim userNeteaseim = new UserNeteaseim();
            userNeteaseim.setUserUuid(phone);
            userNeteaseim.setNeteaseToken(neteaseToken);
            userNeteaseim.setUpdateUid(userUuid);
            userNeteaseimService.updateByUserUuid(userNeteaseim);

            vo.setNeteaseToken(neteaseToken);
            vo.setNeteaseAccid(accid);//当前phone
            return ResponseEntity.ok(Response.success(vo));
        }
    }
}
