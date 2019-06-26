package cn.onlyhi.client.controller;

import cn.onlyhi.client.chat.ChatConfig;
import cn.onlyhi.client.chat.ClientSecretCredential;
import cn.onlyhi.client.chat.Credential;
import cn.onlyhi.client.chat.EndPoints;
import cn.onlyhi.client.chat.HTTPMethod;
import cn.onlyhi.client.chat.JerseyUtils;
import cn.onlyhi.client.chat.Roles;
import cn.onlyhi.client.dto.IMUserInfoDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.LeadsExt;
import cn.onlyhi.client.po.TcTeacher;
import cn.onlyhi.client.po.User;
import cn.onlyhi.client.po.UserEasemob;
import cn.onlyhi.client.request.AddFriendRequest;
import cn.onlyhi.client.request.GetGroupUsersRequest;
import cn.onlyhi.client.request.IMLoginRequest;
import cn.onlyhi.client.request.IMUserInfoRequest;
import cn.onlyhi.client.request.IMUserNamesRequest;
import cn.onlyhi.client.service.PatriarchService;
import cn.onlyhi.client.service.TcTeacherService;
import cn.onlyhi.client.service.UserService;
import cn.onlyhi.client.vo.IMUserInfoVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.util.Page;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.UUIDUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static cn.onlyhi.common.enums.ClientEnum.UserType.PATRIARCH;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.SERRVER_ERROR;


/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/4.
 */
