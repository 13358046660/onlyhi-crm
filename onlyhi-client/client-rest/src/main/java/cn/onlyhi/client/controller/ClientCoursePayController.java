package cn.onlyhi.client.controller;

import cn.onlyhi.client.Pingpp.Event;
import cn.onlyhi.client.Pingpp.PingppSecretKeyConfig;
import cn.onlyhi.client.Pingpp.PingppUtil;
import cn.onlyhi.client.baidustaging.BaiduStagingConfig;
import cn.onlyhi.client.baidustaging.BaiduStagingPushResponse;
import cn.onlyhi.client.baidustaging.Sign;
import cn.onlyhi.client.dto.CoursePriceTypeDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.dto.OrderListDto;
import cn.onlyhi.client.po.CoursePriceBaiduCode;
import cn.onlyhi.client.po.CpCoursePrice;
import cn.onlyhi.client.po.CpPayRequest;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.PromotionCode;
import cn.onlyhi.client.po.Student;
import cn.onlyhi.client.rabbitmq.Producter;
import cn.onlyhi.client.request.BaiduStaginPushRequest;
import cn.onlyhi.client.request.CoursePriceListRequest;
import cn.onlyhi.client.request.CoursePriceTypeListRequest;
import cn.onlyhi.client.request.DirectBaiduStagingPaymentRequest;
import cn.onlyhi.client.request.DirectPingppPaymentRequest;
import cn.onlyhi.client.request.OrderBaiduStagingPayRequest;
import cn.onlyhi.client.request.OrderListRequest;
import cn.onlyhi.client.request.PayMoneyRequest;
import cn.onlyhi.client.request.PingppOrderPayStatusRequest;
import cn.onlyhi.client.request.PingppOrderRequest;
import cn.onlyhi.client.service.CoursePriceBaiduCodeService;
import cn.onlyhi.client.service.CpCoursePriceService;
import cn.onlyhi.client.service.CpPayRequestService;
import cn.onlyhi.client.service.LeadsService;
import cn.onlyhi.client.service.PromotionCodeService;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.service.StudentService;
import cn.onlyhi.client.vo.CoursePriceListVo;
import cn.onlyhi.client.vo.OrderListVo;
import cn.onlyhi.client.vo.OrderPayStatusVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.constants.Constants;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.enums.EnumStringObj;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import com.pingplusplus.model.Charge;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static cn.onlyhi.client.baidustaging.BaiduStagingUtil.SIGNKEY;

/**
 * 课程订单支付
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/24.
 */
