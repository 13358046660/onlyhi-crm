package cn.onlyhi.client.controller;

import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.User;
import cn.onlyhi.client.request.CourseRecordRequest;
import cn.onlyhi.client.request.EmpLoginRequest;
import cn.onlyhi.client.service.CpCourseService;
import cn.onlyhi.client.service.RoleService;
import cn.onlyhi.client.service.UserService;
import cn.onlyhi.client.vo.CourseVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.request.PageRequest;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;

import static cn.onlyhi.common.constants.Constants.ROLE_CC;
import static cn.onlyhi.common.constants.Constants.ROLE_CR;
import static cn.onlyhi.common.constants.Constants.ROLE_QC;
import static cn.onlyhi.common.constants.Constants.ROLE_QCZY;
import static cn.onlyhi.common.constants.Constants.ROLE_TA;
import static cn.onlyhi.common.constants.Constants.ROLE_TS;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.MONITOR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.QC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TA;
import static cn.onlyhi.common.enums.CodeEnum.*;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/11/2.
 */
@RestController
@RequestMapping("/client/emp")
public class ClientEmpController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientEmpController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private RoleService roleService;
    @Value("${phpStaging.url}")
    private String url;
    @PostMapping("/loginByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001157, methodName = "loginByJson", description = "员工密码登录(cc/cr/监课)", checkToken = false)
    public ResponseEntity<Response> loginByJson(@RequestBody EmpLoginRequest request) {
        return loginComPhp(request);
    }

    @PostMapping("/login")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001158, methodName = "login", description = "员工密码登录(cc/cr/监课)", checkToken = false)
    public ResponseEntity<Response> login(EmpLoginRequest request) {
        return loginComPhp(request);
    }


    /**
     * 员工密码登录(cc/cr/监课)
     * 调用php
     * @param request
     * @return
     */
    private ResponseEntity<Response> loginComPhp(EmpLoginRequest request) {

        LoginUserCache loginUserCache = new LoginUserCache();
        Response phpResponse =null;
        String phpUrl = url.concat("client/emp/loginByJson");
        String loginName=request.getLoginName();
        String password=request.getPassword();
        String deviceType = request.getDeviceType().toUpperCase();
        String userType = request.getUserType().toUpperCase();
        String timestamp=request.getTimestamp();
        String deviceId=request.getDeviceId();
        String token=request.getToken();
        try{
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(token));
            post.setHeader("Accept", "application/json");

            JSONObject param= new JSONObject();
            param.put("loginName", loginName);
            param.put("password", password);
            param.put("deviceType", deviceType);
            param.put("userType", userType);
            param.put("timestamp", timestamp);
            param.put("deviceId", deviceId);
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            LOGGER.info("员工密码登录(cc/cr/监课) 传给php={}",param.toString());
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("员工密码登录(cc/cr/监课) 返回空");
            }

            phpResponse = JSON.parseObject(result, Response.class);
            if(!Objects.equals(SUCCESS.getCode(),phpResponse.getCode())){
                return ResponseEntity.ok(phpResponse);
            }
            Object object=phpResponse.getData();
            loginUserCache =JSON.parseObject(object.toString(),LoginUserCache.class);
            //LOGGER.info("loginUserCache={}", JSON.toJSONString(loginUserCache));
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        try{

            if (!ClientEnum.UserType.getEnumKeyList().contains(userType)) {
                return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
            }
            if (loginUserCache == null) {
                return ResponseEntity.ok(Response.error(NO_USER));
            }
     /*   List<String> roleAliasList = roleService.findByUserUuid(user.getUuid());
        if (roleAliasList == null) {
            return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
        }
        //教学监课
        if (MONITOR.name().equals(userType)) {
            if (!roleAliasList.contains(ROLE_TS)) {
                return ResponseEntity.ok(Response.error(NO_MONITOR));
            }
        }
        //质检监课
        if (QC.name().equals(userType)) {
            if (!roleAliasList.contains(ROLE_QC) && !roleAliasList.contains(ROLE_QCZY)) {
                return ResponseEntity.ok(Response.error(NO_MONITOR));
            }
        }
        //cc
        if (CC.name().equals(userType)) {
            if (!roleAliasList.contains(ROLE_CC)) {
                return ResponseEntity.ok(Response.error(NO_CC));
            }
        }
        //cr
        if (CR.name().equals(userType)) {
            if (!roleAliasList.contains(ROLE_CR)) {
                return ResponseEntity.ok(Response.error(NO_CR));
            }
        }
        //技术支持
        if (TA.name().equals(userType)) {
            if (!roleAliasList.contains(ROLE_TA)) {
                return ResponseEntity.ok(Response.error(NO_TA));
            }
        }*/
   /*     String cahcePassword = user.getPassword();
        if (!SecurityUtil.hashSha512Hex(cahcePassword + timestamp).equals(password)) {
            return ResponseEntity.ok(Response.error(INVALID_USERNAME_PWD));
        }*/
            //String newToken = SecurityUtil.hashMD5Hex(loginName + deviceId + userType);
            //重置token和用户信息
            loginUserCache.setLoginName(loginName);
            loginUserCache.setDeviceId(deviceId);
            loginUserCache.setDeviceType(deviceType);
            loginUserCache.setUserType(userType);
            loginUserCache.setToken(loginUserCache.getToken());
            loginUserCache.setUserName(loginUserCache.getUserName());
            loginUserCache.setUserUuid(loginUserCache.getUserUuid());
            loginUserCache.setPhone(loginUserCache.getPhone());

            String indexToken = getIndexToken(loginName, userType);
            String cacheToken = redisService.get(indexToken);
            loginUserCache.setAgoraUid(getAgoraUid(loginUserCache.getUserUuid(), userType));
            redisService.set(indexToken, loginUserCache.getToken());
            if (StringUtils.isNotBlank(cacheToken) && redisService.exists(cacheToken)) {
                redisService.rename(cacheToken, loginUserCache.getToken());
            }
            //密码正确校验在php做，如果java需要从缓存取密码，下面设置并让php返回从表中取的密码
            //loginUserCache.setPassword(cahcePassword);
            redisService.setLoginUserCache(loginUserCache.getToken(), loginUserCache);
            //loginUserCache.setPassword("");
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        //LOGGER.info("loginUserCache={}",JSON.toJSONString(loginUserCache));
        return ResponseEntity.ok(Response.success(loginUserCache));
    }

    /**
     * 员工密码登录(cc/cr/监课)
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> loginCom(EmpLoginRequest request) {
        String userType = request.getUserType().toUpperCase();
        if (!ClientEnum.UserType.getEnumKeyList().contains(userType)) {
            return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
        }
        String loginName = request.getLoginName();
        User user = userService.findByLoginName(loginName);
        if (user == null) {
            return ResponseEntity.ok(Response.error(NO_USER));
        }
        List<String> roleAliasList = roleService.findByUserUuid(user.getUuid());
        if (roleAliasList == null) {
            return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
        }
        if (MONITOR.name().equals(userType)) { //教学监课
            if (!roleAliasList.contains(ROLE_TS)) {
                return ResponseEntity.ok(Response.error(NO_MONITOR));
            }
        }
        if (QC.name().equals(userType)) { //质检监课
            if (!roleAliasList.contains(ROLE_QC) && !roleAliasList.contains(ROLE_QCZY)) {
                return ResponseEntity.ok(Response.error(NO_MONITOR));
            }
        }
        if (CC.name().equals(userType)) { //cc
            if (!roleAliasList.contains(ROLE_CC)) {
                return ResponseEntity.ok(Response.error(NO_CC));
            }
        }
        if (CR.name().equals(userType)) { //cr
            if (!roleAliasList.contains(ROLE_CR)) {
                return ResponseEntity.ok(Response.error(NO_CR));
            }
        }
        if (TA.name().equals(userType)) { //技术支持
            if (!roleAliasList.contains(ROLE_TA)) {
                return ResponseEntity.ok(Response.error(NO_TA));
            }
        }
        String password = request.getPassword();
        String timestamp = request.getTimestamp();
        String cahcePassword = user.getPassword();
        if (!SecurityUtil.hashSha512Hex(cahcePassword + timestamp).equals(password)) {
            return ResponseEntity.ok(Response.error(INVALID_USERNAME_PWD));
        }
        String deviceId = request.getDeviceId();
        String deviceType = request.getDeviceType().toUpperCase();
        String newToken = SecurityUtil.hashMD5Hex(loginName + deviceId + userType);
        //重置token和用户信息
        LoginUserCache loginUserCache = new LoginUserCache();
        loginUserCache.setLoginName(loginName);
        loginUserCache.setDeviceId(deviceId);
        loginUserCache.setDeviceType(deviceType);
        loginUserCache.setUserType(userType);
        loginUserCache.setToken(newToken);
        loginUserCache.setUserName(user.getName());
        loginUserCache.setUserUuid(user.getUuid());
        loginUserCache.setPhone(user.getPhone());

        String indexToken = getIndexToken(loginName, userType);
        String cacheToken = redisService.get(indexToken);
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

    /**
     * cc、cr、监课上课列表
     *
     * @param request
     * @return
     */
    //@GetMapping("/getNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001170, methodName = "getNoEndCourseList", description = "cc、cr、监课上课列表")
    public ResponseEntity<Response> getNoEndCourseList(CourseRecordRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        Integer courseType = request.getCourseType();
        String userName = request.getUserName();
        String userUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        List<CourseDto> courseDtoTmpList = new ArrayList<>();
        if (CC.name().equals(userType)) {
            courseDtoTmpList = cpCourseService.findNoEndCourseByCC(userUuid, subject, startDate, endDate, courseType, userName, 1, Integer.MAX_VALUE);
        } else if (CR.name().equals(userType)) {
            courseDtoTmpList = cpCourseService.findNoEndCourseByCR(userUuid, subject, startDate, endDate, courseType, userName, 1, Integer.MAX_VALUE);
        } else if (QC.name().equals(userType)) {
            courseDtoTmpList = cpCourseService.findNoEndCourseByQCMonitor(subject, startDate, endDate, courseType, userName, 1, Integer.MAX_VALUE);
        } else if (MONITOR.name().equals(userType)){  //教学监课：测评课和正式课
            courseDtoTmpList = cpCourseService.findNoEndCourse(subject, startDate, endDate, courseType, userName, 1, Integer.MAX_VALUE);
        }else{
            return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
        }
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false,userType);
        int totalCount = (int) objects[0];
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        List<CourseVo> voList = TransferUtil.transfer(courseDtoList, CourseVo.class);
        Page<CourseVo> page = new Page<>(totalCount, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * cc、cr、监课上课列表
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/getNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001170, methodName = "getNoEndCourseListPhp", description = "cc、cr、监课上课列表")
    public ResponseEntity<Response> getNoEndCourseListPhp(CourseRecordRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        Integer courseType = request.getCourseType();
        String userName = request.getUserName();
        String userUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String phpToken = loginUserCache.getToken();

        Response phpResponse=null;
        try {
            String phpUrl = url.concat("client/emp/getNoEndCourseList");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(phpToken));
            post.setHeader("Accept", "application/json");

            JSONObject param= new JSONObject();
            param.put("userUuid", userUuid);
            param.put("userType", userType);
            param.put("subject", subject);
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            param.put("courseType", courseType);
            param.put("userName", userName);
            param.put("pageNo", pageNo);
            param.put("pageSize", pageSize);

            LOGGER.info("cc、cr、监课旁听课程列表，传给php={}",JSON.toJSONString(param));
            if(param!=null){
                StringEntity stringEntity = new StringEntity(param.toString(),Charset.forName("UTF-8"));
                post.setEntity(stringEntity);
            }

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("cc、cr、监课旁听课程列表php返回空");
            }
            phpResponse = JSON.parseObject(result, Response.class);

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }

    //@GetMapping("/getNoEndDebugCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001171, methodName = "getNoEndDebugCourseList", description = "cc、cr调试课列表")
    public ResponseEntity<Response> getNoEndDebugCourseList(PageRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        int totalCount = 0;
        List<CourseDto> courseDtoList;
        String userUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        if (CC.name().equals(userType)) {
            totalCount = cpCourseService.countNoEndDebugCourseByCC(userUuid);
            courseDtoList = cpCourseService.findNoEndDebugCourseByCC(userUuid, pageNo, pageSize);
        } else if (CR.name().equals(userType)) {
            totalCount = cpCourseService.countNoEndDebugCourseByCR(userUuid);
            courseDtoList = cpCourseService.findNoEndDebugCourseByCR(userUuid, pageNo, pageSize);
        } else {
            return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
        }
        List<CourseVo> voList = TransferUtil.transfer(courseDtoList, CourseVo.class);
        Page<CourseVo> page = new Page<>(totalCount, voList);
        return ResponseEntity.ok(Response.success(page));
    }
    // 调用php
    @GetMapping("/getNoEndDebugCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001171, methodName = "getNoEndDebugCourseList", description = "cc、cr调试课列表")
    public ResponseEntity<Response> getNoEndDebugCourseListPhp(PageRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();

        Response phpResponse=null;
        try {
            String phpUrl = url.concat("client/emp/getNoEndDebugCourseList");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            post.setHeader("Accept", "application/json");

            JSONObject param= new JSONObject();
            param.put("pageNo", pageNo);
            param.put("pageSize", pageSize);

            if(param!=null){
                StringEntity stringEntity = new StringEntity(param.toString());
                post.setEntity(stringEntity);
            }
            LOGGER.info("监课待上课程列表client/emp/getNoEndDebugCourseList传php={}",JSON.toJSONString(param));

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("cc、cr、监课待上课程列表 返回空");
            }

            phpResponse = JSON.parseObject(result, Response.class);

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
}
