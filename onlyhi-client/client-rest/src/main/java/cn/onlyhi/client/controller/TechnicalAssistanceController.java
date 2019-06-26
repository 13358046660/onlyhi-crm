package cn.onlyhi.client.controller;

import cn.onlyhi.client.config.YmlMyConfig;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.TechnicalAssistance;
import cn.onlyhi.client.po.User;
import cn.onlyhi.client.request.ApplyAssistanceRequest;
import cn.onlyhi.client.request.TechnicalAssistanceUuidRequest;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.service.TechnicalAssistanceService;
import cn.onlyhi.client.service.UserService;
import cn.onlyhi.client.vo.AssistanceStatusVo;
import cn.onlyhi.client.vo.TechnicalAssistanceVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.constants.Constants;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.HttpUtil;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.TransferUtil;
import cn.onlyhi.common.util.UUIDUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/2.
 */
@RestController
@RequestMapping("/client/ta")
public class TechnicalAssistanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalAssistanceController.class);
    private static ConcurrentLinkedQueue<String> taQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    private RedisService redisService;
    @Autowired
    private TechnicalAssistanceService technicalAssistanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private YmlMyConfig ymlMyConfig;

    /**
     * 请求技术支持
     *
     * @param request
     * @return
     */
    @PostMapping("/applyAssistance")
    @LogRecordAnnotation(moduleCode = 100002, moduleName = "技术支持", methodCode = 1000021, methodName = "applyAssistance", description = "请求技术支持")
    public ResponseEntity<Response> applyAssistance(ApplyAssistanceRequest request) {
        String token = request.getToken();
        LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
        String userUuid = loginUserCache.getUserUuid();
        String userName = loginUserCache.getUserName();
        String userType = loginUserCache.getUserType();
        String assistanceId = request.getAssistanceId().replaceAll("\\s*", "");
        String assistancePwd = request.getAssistancePwd();
        String issue = request.getIssue();
        TechnicalAssistance technicalAssistance = new TechnicalAssistance();
        String technicalAssistanceUuid = UUIDUtil.randomUUID2();
        technicalAssistance.setTechnicalAssistanceUuid(technicalAssistanceUuid);
        technicalAssistance.setUserUuid(userUuid);
        technicalAssistance.setUserName(userName);
        technicalAssistance.setUserType(userType);
        technicalAssistance.setUserAssistanceId(assistanceId);
        technicalAssistance.setUserAssistancePwd(assistancePwd);
        technicalAssistance.setIssue(issue);
        technicalAssistance.setCreateUid(userUuid);
        Date createTime = new Date();
        technicalAssistance.setCreateTime(createTime);
        technicalAssistanceService.save(technicalAssistance);
        //匹配技术支持 给技术支持发送请求信息
            String alias = "ta";
            List<User> userList = userService.findByRoleAlias(alias);
            if (userList != null && userList.size() > 0) {
                List<String> loginNameList = new ArrayList<String>();
                for (User user : userList) {
                    loginNameList.add(user.getLoginName());
                }
                String loginName = matchTechnical(loginNameList);
                String uuid = userService.findByLoginName(loginName).getUuid();
                technicalAssistance.setAssistanceUuid(uuid);
                int i = technicalAssistanceService.update(technicalAssistance);
                if (i > 0) {
                    //通知前端
                    String websocketUrl = ymlMyConfig.getWebsocketUrl();
                    Map<String, String> paramMap = new HashMap<>();
                    String taToken = redisService.get(loginName + alias.toUpperCase());
                    if (taToken != null) {
                        paramMap.put("token", taToken);
                        paramMap.put("type", "2");
                        TechnicalAssistanceVo vo = new TechnicalAssistanceVo();
                        vo.setTechnicalAssistanceUuid(technicalAssistanceUuid);
                        vo.setUserName(userName);
                        vo.setUserAssistanceId(assistanceId);
                        vo.setUserAssistancePwd(assistancePwd);
                        vo.setIssue(issue);
                        vo.setCreateTime(createTime);
                        vo.setAssistanceStatus(1);
                        paramMap.put("message", JSON.toJSONString(vo));
                        String result = HttpUtil.sendPost(websocketUrl, paramMap);
                        LOGGER.info("result of websocket = {}", result);
                    } else {
                        LOGGER.info("技术支持:{},token={}已失效", loginName, taToken);
                    }
                }
            } else {
                LOGGER.info("没有技术支持！");
            }

        return ResponseEntity.ok(Response.success(technicalAssistanceUuid));
    }

    /**
     * 匹配合适的技术支持并返回登录名
     *
     * @param loginNameList
     * @return
     */
    private String matchTechnical(List<String> loginNameList) {
        if (loginNameList == null || loginNameList.size() == 0) {
            return null;
        }
        if (taQueue.size() == 0) {
            taQueue.addAll(loginNameList);
        } else {
            if (!loginNameList.containsAll(taQueue) || !taQueue.containsAll(loginNameList)) {
                //有新增，加入taQueue
                loginNameList.stream().forEach(loginName -> {
                    if (!taQueue.contains(loginName)) {
                        taQueue.add(loginName);
                    }
                });
                //有删除，从taQueue删除
                taQueue.stream().forEach(loginName -> {
                    if (!loginNameList.contains(loginName)) {
                        taQueue.remove(loginName);
                    }
                });
            }
        }
        //轮询获取ta
        String loginName = taQueue.poll();
        taQueue.add(loginName);
        //记录轮询到的ta次数
        redisService.zincrby(Constants.TASET, 1, loginName);
        return loginName;
    }

    /**
     * 撤销申请
     *
     * @param request
     * @return
     */
    @PostMapping("/applyRepeal")
    @LogRecordAnnotation(moduleCode = 100002, moduleName = "技术支持", methodCode = 1000022, methodName = "applyRepeal", description = "撤销申请")
    public ResponseEntity<Response> applyRepeal(TechnicalAssistanceUuidRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String technicalAssistanceUuid = request.getTechnicalAssistanceUuid();
        TechnicalAssistance technicalAssistance = new TechnicalAssistance();
        technicalAssistance.setTechnicalAssistanceUuid(technicalAssistanceUuid);
        technicalAssistance.setAssistanceStatus(2);
        technicalAssistance.setUpdateUid(userUuid);
        int i = technicalAssistanceService.update(technicalAssistance);
        if (i > 0) {
            //通知前端
            TechnicalAssistance assistance = technicalAssistanceService.findByUuid(technicalAssistanceUuid);
            String assistanceUuid = assistance.getAssistanceUuid();
            if (StringUtils.isNotBlank(assistanceUuid)) {
                User user = userService.findByUuid(assistanceUuid);
                String loginName = user.getLoginName();
                String websocketUrl = ymlMyConfig.getWebsocketUrl();
                Map<String, String> paramMap = new HashMap<>();
                String taToken = redisService.get(loginName + "TA");
                if (taToken != null) {
                    paramMap.put("token", taToken);
                    paramMap.put("type", "2");
                    TechnicalAssistanceVo vo = new TechnicalAssistanceVo();
                    vo.setTechnicalAssistanceUuid(technicalAssistanceUuid);
                    vo.setUserName(loginUserCache.getUserName());
                    vo.setUserAssistanceId(assistance.getUserAssistanceId());
                    vo.setUserAssistancePwd(assistance.getUserAssistancePwd());
                    vo.setIssue(assistance.getIssue());
                    vo.setCreateTime(assistance.getCreateTime());
                    vo.setAssistanceStatus(2);
                    paramMap.put("message", JSON.toJSONString(vo));
                    String result = HttpUtil.sendPost(websocketUrl, paramMap);
                    LOGGER.info("result of websocket = {}", result);
                } else {
                    LOGGER.info("技术支持:{},token={}已失效", loginName, taToken);
                }
            }
        }
        return ResponseEntity.ok(Response.success());
    }


    /**
     * 待协助列表
     *
     * @param request
     * @return
     */
    @GetMapping("/applyList")
    @LogRecordAnnotation(moduleCode = 100002, moduleName = "技术支持", methodCode = 1000023, methodName = "applyList", description = "待协助列表")
    public ResponseEntity<Response> applyList(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String assistanceUuid = loginUserCache.getUserUuid();
        int assistanceStatus = 1;
        List<TechnicalAssistance> technicalAssistanceList = technicalAssistanceService.findByAssistanceUuidAndAssistanceStatus(assistanceUuid, assistanceStatus);
        List<TechnicalAssistanceVo> voList = TransferUtil.transfer(technicalAssistanceList, TechnicalAssistanceVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 完成申请
     *
     * @param request
     * @return
     */
    @PostMapping("/applyFinish")
    @LogRecordAnnotation(moduleCode = 100002, moduleName = "技术支持", methodCode = 1000024, methodName = "applyFinish", description = "完成申请")
    public ResponseEntity<Response> applyFinish(TechnicalAssistanceUuidRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String technicalAssistanceUuid = request.getTechnicalAssistanceUuid();
        TechnicalAssistance technicalAssistance = new TechnicalAssistance();
        technicalAssistance.setTechnicalAssistanceUuid(technicalAssistanceUuid);
        technicalAssistance.setAssistanceStatus(3);
        technicalAssistance.setUpdateUid(userUuid);
        technicalAssistanceService.update(technicalAssistance);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 获取远程协助状态
     *
     * @param request
     * @return
     */
    @GetMapping("/getAssistanceStatus")
    @LogRecordAnnotation(moduleCode = 100002, moduleName = "技术支持", methodCode = 1000025, methodName = "getAssistanceStatus", description = "获取远程协助状态")
    public ResponseEntity<Response> getAssistanceStatus(TechnicalAssistanceUuidRequest request) {
        TechnicalAssistance technicalAssistance = technicalAssistanceService.findByUuid(request.getTechnicalAssistanceUuid());
        AssistanceStatusVo vo = new AssistanceStatusVo();
        vo.setAssistanceStatus(technicalAssistance.getAssistanceStatus());
        return ResponseEntity.ok(Response.success(vo));
    }
}