@RestController
@RequestMapping("/client/chat")
public class ClientChatController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientChatController.class);
    private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    @Autowired
    private ChatConfig chatConfig;
    @Autowired
    private TcTeacherService tcTeacherService;
    @Autowired
    private PatriarchService patriarchService;
    @Autowired
    private UserService userService;

    @GetMapping("/getIMUserList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000193, methodName = "getIMUserList", description = "获取IMUserName列表")
    public ResponseEntity<Response> getIMUserList(IMUserNamesRequest request) {
        List<String> easemobUsernameList = request.getUserNames();
        if (easemobUsernameList == null || easemobUsernameList.size() == 0) {
            throw new ValidationException("userNames不可包含空！");
        }
        List<IMUserInfoVo> voList = getIMUserInfoVoList(easemobUsernameList, 1, Integer.MAX_VALUE);
        return ResponseEntity.ok(Response.success(voList));
    }

    private List<IMUserInfoVo> getIMUserInfoVoList(List<String> easemobUsernameList, int pageNo, int pageSize) {
        List<IMUserInfoVo> voList = new ArrayList<>();
        IMUserInfoVo vo = new IMUserInfoVo();
        List<UserEasemob> userEasemobList = userEasemobService.findByEasemobUsernameList(easemobUsernameList, pageNo, pageSize);
        for (UserEasemob userEasemob : userEasemobList) {
            vo = new IMUserInfoVo();
            String userUuid = userEasemob.getUserUuid();
            String userType = userEasemob.getUserType();
            String easemobUsername = userEasemob.getEasemobUsername();
            vo.setImUserName(easemobUsername);
            if (STUDENT.name().equals(userType)) {
                Leads leads = leadsService.findByUuid(userUuid);
                if (leads != null) {
                    vo.setUserName(leads.getName());
                }
                LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(userUuid);
                if (leadsExt != null) {
                    vo.setIconurl(leadsExt.getIconurl());
                }
            } else if (TEACHER.name().equals(userType)) {
                TcTeacher tcTeacher = tcTeacherService.findByUuid(userUuid);
                vo.setUserName(tcTeacher.getTcName());
                //教师没头像
            } else if (PATRIARCH.name().equals(userType)) {
                //家长没姓名没头像
                continue;
            } else {
                User user = userService.findByUuid(userUuid);
                vo.setUserName(user.getName());
                //crm用户没头像
            }
            voList.add(vo);
        }
        return voList;
    }

    @GetMapping("/getGroupUsers")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000191, methodName = "getGroupUsers", description = "分页获取群组成员")
    public ResponseEntity<Response> getGroupUsers(GetGroupUsersRequest request) throws ValidationException {
        String groupId = request.getGroupId();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        ObjectNode getGroupUsersNode = getGroupUsers(groupId, pageNo, pageSize);
        if (getGroupUsersNode == null) {
            return ResponseEntity.ok(Response.error(SERRVER_ERROR));
        }
        if (getGroupUsersNode.get("error") != null) {
            throw new ValidationException(getGroupUsersNode.get("error_description").asText());
        }
        int count = getGroupUsersNode.get("count").asInt();
        LOGGER.info("分页获取群组成员总数:{}", count);
        List<String> imUserNameList = new ArrayList<>();
        JsonNode data = getGroupUsersNode.get("data");
        Iterator<JsonNode> elements = data.elements();
        while (elements.hasNext()) {
            JsonNode jsonNode = elements.next();
            JsonNode member = jsonNode.get("member");
            JsonNode owner = jsonNode.get("owner");
            if (member != null) {
                imUserNameList.add(member.asText());
            }
            if (owner != null) {
                imUserNameList.add(owner.asText());
            }
        }
        List<IMUserInfoVo> voList = getIMUserInfoVoList(imUserNameList, 1, Integer.MAX_VALUE);
        int size = voList.size();
        LOGGER.info("查询DB获取用户总数:{}", size);
        if (count > size) {
            LOGGER.info("异常情况:环信有注册用户不在DB中！！");
        }
        Page<IMUserInfoVo> page = new Page<>(size, voList);
        return ResponseEntity.ok(Response.success(page));

    }

    @PostMapping("/registerByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000178, methodName = "registerByJson", description = "注册环信IM账号")
    public ResponseEntity<Response> registerByJson(@RequestBody IMLoginRequest request) {
        return registerCom(request);
    }

    @PostMapping("/register")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000179, methodName = "register", description = "注册环信IM账号")
    public ResponseEntity<Response> register(IMLoginRequest request) {
        return registerCom(request);
    }

    /**
     * 注册环信IM账号
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> registerCom(IMLoginRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String userName = loginUserCache.getUserName();
        String imUserName = request.getUserName();
        String password = request.getPassword();
        ObjectNode datanode = JsonNodeFactory.instance.objectNode();
        datanode.put("username", imUserName);
        datanode.put("password", password);
        datanode.put("nickname", userName);
        ObjectNode jsonNodes = getIMUsersByUserName(password);
        if (jsonNodes.get("entities") == null) {
            ObjectNode objectNode = createNewIMUserSingle(datanode);
            LOGGER.info("注册IM用户[单个]: " + objectNode.toString());
            JsonNode entities = objectNode.get("entities");
            if (entities != null) {
                String easemobUuid = entities.get(0).get("uuid").asText();
                String easemobUsername = entities.get(0).get("username").asText();
                //表中用户是否已存在
                UserEasemob userEasemob = userEasemobService.findByUserUuid(userUuid);
                if (userEasemob == null) {  //没有
                    userEasemob = new UserEasemob();
                    userEasemob.setUserEasemobUuid(UUIDUtil.randomUUID2());
                    userEasemob.setUserUuid(userUuid);
                    userEasemob.setUserType(loginUserCache.getUserType());
                    userEasemob.setEasemobUuid(easemobUuid);
                    userEasemob.setEasemobUsername(easemobUsername);
                    userEasemobService.save(userEasemob);
                } else {
                    userEasemob.setUserUuid(userUuid);
                    userEasemob.setUserType(loginUserCache.getUserType());
                    userEasemob.setEasemobUuid(easemobUuid);
                    userEasemob.setEasemobUsername(easemobUsername);
                    userEasemobService.update(userEasemob);
                }
            }
            JsonNode jsonNode = objectNode.get("error");
            if (jsonNode != null) {
                String errorMsg = jsonNode.asText();
                if (!"duplicate_unique_property_exists".equals(errorMsg)) {
                    return ResponseEntity.ok(Response.errorCustom(errorMsg));
                }
            }
        }
        loginUserCache.setRegisterIMFlag(true);
        redisService.setLoginUserCache(loginUserCache.getToken(), loginUserCache);
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/addFriendByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000182, methodName = "addFriendByJson", description = "给IM用户添加好友")
    public ResponseEntity<Response> addFriendByJson(@RequestBody AddFriendRequest request) {
        return addFriendCom(request);
    }

    @PostMapping("/addFriend")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000183, methodName = "addFriend", description = "给IM用户添加好友")
    public ResponseEntity<Response> addFriend(AddFriendRequest request) {
        return addFriendCom(request);
    }

    /**
     * 给IM用户添加好友[单个]
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> addFriendCom(AddFriendRequest request) {
        String ownerUserName = request.getOwnerUserName();
        String friendUserName = request.getFriendUserName();
        ObjectNode addFriendSingleNode = addFriendSingle(ownerUserName, friendUserName);
        if (null != addFriendSingleNode) {
            LOGGER.info("添加好友[单个]: " + addFriendSingleNode.toString());
        }
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/deleteFriendByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000184, methodName = "deleteFriendByJson", description = "解除IM用户的好友关系")
    public ResponseEntity<Response> deleteFriendByJson(@RequestBody AddFriendRequest request) {
        return deleteFriendCom(request);
    }

    @PostMapping("/deleteFriend")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000185, methodName = "deleteFriend", description = "解除IM用户的好友关系")
    public ResponseEntity<Response> deleteFriend(AddFriendRequest request) {
        return deleteFriendCom(request);
    }

    /**
     * 解除IM用户的好友关系
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> deleteFriendCom(AddFriendRequest request) {
        String ownerUserName = request.getOwnerUserName();
        String friendUserName = request.getFriendUserName();
        ObjectNode deleteFriendSingleNode = deleteFriendSingle(ownerUserName, friendUserName);
        if (null != deleteFriendSingleNode) {
            LOGGER.info("解除好友关系: " + deleteFriendSingleNode.toString());
        }
        return ResponseEntity.ok(Response.success());
    }

    @GetMapping("/getIMUserInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000187, methodName = "getIMUserInfo", description = "获取IM用户信息")
    public ResponseEntity<Response> getIMUserInfo(IMUserInfoRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userName = loginUserCache.getUserName();
        String userType = loginUserCache.getUserType();
        String userUuid = loginUserCache.getUserUuid();
        IMUserInfoVo vo = new IMUserInfoVo();
        vo.setUserName(userName);
        if (STUDENT.name().equals(userType)) {
            LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(userUuid);
            if (leadsExt != null) {
                vo.setIconurl(leadsExt.getIconurl());
            }
        }
        vo.setImUserName(request.getUserName());
        return ResponseEntity.ok(Response.success(vo));
    }

    @GetMapping("/getIMUserFriendList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000189, methodName = "getIMUserFriendList", description = "获取IM用户的好友列表")
    public ResponseEntity<Response> getIMUserFriendList(IMUserInfoRequest request) {
        String userName = request.getUserName();
        ObjectNode objectNode = findIMUserFriendList(userName);
        JsonNode jsonNode = objectNode.get("data");
        if (jsonNode != null) {
            List<String> imUserNameList = new ArrayList<>();
            for (int i = 0; i < jsonNode.size(); i++) {
                imUserNameList.add(jsonNode.get(i).asText());
            }
            if (imUserNameList.size() == 0) {
                Page<IMUserInfoDto> page = new Page<>(0, new ArrayList<>());
                return ResponseEntity.ok(Response.success(page));
            }
            int pageNo = request.getPageNo();
            int pageSize = request.getPageSize();
            int count = userEasemobService.countByEasemobUsernameList(imUserNameList);
            List<IMUserInfoVo> voList = getIMUserInfoVoList(imUserNameList, pageNo, pageSize);
            Page<IMUserInfoVo> page = new Page<>(count, voList);
            return ResponseEntity.ok(Response.success(page));
        } else {
            return ResponseEntity.ok(Response.errorCustom("获取好友列表失败！"));
        }
    }

    private Credential getCredential(ChatConfig chatConfig) {
        // 通过app的client_id和client_secret来获取app管理员token
        return new ClientSecretCredential(chatConfig.getClientId(), chatConfig.getClientSecret(), Roles.USER_ROLE_APPADMIN, chatConfig.getAppKey());
    }

    /**
     * 注册IM用户[单个]
     * <p>
     * 给指定chatConfig.getAppKey()创建一个新的用户
     *
     * @param dataNode
     * @return
     */
    private ObjectNode createNewIMUserSingle(ObjectNode dataNode) {
        ObjectNode objectNode = factory.objectNode();
        String appKey = chatConfig.getAppKey();

        // check chatConfig.getAppKey() format
        if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", appKey)) {
            LOGGER.error("Bad format of chatConfig.getAppKey(): " + appKey);
            objectNode.put("message", "Bad format of chatConfig.getAppKey()");
            return objectNode;
        }

        objectNode.removeAll();
        // check properties that must be provided
        if (null != dataNode && !dataNode.has("username")) {
            LOGGER.error("Property that named username must be provided .");
            objectNode.put("message", "Property that named username must be provided .");
            return objectNode;
        }
        if (null != dataNode && !dataNode.has("password")) {
            LOGGER.error("Property that named password must be provided .");
            objectNode.put("message", "Property that named password must be provided .");
            return objectNode;
        }

        JerseyWebTarget webTarget = EndPoints.USERS_TARGET.resolveTemplate("org_name",
                appKey.split("#")[0]).resolveTemplate("app_name",
                appKey.split("#")[1]);
        objectNode = JerseyUtils.sendRequest(webTarget, dataNode, getCredential(chatConfig), HTTPMethod.METHOD_POST, null);

        return objectNode;
    }

    /**
     * 获取IM用户
     *
     * @param userName 用户主键：username或者uuid
     * @return
     */
    private ObjectNode getIMUsersByUserName(String userName) {
        ObjectNode objectNode = factory.objectNode();
        String appKey = chatConfig.getAppKey();
        // check appKey format
        if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", appKey)) {
            LOGGER.error("Bad format of Appkey: " + appKey);
            objectNode.put("message", "Bad format of Appkey");
            return objectNode;
        }

        // check properties that must be provided
        if (StringUtils.isEmpty(userName)) {
            LOGGER.error("The primaryKey that will be useed to query must be provided .");
            objectNode.put("message", "The primaryKey that will be useed to query must be provided .");
            return objectNode;
        }
        JerseyWebTarget webTarget = EndPoints.USERS_TARGET
                .resolveTemplate("org_name", appKey.split("#")[0])
                .resolveTemplate("app_name", appKey.split("#")[1])
                .path(userName);
        objectNode = JerseyUtils.sendRequest(webTarget, null, getCredential(chatConfig), HTTPMethod.METHOD_GET, null);
        return objectNode;
    }

    /**
     * 添加好友[单个]
     *
     * @param ownerUserName
     * @param friendUserName
     * @return
     */
    private ObjectNode addFriendSingle(String ownerUserName, String friendUserName) {
        ObjectNode objectNode = factory.objectNode();
        String appKey = chatConfig.getAppKey();
        // check appKey format
        if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", appKey)) {
            LOGGER.error("Bad format of Appkey: " + appKey);
            objectNode.put("message", "Bad format of Appkey");
            return objectNode;
        }
        if (StringUtils.isEmpty(ownerUserName)) {
            LOGGER.error("Your userName must be provided，the value is username or uuid of imuser.");
            objectNode.put("message", "Your userName must be provided，the value is username or uuid of imuser.");
            return objectNode;
        }
        if (StringUtils.isEmpty(friendUserName)) {
            LOGGER.error("The userName of friend must be provided，the value is username or uuid of imuser.");
            objectNode.put("message", "The userName of friend must be provided，the value is username or uuid of imuser.");
            return objectNode;
        }
        JerseyWebTarget webTarget = EndPoints.USERS_ADDFRIENDS_TARGET
                .resolveTemplate("org_name", appKey.split("#")[0])
                .resolveTemplate("app_name", appKey.split("#")[1])
                .resolveTemplate("ownerUserName", ownerUserName)
                .resolveTemplate("friendUserName", friendUserName);
        ObjectNode body = factory.objectNode();
        objectNode = JerseyUtils.sendRequest(webTarget, body, getCredential(chatConfig), HTTPMethod.METHOD_POST, null);
        return objectNode;
    }

    /**
     * 解除好友关系
     *
     * @param ownerUserName
     * @param friendUserName
     * @return
     */
    private ObjectNode deleteFriendSingle(String ownerUserName, String friendUserName) {
        ObjectNode objectNode = factory.objectNode();
        String appKey = chatConfig.getAppKey();

        // check appKey format
        if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", appKey)) {
            LOGGER.error("Bad format of Appkey: " + appKey);
            objectNode.put("message", "Bad format of Appkey");
            return objectNode;
        }

        if (StringUtils.isEmpty(ownerUserName)) {
            LOGGER.error("Your userName must be provided，the value is username or uuid of imuser.");
            objectNode.put("message", "Your userName must be provided，the value is username or uuid of imuser.");
            return objectNode;
        }

        if (StringUtils.isEmpty(friendUserName)) {
            LOGGER.error("The userName of friend must be provided，the value is username or uuid of imuser.");
            objectNode.put("message", "The userName of friend must be provided，the value is username or uuid of imuser.");
            return objectNode;
        }
        JerseyWebTarget webTarget = EndPoints.USERS_ADDFRIENDS_TARGET
                .resolveTemplate("org_name", appKey.split("#")[0])
                .resolveTemplate("app_name", appKey.split("#")[1])
                .resolveTemplate("ownerUserName", ownerUserName)
                .resolveTemplate("friendUserName", friendUserName);
        ObjectNode body = factory.objectNode();
        objectNode = JerseyUtils.sendRequest(webTarget, body, getCredential(chatConfig), HTTPMethod.METHOD_DELETE, null);
        return objectNode;
    }

    private ObjectNode findIMUserFriendList(String userName) {
        ObjectNode objectNode = factory.objectNode();
        String appKey = chatConfig.getAppKey();
        // check appKey format
        if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", appKey)) {
            LOGGER.error("Bad format of Appkey: " + appKey);
            objectNode.put("message", "Bad format of Appkey");
            return objectNode;
        }
        if (StringUtils.isEmpty(userName)) {
            LOGGER.error("Your userName must be provided，the value is username or uuid of imuser.");
            objectNode.put("message", "Your userName must be provided，the value is username or uuid of imuser.");
            return objectNode;
        }
        JerseyWebTarget webTarget = EndPoints.USERS_FRIENDS_TARGET
                .resolveTemplate("org_name", appKey.split("#")[0])
                .resolveTemplate("app_name", appKey.split("#")[1])
                .resolveTemplate("ownerUserName", userName);
        ObjectNode body = factory.objectNode();
        objectNode = JerseyUtils.sendRequest(webTarget, body, getCredential(chatConfig), HTTPMethod.METHOD_GET, null);
        return objectNode;
    }

    /**
     * 分页获取群组成员列表
     *
     * @param groupId
     * @param pageNo
     * @param pageSize
     * @return
     */
    private ObjectNode getGroupUsers(String groupId, int pageNo, int pageSize) {
        ObjectNode objectNode = factory.objectNode();
        String appKey = chatConfig.getAppKey();
        // check appKey format
        if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", appKey)) {
            LOGGER.error("Bad format of Appkey: " + appKey);
            objectNode.put("message", "Bad format of Appkey");
            return objectNode;
        }
        JerseyWebTarget webTarget = EndPoints.GROUP_USERS_TARGET
                .resolveTemplate("org_name", appKey.split("#")[0])
                .resolveTemplate("app_name", appKey.split("#")[1])
                .resolveTemplate("group_id", groupId)
                .queryParam("pagenum", pageNo)
                .queryParam("pagesize", pageSize);
        ObjectNode body = factory.objectNode();
        objectNode = JerseyUtils.sendRequest(webTarget, body, getCredential(chatConfig), HTTPMethod.METHOD_GET, null);
        return objectNode;
    }
}
