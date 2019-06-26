package cn.onlyhi.client.controller;

import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.LeadsExt;
import cn.onlyhi.client.po.LeadsLog;
import cn.onlyhi.client.po.UserQq;
import cn.onlyhi.client.request.BingUserRequest;
import cn.onlyhi.client.request.QqLoginRequest;
import cn.onlyhi.client.service.LeadsLogService;
import cn.onlyhi.client.service.UserQqService;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.TransferUtil;
import cn.onlyhi.common.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static cn.onlyhi.common.enums.CodeEnum.AUTHCODE_ERROR;
import static cn.onlyhi.common.enums.CodeEnum.AUTHCODE_TIMEOUT;
import static cn.onlyhi.common.enums.CodeEnum.BING_FAIL;
import static cn.onlyhi.common.enums.CodeEnum.MOBILE_HAS_BING_QQ;
import static cn.onlyhi.common.enums.CodeEnum.NO_QQ;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/3.
 */
@RestController
@RequestMapping("/client/qq")
public class ClientQqController extends BaseController {

    @Autowired
    private UserQqService userQqService;
    @Autowired
    private LeadsLogService leadsLogService;

    @PostMapping("loginByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000170, methodName = "loginByJson", description = "qq登录", checkToken = false)
    public ResponseEntity<Response> loginByJson(@RequestBody QqLoginRequest request) {
        return loginCom(request);
    }

    @PostMapping("login")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000171, methodName = "login", description = "qq登录", checkToken = false)
    public ResponseEntity<Response> login(QqLoginRequest request) {
        return loginCom(request);
    }

    /**
     * qq登录
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> loginCom(QqLoginRequest request) {
        String uid = request.getUid();
        UserQq userQq = userQqService.findUserQqByUid(uid);
        LoginUserCache loginUserCache = new LoginUserCache();
        if (userQq == null) {    //第一次qq登录
            //保存qq信息
            userQq = TransferUtil.transfer(request, UserQq.class);
            userQq.setGender(ClientEnum.Gender.getEnumValueByKey(request.getGender()));
            userQq.setUserQqUuid(UUIDUtil.randomUUID2());
            userQqService.save(userQq);
        } else {
            String phone = userQq.getPhone();
            if (StringUtils.isNotBlank(phone)) {    //已绑定手机号，进行登录
                String deviceId = request.getDeviceId();
                String deviceType = request.getDeviceType();
                return userLogin(phone, deviceId, deviceType);
            }
        }
        return ResponseEntity.ok(Response.success(loginUserCache));
    }

    @PostMapping("bingByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000172, methodName = "bingByJson", description = "qq绑定手机号", checkToken = false)
    public ResponseEntity<Response> bingByJson(@RequestBody BingUserRequest request) {
        return bingCom(request);
    }

    @PostMapping("bing")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000173, methodName = "bing", description = "qq绑定手机号", checkToken = false)
    public ResponseEntity<Response> bing(BingUserRequest request) {
        return bingCom(request);
    }

    /**
     * qq绑定手机号
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> bingCom(BingUserRequest request) {
        String authCode = request.getAuthCode();
        String phone = request.getPhone();
        String authCodeCache = getAuthCode(phone);
        if (StringUtils.isBlank(authCodeCache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        if (!authCode.equals(authCodeCache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }
        String userName = request.getUserName();
        String uid = request.getUid();
        UserQq userQq = userQqService.findUserQqByUid(uid);
        if (userQq == null) { //qq信息不存在
            return ResponseEntity.ok(Response.error(NO_QQ));
        } else {
            UserQq userQQByPhone = userQqService.findUserQQByPhone(phone);
            if (userQQByPhone != null) {
                return ResponseEntity.ok(Response.error(MOBILE_HAS_BING_QQ));
            }
            userQq.setPhone(phone);
            //绑定
            int i = userQqService.update(userQq);
            if (i > 0) {
                Date date = new Date();
                LeadsLog leadsLog = new LeadsLog();
                leadsLog.setName(userName);
                leadsLog.setPhone(phone);
                leadsLog.setChannelUuid(getChannelUuid());
                leadsLog.setSignupDate(date);
                leadsLogService.save(leadsLog);

                Leads leads = leadsService.findLeadsByPhone(phone);
                if (leads == null) {
                    leads = new Leads();
                    leads.setUuid(UUIDUtil.randomUUID2());
                    leads.setPhone(phone);
                    leads.setName(userName);
                    leads.setChannelUuid(getChannelUuid());
                    leads.setSignupDate(date);
                    leadsService.save(leads);
                } else {
                    leads.setName(userName);
                    leadsService.update(leads);
                }
                LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leads.getUuid());
                if (leadsExt == null) {
                    leadsExt = new LeadsExt();
                    leadsExt.setLeadsUuid(leads.getUuid());
                    leadsExt.setIconurl(userQq.getIconurl());
                    leadsExt.setPhone(userQq.getPhone());
                    leadsExtService.save(leadsExt);
                }
                if (leadsExt != null && StringUtils.isBlank(leadsExt.getIconurl())) {
                    leadsExt.setIconurl(userQq.getIconurl());
                    leadsExtService.updateLeadsExtByLeadsUuid(leadsExt);
                }
                //登录
                String deviceId = request.getDeviceId();
                String deviceType = request.getDeviceType();
                return userLogin(phone, deviceId, deviceType, userName);
            } else {
                return ResponseEntity.ok(Response.error(BING_FAIL));
            }
        }
    }
}
