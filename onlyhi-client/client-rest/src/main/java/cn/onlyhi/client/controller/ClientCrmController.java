package cn.onlyhi.client.controller;

import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Role;
import cn.onlyhi.client.request.CrmTokenRequest;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.service.RoleService;
import cn.onlyhi.client.vo.ClientTokenVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.SecurityUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/7.
 */
@RestController
@RequestMapping("/client/crm")
public class ClientCrmController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private RoleService roleService;

    /**
     * crmToken转换为clientToken
     *
     * @param request
     * @return
     */
    @GetMapping("/getClientToken")
    @LogRecordAnnotation(moduleCode = 100008, moduleName = "客户端与crm交互", methodCode = 1000081, methodName = "getClientToken", description = "crmToken转换为clientToken", checkToken = false)
    public ResponseEntity<Response> getClientToken(CrmTokenRequest request) {
        String crmToken = request.getCrmToken();
        String crmUser = redisService.get(crmToken);
        if (StringUtils.isBlank(crmUser)) {
            return ResponseEntity.ok(Response.errorCustom("crmToken无效！"));
        }
        JSONObject jsonObject = JSON.parseObject(crmUser);
        String loginName = jsonObject.getString("loginName");
        String roleUuid = jsonObject.getString("roleUuid");
        Role role = roleService.findByUuid(roleUuid);
        if (role == null) {
            return ResponseEntity.ok(Response.errorCustom("用户角色不存在！"));
        }
        if (StringUtils.isBlank(role.getAlias())) {
            return ResponseEntity.ok(Response.errorCustom("用户角色别名为空！"));
        }
        String userType = role.getAlias().toUpperCase();
        String indexToken = loginName + userType;
        String clientToken = redisService.get(indexToken);
        if (StringUtils.isBlank(clientToken)) {
            String deviceId = "deviceId_client";
            String deviceType = "deviceType_client";
            String userName = jsonObject.getString("name");
            String userUuid = jsonObject.getString("uuid");
            String phone = jsonObject.getString("phone");
            String password = jsonObject.getString("password");

            clientToken = SecurityUtil.hashMD5Hex(loginName + deviceId + userType);
            //重置token和用户信息
            LoginUserCache loginUserCache = new LoginUserCache();
            loginUserCache.setLoginName(loginName);
            loginUserCache.setDeviceId(deviceId);
            loginUserCache.setDeviceType(deviceType);
            loginUserCache.setUserType(userType);
            loginUserCache.setToken(clientToken);
            loginUserCache.setUserName(userName);
            loginUserCache.setUserUuid(userUuid);
            loginUserCache.setPhone(phone);
            loginUserCache.setAgoraUid(getAgoraUid(userUuid, userType));
            loginUserCache.setPassword(password);

            redisService.set(indexToken, clientToken);
            redisService.setLoginUserCache(clientToken, loginUserCache);
        }
        ClientTokenVo vo = new ClientTokenVo();
        vo.setClientToken(clientToken);
        return ResponseEntity.ok(Response.success(vo));
    }
}