@RestController
@RequestMapping("/client/coursepay")
public class ClientCoursePayController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCoursePayController.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private CpCoursePriceService cpCoursePriceService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private LeadsService leadsService;
    @Autowired
    private CpPayRequestService cpPayRequestService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private PingppSecretKeyConfig pingppSecretKeyConfig;
    @Autowired
    private BaiduStagingConfig baiduStagingConfig;
    @Autowired
    private Producter producter;
    @Autowired
    private PromotionCodeService promotionCodeService;
    @Autowired
    private CoursePriceBaiduCodeService coursePriceBaiduCodeService;


    /**
     * 获取优惠活动类型列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getActivityTypeList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001106, methodName = "getActivityTypeList", description = "课时包列表")
    public ResponseEntity<Response> getActivityTypeList(BaseRequest request) {
        List<EnumStringObj> enumObjeList = ClientEnum.ActivityType.getEnumObjeList();
        return ResponseEntity.ok(Response.success(enumObjeList));
    }

    /**
     * 获取课时包类型列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getCoursePriceTypeList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001107, methodName = "getCoursePriceTypeList", description = "获取课时包类型列表")
    public ResponseEntity<Response> getCoursePriceTypeList(CoursePriceTypeListRequest request) {
        String activityType = request.getActivityType();
        List<CoursePriceTypeDto> list = cpCoursePriceService.findCoursePriceTypeListByActivityType(activityType);
        return ResponseEntity.ok(Response.success(list));
    }

    /**
     * 获取课时包列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getCoursePriceList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001108, methodName = "getCoursePriceList", description = "获取课时包列表")
    public ResponseEntity<Response> getCoursePriceList(CoursePriceListRequest request) {
        String activityType = request.getActivityType();
        String type = request.getType();
        List<CpCoursePrice> coursePriceList = cpCoursePriceService.findCoursePriceListByActivityTypeAndType(activityType, type);
        List<CoursePriceListVo> coursePriceListVoList = new ArrayList<>();
        int size = coursePriceList.size();
        for (int i = 0; i < size; i++) {
            CoursePriceListVo coursePriceListVo = new CoursePriceListVo();
            CpCoursePrice cpCoursePrice = coursePriceList.get(i);
            Integer length = cpCoursePrice.getLength();
            String uuid = cpCoursePrice.getUuid();
            coursePriceListVo.setUuid(uuid);
            coursePriceListVo.setLength(length);
            coursePriceListVo.setOriginalPrice(cpCoursePrice.getOriginalPrice());
            coursePriceListVo.setNowPrice(cpCoursePrice.getNowPrice());
            coursePriceListVo.setSpecialPrice(cpCoursePrice.getSpecialPrice());
            coursePriceListVo.setSpecialDiscount(cpCoursePrice.getSpecialDiscount());
            String coursePricePackageName = getCoursePricePackageName(type, length.toString());
            coursePriceListVo.setCoursePricePackageName(coursePricePackageName);
            if ("小学".equals(type)) {
                //赠送课时
                PromotionCode promotionCode = promotionCodeService.findPromotionCodeByCoursePriceUuidAndCode(uuid, "0902");
                Integer giveLength = promotionCode.getGiveLength();
                coursePriceListVo.setGiveLength(giveLength);
            }
            coursePriceListVoList.add(coursePriceListVo);
        }
        return ResponseEntity.ok(Response.success(coursePriceListVoList));
    }

    /**
     * 根据课时包和优惠码获取支付金额
     *
     * @param request
     * @return
     */
    @GetMapping("/getPayMoney")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001109, methodName = "getPayMoney", description = "根据课时包和优惠码获取支付金额")
    public ResponseEntity<Response> getPayMoney(PayMoneyRequest request) {
        String coursePriceUuid = request.getCoursePriceUuid();
        String code = request.getCode();
        CpCoursePrice coursePrice = cpCoursePriceService.findByUuid(coursePriceUuid);
        if (coursePrice == null) {
            return ResponseEntity.ok(Response.errorCustom("此课时包不存在，请联系管理员！"));
        }
        PromotionCode promotionCode = promotionCodeService.findPromotionCodeByCoursePriceUuidAndCode(coursePriceUuid, code);
        //应付款
        double payMoney = coursePrice.getNowPrice();
        if (promotionCode != null) {
            payMoney = coursePrice.getNowPrice() - promotionCode.getPromotionMoney();
        }
        return ResponseEntity.ok(Response.success(payMoney));
    }

    @PostMapping("/directPingppPaymentByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001112, methodName = "directPingppPaymentByJson", description = "课时包使用ping++直接支付")
    public ResponseEntity<Response> directPingppPaymentByJson(@RequestBody DirectPingppPaymentRequest request) throws Exception {
        return directPingppPaymentCom(request);
    }

    @PostMapping("/directPingppPayment")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001113, methodName = "directPingppPayment", description = "课时包使用ping++直接支付")
    public ResponseEntity<Response> directPingppPayment(DirectPingppPaymentRequest request) throws Exception {
        return directPingppPaymentCom(request);
    }

    /**
     * 课时包使用ping++直接支付
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> directPingppPaymentCom(DirectPingppPaymentRequest request) throws Exception {
        return ResponseEntity.ok(Response.errorCustom("购买课程请联系课程顾问"));
        /*String token = request.getToken();
        String coursePriceUuid = request.getCoursePriceUuid();
        String code = request.getCode();
        String channel = request.getChannel();
        CpCoursePrice coursePrice = cpCoursePriceService.findByUuid(coursePriceUuid);
        if (coursePrice == null) {
            return ResponseEntity.ok(Response.errorCustom("此课时包不存在，请联系管理员！"));
        }
        LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
        Leads leads = leadsService.findLeadsByPhone(loginUserCache.getPhone());
        if (StringUtils.isBlank(leads.getGrade()) || StringUtils.isBlank(leads.getSubject())) {
            return ResponseEntity.ok(Response.errorCustom("请完善个人年级和科目信息！"));
        }
        String leadsUuid = loginUserCache.getUserUuid();
        //关闭课时包旧的未支付未关闭订单
        cpPayRequestService.closeNoPayOrderByCoursePriceUuidAndLeadsUuid(coursePriceUuid, leadsUuid);
        CpPayRequest cpPayRequest = new CpPayRequest();
        PromotionCode promotionCode = null;
        if (StringUtils.isNotBlank(code)) {
            promotionCode = promotionCodeService.findPromotionCodeByCoursePriceUuidAndCode(coursePriceUuid, code);
        }
        //应付款
        double payMoney = coursePrice.getNowPrice();
        if (promotionCode != null) {
            payMoney = coursePrice.getNowPrice() - promotionCode.getPromotionMoney();
            cpPayRequest.setPromotionCodeUuid(promotionCode.getUuid());
        }
        String type = coursePrice.getType();
        Integer length = coursePrice.getLength();
        String orderNo = OrderGenerateUtils.generateOrderNo();

        //创建ping++订单
        //channel = "alipay";
        channel = "wx";
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        //订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
        chargeMap.put("amount", payMoney * 100);
        // TODO: 2017/8/2  为1分钱的测试数据
     *//*   if ("84f4d32a774411e796a5ecf4bbc00a24".equals(coursePrice.getUuid())) {
            chargeMap.put("amount", payMoney);
        }*//*
        //3 位 ISO 货币代码，人民币为  cny 。
        chargeMap.put("currency", "cny");
        String coursePricePackageName = getCoursePricePackageName(type, length.toString());
        //商品标题，该参数最长为 32 个 Unicode 字符
        chargeMap.put("subject", coursePricePackageName);
        //商品描述信息，该参数最长为 128 个 Unicode 字符
        chargeMap.put("body", coursePricePackageName);
        // 推荐使用 8-20 位，要求数字或字母，不允许其他字符
        chargeMap.put("order_no", orderNo);
        // 支付使用的第三方支付渠道取值，请参考：https://www.pingxx.com/api#api-c-new
        chargeMap.put("channel", channel);
        String clientIp = IpUtil.getLocalIp(httpServletRequest);
        // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1
        chargeMap.put("client_ip", clientIp);
        Map<String, String> app = new HashMap<>();
        app.put("id", PingppUtil.appId);
        chargeMap.put("app", app);
        // extra 取值请查看相应方法说明
        chargeMap.put("extra", PingppUtil.getInstance(pingppSecretKeyConfig.getApiKey(), PingppUtil.rsaPrivateKey).channelExtra(channel));
        //发起交易请求
        Charge charge = Charge.create(chargeMap);
        if (charge.getFailureCode() == null) {
            //创建crm订单
            String orderUuid = UUIDUtil.randomUUID2();
            //支付订单uuid
            cpPayRequest.setUuid(orderUuid);
            //支付订单号
            cpPayRequest.setOrderNo(orderNo);
            //支付订单提交人
            cpPayRequest.setUserUuid(leadsUuid);
            cpPayRequest.setLeadsUuid(leadsUuid);
            cpPayRequest.setBuyLength(length.toString());
            //课程级别
            byte courseLevel = 0;
            if (ClientEnum.CoursePackageType.TSINGHUA.equals(type.trim())) {
                courseLevel = 1;
            } else if (ClientEnum.CoursePackageType.STAR.equals(type.trim())) {
                courseLevel = 2;
            } else {
                courseLevel = 0;
            }
            cpPayRequest.setCourseLevel(courseLevel);
            cpPayRequest.setCoursePriceUuid(coursePriceUuid);
            cpPayRequest.setMoney(String.valueOf(payMoney));
            cpPayRequest.setPayType("pingpp");
            cpPayRequest.setPingppChannel(channel);
            cpPayRequest.setChargeId(charge.getId());
            cpPayRequest.setTransactionNo(charge.getTransactionNo());
            cpPayRequest.setPendingPay(payMoney);
            cpPayRequest.setPayChannel((byte) 2);
            cpPayRequestService.save(cpPayRequest);
            //将订单uuid传入消息队列
            producter.send(orderUuid);
            //将charge传到客户端
            return ResponseEntity.ok(Response.success(charge));
        } else {
            return ResponseEntity.ok(Response.errorCustom(charge.getFailureMsg()));
        }*/

    }

    /*public static void main(String[] args) {
        double payMoney = 1d;
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        chargeMap.put("amount", payMoney * 100);  //订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）

        chargeMap.put("currency", "cny");   //3 位 ISO 货币代码，人民币为  cny 。
        chargeMap.put("subject", "hehe");   //商品标题，该参数最长为 32 个 Unicode 字符
        chargeMap.put("body", "hehe");     //商品描述信息，该参数最长为 128 个 Unicode 字符
        chargeMap.put("order_no", "11111dsdsdsss");// 推荐使用 8-20 位，要求数字或字母，不允许其他字符
        chargeMap.put("channel", "wx");// 支付使用的第三方支付渠道取值，请参考：https://www.pingxx.com/api#api-c-new
        chargeMap.put("client_ip", "127.0.0.1"); // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1
        Map<String, String> app = new HashMap<>();
        app.put("id", PingppUtil.appId);
        chargeMap.put("app", app);
        // extra 取值请查看相应方法说明
        chargeMap.put("extra", PingppUtil.getInstance("sk_test_zf5SCGi5yT4OffTuTSOWbzL0", PingppUtil.rsaPrivateKey).channelExtra("wx"));
        //发起交易请求
        try {
            Charge charge = Charge.create(chargeMap);
            LOGGER.info("charge={}", charge);
        } catch (AuthenticationException e) {
        } catch (InvalidRequestException e) {
        } catch (APIConnectionException e) {
        } catch (APIException e) {
        } catch (ChannelException e) {
        } catch (RateLimitException e) {
        }
    }*/


    private String getCoursePricePackageName(String type, String length) {
        String name = type + length;
        if ("小学".equals(type)) {
            name += "节课";
        } else {
            name += "课时";
        }
        return name;
    }


    /**
     * ping++支付成功推送 Webhooks
     *
     * @RequestBody Event event
     */
    @PostMapping("/chargeSucceeded")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001114, methodName = "chargeSucceeded", description = "ping++支付成功推送", checkToken = false)
    public ResponseEntity<Response> chargeSucceeded(BaseRequest request) throws Exception {
        InputStream inputStream = httpServletRequest.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        LOGGER.info("接收到的请求参数={}", sb);
        Event event = JSON.parseObject(sb.toString(), Event.class);
        LOGGER.info("参数解析之后event={}", event);
        //判断是否为event对象
        if (Constants.EVENT.equals(event.getObject())) {
            //支付对象，支付成功时触发。
            if (Constants.CHARGESTATUS.equals(event.getType())) {
                Charge charge = event.getData().getObject();
                CpPayRequest cpPayRequest = cpPayRequestService.findPayRequestByOrderNo(charge.getOrderNo());
                if (cpPayRequest != null) {
                    //ping++支付方式
                    cpPayRequest.setPingppChannel(charge.getChannel());
                    //ping++支付订单号
                    cpPayRequest.setChargeId(charge.getId());
                    //ping++支付渠道返回的交易流水号
                    cpPayRequest.setTransactionNo(charge.getTransactionNo());
                    //已付款（charge.getAmount()为分，转换为元）?
                    cpPayRequest.setAlreadyPay(charge.getAmount() / 100d);
                    //支付时间
                    cpPayRequest.setPayTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(charge.getTimePaid() * 1000)));
                    //支付状态
                    cpPayRequest.setIsPay(charge.getPaid() ? (byte) 1 : (byte) 0);
                    int i = cpPayRequestService.update(cpPayRequest);
                    if (i > 0) {
                        String leadsUuid = cpPayRequest.getLeadsUuid();
                        Leads leads = leadsService.findByUuid(leadsUuid);
                        //非正式生转为正式生
                        if (StringUtils.isBlank(leads.getStuNo())) {
                            String grade = leads.getGrade();
                            String subject = leads.getSubject();
                            String stuNo = RandomNum.generateStuNo(grade, subject);
                            Student student = new Student();
                            student.setStuNo(stuNo);
                            student.setLeadUuid(leadsUuid);
                            student.setName(leads.getName());
                            student.setPassword(leads.getPassword());
                            student.setPhone(leads.getPhone());
                            student.setGrade(grade);
                            student.setSubject(subject);
                            student.setSex(leads.getSex());
                            student.setAge(leads.getAge());
                            student.setExamArea(leads.getExamArea());
                            studentService.save(student);

                            //将学号更新到leads表中
                            leads.setStuNo(stuNo);
                            leadsService.update(leads);
                        }
                    }
                } else {
                    LOGGER.info("订单没了，是谁删了数据库数据？！");
                }
            }
        }
        return ResponseEntity.ok(Response.success());
    }


    /**
     * 分页获取订单列表
     *
     * @param request
     * @return
     */
    @GetMapping("getOrderList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001115, methodName = "getOrderList", description = "分页获取订单列表")
    public ResponseEntity<Response> getOrderList(OrderListRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        //leadsUuid
        String userUuid = loginUserCache.getUserUuid();
        Integer payStatus = request.getPayStatus();
        Integer status = null;
        Integer isPay = null;
        if (payStatus != null) {
            if (Objects.equals(ClientEnum.PayStatus.CLOSED,payStatus)) {
                status = 0;
            } else if (Objects.equals(ClientEnum.PayStatus.TOBEPAID,payStatus )) {
                status = 1;
                isPay = 0;
            } else if (Objects.equals(ClientEnum.PayStatus.CLOSED,payStatus)) {
                status = 1;
                isPay = 1;
            }
        }
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        int total = cpPayRequestService.countOrderList(userUuid, isPay, status);
        List<OrderListDto> orderList = cpPayRequestService.findOrderList(userUuid, isPay, status, pageNo, pageSize);
        List<OrderListVo> orderListVoList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (OrderListDto orderListDto : orderList) {
            OrderListVo orderListVo = new OrderListVo();
            Date createDate = orderListDto.getCreateDate();
            String length = orderListDto.getLength();
            String type = orderListDto.getType();
            String money = orderListDto.getMoney();
            String orderNo = orderListDto.getOrderNo();
            String orderUuid = orderListDto.getOrderUuid();
            String payTime = orderListDto.getPayTime();
            Integer isPay1 = orderListDto.getIsPay();
            Integer status1 = orderListDto.getStatus();
            Long originalPrice = orderListDto.getOriginalPrice();
            if (originalPrice == null) {
                originalPrice = 0L;
                String content = "正式支付环境课时包测试数据被删除！！！课时包uuid:84f4d32a774411e796a5ecf4bbc00a24";
                LOGGER.info(content);
                continue;
            }
            //已关闭
            if (status1 == 0) {
                orderListVo.setOrderStatus(0);
            } else {
                //待支付
                if (isPay1 == 0) {
                    orderListVo.setOrderStatus(1);
                    //已支付
                } else if (isPay1 == 1) {
                    orderListVo.setOrderStatus(2);
                }
            }
            orderListVo.setCoursePricePackageName(type + length + "课时");
            if (createDate != null) {
                orderListVo.setCreateDate(dateFormat.format(createDate));
            } else {
                orderListVo.setCreateDate("");
            }
            orderListVo.setMoney(money);
            orderListVo.setOrderNo(orderNo);
            orderListVo.setOrderUuid(orderUuid);
            orderListVo.setPayTime(payTime);
            orderListVo.setOriginalPrice(Long.toString(originalPrice));
            orderListVo.setDiscountPrice(Double.toString(originalPrice - Double.parseDouble(money)));

            orderListVoList.add(orderListVo);
        }
        Page<OrderListVo> page = new Page<>(total, orderListVoList);
        return ResponseEntity.ok(Response.success(page));
    }

    @PostMapping("/orderPingppPayByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001116, methodName = "orderPingppPayByJson", description = "订单ping++支付")
    public ResponseEntity<Response> orderPingppPayByJson(@RequestBody PingppOrderRequest request) throws Exception {
        return orderPingppPayCom(request);
    }

    @PostMapping("/orderPingppPay")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001117, methodName = "orderPingppPay", description = "订单ping++支付")
    public ResponseEntity<Response> orderPingppPay(PingppOrderRequest request) throws Exception {
        return orderPingppPayCom(request);
    }

    /**
     * 订单ping++支付
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> orderPingppPayCom(PingppOrderRequest request) throws Exception {
        return ResponseEntity.ok(Response.errorCustom("购买课程请联系课程顾问"));
       /* String orderUuid = request.getOrderUuid();
        String channel = request.getChannel();
        OrderListDto orderListDto = cpPayRequestService.findOrderByOrderUuid(orderUuid);
        if (orderListDto == null) {
            return ResponseEntity.ok(Response.errorCustom("此订单不存在，请联系管理员！"));
        }
        String money = orderListDto.getMoney();
        String buyLength = orderListDto.getLength();
        String orderNo = orderListDto.getOrderNo();
        String type = orderListDto.getType();

        //创建ping++订单
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        //订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
        chargeMap.put("amount", Double.valueOf(money) * 100);
        //3 位 ISO 货币代码，人民币为  cny 。
        chargeMap.put("currency", "cny");
        String coursePricePackageName = getCoursePricePackageName(type, buyLength);
        //商品标题，该参数最长为 32 个 Unicode 字符
        chargeMap.put("subject", coursePricePackageName);
        //商品描述信息，该参数最长为 128 个 Unicode 字符
        chargeMap.put("body", coursePricePackageName);
        // 推荐使用 8-20 位，要求数字或字母，不允许其他字符
        chargeMap.put("order_no", orderNo);
        // 支付使用的第三方支付渠道取值，请参考：https://www.pingxx.com/api#api-c-new
        chargeMap.put("channel", channel);
        String clientIp = IpUtil.getLocalIp(httpServletRequest);
        // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1
        chargeMap.put("client_ip", clientIp);
        Map<String, String> app = new HashMap<>();
        app.put("id", PingppUtil.appId);
        chargeMap.put("app", app);
        // extra 取值请查看相应方法说明
        chargeMap.put("extra", PingppUtil.getInstance(pingppSecretKeyConfig.getApiKey(), PingppUtil.rsaPrivateKey).channelExtra(channel));
        //发起交易请求
        Charge charge = Charge.create(chargeMap);
        //创建成功，更新订单支付表
        if (charge.getFailureCode() == null) {
            CpPayRequest cpPayRequest = cpPayRequestService.findByUuid(orderUuid);
            cpPayRequest.setPayType("pingpp");
            cpPayRequest.setPingppChannel(channel);
            cpPayRequest.setChargeId(charge.getId());
            cpPayRequest.setTransactionNo(charge.getTransactionNo());
            cpPayRequestService.update(cpPayRequest);
            return ResponseEntity.ok(Response.success(charge));
        } else {
            return ResponseEntity.ok(Response.errorCustom(charge.getFailureMsg()));
        }*/

    }

    @PostMapping("/directBaiduStagingPaymentByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001122, methodName = "directBaiduStagingPaymentByJson", description = "课时包使用百度分期直接支付")
    public ResponseEntity<Response> directBaiduStagingPaymentByJson(@RequestBody DirectBaiduStagingPaymentRequest request) throws Exception {
        return directBaiduStagingPaymentCom(request);
    }

    @PostMapping("/directBaiduStagingPayment")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001123, methodName = "directBaiduStagingPayment", description = "课时包使用百度分期直接支付")
    public ResponseEntity<Response> directBaiduStagingPayment(DirectBaiduStagingPaymentRequest request) throws Exception {
        return directBaiduStagingPaymentCom(request);
    }

    /**
     * 课时包使用百度分期直接支付
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> directBaiduStagingPaymentCom(DirectBaiduStagingPaymentRequest request) throws Exception {
        return ResponseEntity.ok(Response.errorCustom("购买课程请联系课程顾问"));
      /*  String token = request.getToken();
        String coursePriceUuid = request.getCoursePriceUuid();
        String code = request.getCode();
        CpCoursePrice coursePrice = cpCoursePriceService.findByUuid(coursePriceUuid);
        if (coursePrice == null) {
            return ResponseEntity.ok(Response.errorCustom("此课时包不存在，请联系管理员！"));
        }
        LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
        Leads leads = leadsService.findLeadsByPhone(loginUserCache.getPhone());
        if (StringUtils.isBlank(leads.getGrade()) || StringUtils.isBlank(leads.getSubject())) {
            return ResponseEntity.ok(Response.errorCustom("请完善个人年级和科目信息！"));
        }
        String type = coursePrice.getType();
        String userUuid = loginUserCache.getUserUuid();
        String courseId = getCourseId(type, userUuid);
        if (courseId == null) {
            return ResponseEntity.ok(Response.errorCustom("百度分期不可用！"));
        }

        CpPayRequest cpPayRequest = new CpPayRequest();
        PromotionCode promotionCode = null;
        if (StringUtils.isNotBlank(code)) {
            promotionCode = promotionCodeService.findPromotionCodeByCoursePriceUuidAndCode(coursePriceUuid, code);
        }
        //应付款
        double payMoney = coursePrice.getNowPrice();
        if (promotionCode != null) {
            payMoney = coursePrice.getNowPrice() - promotionCode.getPromotionMoney();
            cpPayRequest.setPromotionCodeUuid(promotionCode.getUuid());
        }
        Integer length = coursePrice.getLength();
        String orderNo = OrderGenerateUtils.generateOrderNo();
        String patriarchName = request.getPatriarchName();
        String patriarchPhone = request.getPatriarchPhone();
        //创建百度分期订单
        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("username", patriarchName);
        userMap.put("mobile", patriarchPhone);
        userMap.put("email", "");
        String jsonStr = JSON.toJSONString(userMap);
        String rsaStr = Rsa.encrypt(Rsa.getPublicKey(PUBLICKEY), jsonStr);

        Map<String, String> map = new HashMap<String, String>();
        map.put("action", "sync_order_info");
        map.put("corpid", CORPID);
        map.put("orderid", orderNo);
        map.put("money", new BigDecimal(payMoney * 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("period", "");
        map.put("oauthid", userUuid);
        map.put("courseid", courseId);
        String coursePricePackageName = getCoursePricePackageName(type, length.toString());
        map.put("coursename", coursePricePackageName);
        map.put("tpl", CORPID);
        map.put("data", rsaStr);
        map.put("addrtype", "1");

        // 获取sign
        String sign = Sign.createBaseSign(map, SIGNKEY);
        map.put("sign", sign);

        // post 请求
        Set<Map.Entry<String, String>> entries = map.entrySet();
        String postStr = "";
        for (Map.Entry<String, String> entry : entries) {
            postStr += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8") + "&";
        }
        LOGGER.info("百度分期创建订单请求参数postStr：{}", postStr);
        String result = HttpRequestUtil.sendPost(baiduStagingConfig.getUrl(), postStr);
        LOGGER.info("百度分期创建订单返回结果：{}", result);
        BaiduStagingResponse baiduStagingResponse = JSON.parseObject(result, BaiduStagingResponse.class);
        if (baiduStagingResponse.getStatus() == 0) {
            //创建crm订单
            String orderUuid = UUIDUtil.randomUUID2();
            //支付订单uuid
            cpPayRequest.setUuid(orderUuid);
            //支付订单号
            cpPayRequest.setOrderNo(orderNo);
            //支付订单提交人
            cpPayRequest.setUserUuid(userUuid);
            cpPayRequest.setLeadsUuid(userUuid);
            cpPayRequest.setBuyLength(length.toString());
            //课程级别
            byte courseLevel = 0;
            if (Objects.equals(ClientEnum.CoursePackageType.TSINGHUA,type.trim())) {
                courseLevel = 1;
            } else if (Objects.equals(ClientEnum.CoursePackageType.STAR,type.trim())) {
                courseLevel = 2;
            } else {
                courseLevel = 0;
            }
            cpPayRequest.setCourseLevel(courseLevel);
            cpPayRequest.setCoursePriceUuid(coursePriceUuid);
            cpPayRequest.setMoney(String.valueOf(payMoney));
            cpPayRequest.setPayType("baiduStaging");
            cpPayRequest.setPendingPay(payMoney);
            cpPayRequest.setPayChannel((byte) 2);
            cpPayRequest.setBaiduCode(courseId);
            cpPayRequest.setPatriarchName(patriarchName);
            cpPayRequest.setPatriarchPhone(patriarchPhone);
            cpPayRequestService.save(cpPayRequest);
            //将订单uuid传入消息队列
            producter.send(orderUuid);
            //关闭课时包旧的未支付未关闭订单
            cpPayRequestService.closeNoPayOrderByCoursePriceUuidAndLeadsUuid(coursePriceUuid, userUuid);
            return ResponseEntity.ok(Response.success(baiduStagingResponse.getResult()));
        } else {
            return ResponseEntity.ok(Response.errorCustom("百度分期订单创建失败！"));
        }*/
    }

    private String getCourseId(String type, String userUuid) {
        List<CpPayRequest> payRequestList = cpPayRequestService.findOrderListByUserUuid(userUuid);
        List<CoursePriceBaiduCode> coursePriceBaiduCodeList = coursePriceBaiduCodeService.findBaiduCodeByType(type);
        if (coursePriceBaiduCodeList == null || coursePriceBaiduCodeList.size() == 0) {
            return null;
        }
        if (payRequestList == null || payRequestList.size() == 0) {
            return coursePriceBaiduCodeList.get(0).getBaiduCode();
        }
        List<String> userBaiduCodeList = new ArrayList<>();
        List<String> baiduCodeList = new ArrayList<>();
        for (CpPayRequest payRequest : payRequestList) {
            userBaiduCodeList.add(payRequest.getBaiduCode());
        }
        for (CoursePriceBaiduCode coursePriceBaiduCode : coursePriceBaiduCodeList) {
            baiduCodeList.add(coursePriceBaiduCode.getBaiduCode());
        }
        for (int i = 0; i < userBaiduCodeList.size(); i++) {
            String baiduCode = userBaiduCodeList.get(i);
            if (!baiduCodeList.contains(baiduCode)) {
                return baiduCode;
            }
        }
        return null;
    }

    /**
     * 百度分期支付状态推送接口
     *
     * @param request
     * @return
     */
    @PostMapping("/baiduStaginPush")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001124, methodName = "baiduStaginPush", description = "百度分期支付状态推送接口", checkToken = false)
    public ResponseEntity<BaiduStagingPushResponse> baiduStaginPush(BaiduStaginPushRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Map requestParameterMap = httpServletRequest.getParameterMap();
        for (Object key : requestParameterMap.keySet()) {
            String[] array = (String[]) requestParameterMap.get(key);
            String value = StringUtils.join(array, ",");
            /*if (StringUtils.isBlank(value)) {
                continue;
            }*/
            paramMap.put(key.toString(), StringUtils.trim(value));
        }
        LOGGER.info("百度分期支付状态推送接口参数：paramMap={}", paramMap);
        paramMap.remove("sign");
        // 获取sign
        String sign = Sign.createBaseSign(paramMap, SIGNKEY);
        if (sign.equals(request.getSign())) {
            String orderid = request.getOrderid();
            CpPayRequest payRequest = cpPayRequestService.findPayRequestByOrderNo(orderid);
            if (payRequest == null) {
                return ResponseEntity.ok(new BaiduStagingPushResponse(1, "订单号不存在"));
            }
            Integer status = request.getStatus();
            if (Objects.equals(ClientEnum.IsPay.HAVETOPAID,status)) {
                payRequest.setIsPay((byte) 1);
                //支付时间
                payRequest.setPayTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                cpPayRequestService.update(payRequest);
            } else if (status == 10) {
                payRequest.setIsPay((byte) 0);
                cpPayRequestService.update(payRequest);
            }
            return ResponseEntity.ok(new BaiduStagingPushResponse(0, "success"));
        } else {
            return ResponseEntity.ok(new BaiduStagingPushResponse(2, "参数错误"));
        }


    }

    @PostMapping("/orderBaiduStagingPayByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001125, methodName = "orderBaiduStagingPayByJson", description = "订单百度分期支付")
    public ResponseEntity<Response> orderBaiduStagingPayByJson(@RequestBody OrderBaiduStagingPayRequest request) throws Exception {
        return orderBaiduStagingPayCom(request);
    }

    @PostMapping("/orderBaiduStagingPay")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001126, methodName = "orderBaiduStagingPay", description = "订单百度分期支付")
    public ResponseEntity<Response> orderBaiduStagingPay(OrderBaiduStagingPayRequest request) throws Exception {
        return orderBaiduStagingPayCom(request);
    }

    /**
     * 订单百度分期支付
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> orderBaiduStagingPayCom(OrderBaiduStagingPayRequest request) throws Exception {
        return ResponseEntity.ok(Response.errorCustom("购买课程请联系课程顾问"));
       /* String orderUuid = request.getOrderUuid();
        OrderListDto orderListDto = cpPayRequestService.findOrderByOrderUuid(orderUuid);
        if (orderListDto == null) {
            return ResponseEntity.ok(Response.errorCustom("此订单不存在，请联系管理员！"));
        }
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String type = orderListDto.getType();
        String userUuid = loginUserCache.getUserUuid();
        String courseId = getCourseId(type, userUuid);
        if (courseId == null) {
            return ResponseEntity.ok(Response.errorCustom("百度分期不可用！"));
        }

        String money = orderListDto.getMoney();
        String buyLength = orderListDto.getLength();
        String orderNo = orderListDto.getOrderNo();
        String patriarchName = request.getPatriarchName();
        String patriarchPhone = request.getPatriarchPhone();
        //创建百度分期订单
        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("username", patriarchName);
        userMap.put("mobile", patriarchPhone);
        userMap.put("email", "");
        String jsonStr = JSON.toJSONString(userMap);
        String rsaStr = Rsa.encrypt(Rsa.getPublicKey(PUBLICKEY), jsonStr);

        Map<String, String> map = new HashMap<String, String>();
        map.put("action", "sync_order_info");
        map.put("corpid", CORPID);
        map.put("orderid", orderNo);
        map.put("money", new BigDecimal(Double.valueOf(money) * 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("period", "");
        map.put("oauthid", userUuid);
        map.put("courseid", courseId);
        String coursePricePackageName = getCoursePricePackageName(type, buyLength);
        map.put("coursename", coursePricePackageName);
        map.put("tpl", CORPID);
        map.put("data", rsaStr);
        map.put("addrtype", "1");

        // 获取sign
        String sign = Sign.createBaseSign(map, SIGNKEY);
        map.put("sign", sign);

        // post 请求
        Set<Map.Entry<String, String>> entries = map.entrySet();
        String postStr = "";
        for (Map.Entry<String, String> entry : entries) {
            postStr += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8") + "&";
        }

        String result = HttpRequestUtil.sendPost(baiduStagingConfig.getUrl(), postStr);
        LOGGER.info("百度分期创建订单返回结果：{}", result);
        BaiduStagingResponse baiduStagingResponse = JSON.parseObject(result, BaiduStagingResponse.class);
        if (baiduStagingResponse.getStatus() == 0) {
            //创建crm订单
            CpPayRequest cpPayRequest = cpPayRequestService.findByUuid(orderUuid);
            cpPayRequest.setUserUuid(userUuid);
            cpPayRequest.setLeadsUuid(userUuid);
            cpPayRequest.setPayType("baiduStaging");
            cpPayRequest.setBaiduCode(courseId);
            cpPayRequest.setPatriarchName(patriarchName);
            cpPayRequest.setPatriarchPhone(patriarchPhone);
            cpPayRequestService.update(cpPayRequest);
            return ResponseEntity.ok(Response.success(baiduStagingResponse.getResult()));
        } else {
            return ResponseEntity.ok(Response.errorCustom(baiduStagingResponse.getMsg()));
        }*/
    }

    /**
     * ping++订单支付状态查询
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getPingppOrderPayStatus")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001127, methodName = "getPingppOrderPayStatus", description = "ping++订单支付状态查询")
    public ResponseEntity<Response> getPingppOrderPayStatus(PingppOrderPayStatusRequest request) throws Exception {
        String chargeId = request.getChargeId();
        CpPayRequest cpPayRequest = cpPayRequestService.findOrderByChargeId(chargeId);
        if (cpPayRequest == null) {
            return ResponseEntity.ok(Response.errorCustom("订单不存在！"));
        }
        OrderPayStatusVo orderPayStatusVo = new OrderPayStatusVo();
        if (cpPayRequest.getIsPay() == 0) {
            //主动查询ping++订单状态
            Charge charge = Charge.retrieve(chargeId);
            //查询异常
            if (charge.getFailureCode() != null) {
                return ResponseEntity.ok(Response.errorCustom(charge.getFailureMsg()));
            } else {
                //false:未付款，true:已付款
                if (charge.getPaid()) {
                    orderPayStatusVo.setPayStatus(1);
                    cpPayRequest.setIsPay((byte) 1);
                    //支付时间
                    cpPayRequest.setPayTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(charge.getTimePaid() * 1000)));
                    cpPayRequestService.update(cpPayRequest);
                }
            }
        } else if (cpPayRequest.getIsPay() == 1) {
            orderPayStatusVo.setPayStatus(1);
        } else {
            return ResponseEntity.ok(Response.errorCustom("未知异常，请联系管理员！"));
        }
        return ResponseEntity.ok(Response.success(orderPayStatusVo));
    }

}
