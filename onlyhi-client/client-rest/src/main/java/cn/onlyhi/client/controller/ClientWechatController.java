package cn.onlyhi.client.controller;

import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.LeadsExt;
import cn.onlyhi.client.po.LeadsLog;
import cn.onlyhi.client.po.UserWechat;
import cn.onlyhi.client.request.BingUserRequest;
import cn.onlyhi.client.request.WechatLoginRequest;
import cn.onlyhi.client.service.LeadsLogService;
import cn.onlyhi.client.service.UserWechatService;
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

import javax.xml.bind.ValidationException;
import java.util.Date;

import static cn.onlyhi.common.enums.CodeEnum.AUTHCODE_ERROR;
import static cn.onlyhi.common.enums.CodeEnum.AUTHCODE_TIMEOUT;
import static cn.onlyhi.common.enums.CodeEnum.BING_FAIL;
import static cn.onlyhi.common.enums.CodeEnum.MOBILE_HAS_BING_WECHAT;
import static cn.onlyhi.common.enums.CodeEnum.NO_WECHAT;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/3.
 */
@RestController
@RequestMapping("/client/wechat")
public class ClientWechatController extends BaseController {

    @Autowired
    private UserWechatService userWechatService;
    @Autowired
    private LeadsLogService leadsLogService;

    @PostMapping("loginByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000166, methodName = "loginByJson", description = "学生微信登录", checkToken = false)
    public ResponseEntity<Response> loginByJson(@RequestBody WechatLoginRequest request) throws ValidationException {
        String gender = request.getGender();
        if (StringUtils.isNotBlank(gender)) {
            if ("f".equals(gender)) {
                gender = "女";
            } else if ("m".equals(gender)) {
                gender = "男";
            } else {
                throw new ValidationException("异常性别！");
            }
        }
        request.setGender(gender);
        return loginCom(request);
    }

    @PostMapping("login")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000167, methodName = "login", description = "学生微信登录", checkToken = false)
    public ResponseEntity<Response> login(WechatLoginRequest request) {
        return loginCom(request);
    }

    /**
     * 微信登录
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> loginCom(WechatLoginRequest request) {
        String uid = request.getUid();
        UserWechat userWechat = userWechatService.findUserWechatByUid(uid);
        LoginUserCache loginUserCache = new LoginUserCache();
        if (userWechat == null) {    //第一次微信登录
            //保存微信信息
            userWechat = TransferUtil.transfer(request, UserWechat.class);
            userWechat.setGender(ClientEnum.Gender.getEnumValueByKey(request.getGender()));
            userWechat.setUserWechatUuid(UUIDUtil.randomUUID2());
            userWechatService.save(userWechat);
        } else {
            String phone = userWechat.getPhone();
            if (StringUtils.isNotBlank(phone)) {    //已绑定手机号，进行登录
                String deviceId = request.getDeviceId();
                String deviceType = request.getDeviceType();
                return userLogin(phone, deviceId, deviceType);
            }
        }
        return ResponseEntity.ok(Response.success(loginUserCache));
    }

    @PostMapping("bingByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000168, methodName = "bingByJson", description = "微信绑定手机号", checkToken = false)
    public ResponseEntity<Response> bingByJson(@RequestBody BingUserRequest request) {
        return bingCom(request);
    }

    @PostMapping("bing")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000169, methodName = "login", description = "微信绑定手机号", checkToken = false)
    public ResponseEntity<Response> bing(BingUserRequest request) {
        return bingCom(request);
    }

    /**
     * 微信绑定手机号
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> bingCom(BingUserRequest request) {
        String phone = request.getPhone();
        String authCodeCache = getAuthCode(phone);
        if (StringUtils.isBlank(authCodeCache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        String authCode = request.getAuthCode();
        if (!authCode.equals(authCodeCache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }
        String userName = request.getUserName();
        String uid = request.getUid();
        UserWechat userWechat = userWechatService.findUserWechatByUid(uid);
        if (userWechat == null) { //微信信息不存在
            return ResponseEntity.ok(Response.error(NO_WECHAT));
        } else {
            UserWechat userWechatByPhone = userWechatService.findUserWechatByPhone(phone);
            if (userWechatByPhone != null) {
                return ResponseEntity.ok(Response.error(MOBILE_HAS_BING_WECHAT));
            }
            userWechat.setPhone(phone);
            //绑定
            int i = userWechatService.update(userWechat);
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
                    leadsExt.setIconurl(userWechat.getIconurl());
                    leadsExt.setPhone(userWechat.getPhone());
                    leadsExtService.save(leadsExt);
                }
                if (leadsExt != null && StringUtils.isBlank(leadsExt.getIconurl())) {
                    leadsExt.setIconurl(userWechat.getIconurl());
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
