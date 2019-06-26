package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.MessageReturnDto;
import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/14.
 */
@Repository
public class MessageDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDao.class);

    @Value("${message.url}")
    private String url;
    @Value("${message.account}")
    private String account;
    @Value("${message.password}")
    private String password;

    /**
     * 发送短信息
     *
     * @param phone   多个号码使用","分割
     * @param message 发送的内容
     * @return success:成功
     */
    public MessageReturnDto sendMessage(String phone, String message) throws Exception {
        boolean needstatus = true;  // 是否需要状态报告，需要true，不需要false
        String extno = null;    // 扩展码
        String returnString = sendMessage(phone, message, needstatus, extno);
        int state = -1;
        if (StringUtils.isNotBlank(returnString)) {   //不为空则解析
            String[] returnStr = returnString.split("\n");
            String[] timeAndState = returnStr[0].split(",");
            state = Integer.parseInt(timeAndState[1]);
        }
        String stateValue = getStateValue(state);
        return new MessageReturnDto(state,stateValue);
    }

 /*   public static void main(String[] args) throws Exception {
        sendMessageNew("18001295396","text",true,null);
    }*/
    /**
     * @param phone
     * @param message
     * @param needstatus
     * @param extno
     * @return
     * @throws Exception
     */
    private String sendMessage(String phone, String message, boolean needstatus, String extno) throws Exception {
        HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        GetMethod method = new GetMethod();
        try {
            URI base = new URI(url, false);
            method.setURI(new URI(base, "HttpBatchSendSM", false));
            method.setQueryString(new NameValuePair[]{
                    new NameValuePair("account", account),
                    new NameValuePair("pswd", password),
                    new NameValuePair("mobile", phone),
                    new NameValuePair("needstatus", String.valueOf(needstatus)),
                    new NameValuePair("msg", message),
                    new NameValuePair("extno", extno),
            });
            int result = client.executeMethod(method);
            if (result == HttpStatus.SC_OK) {
                InputStream in = method.getResponseBodyAsStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return URLDecoder.decode(baos.toString(), "UTF-8");
            } else {
                throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
            }
        } finally {
            method.releaseConnection();
        }
    }

  /*  private static String sendMessageNew(String phone, String message, boolean needstatus, String extno) throws Exception {
        HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        GetMethod method = new GetMethod();
        try {
            URI base = new URI("http://smsbj1.253.com/msg/send/json", false);
            method.setURI(new URI(base, "HttpBatchSendSM", false));
            method.setQueryString(JSON.toJSONString(new NameValuePair[]{
                    new NameValuePair("account", account),
                    new NameValuePair("pswd", password),
                    new NameValuePair("mobile", phone),
                    new NameValuePair("needstatus", String.valueOf(needstatus)),
                    new NameValuePair("msg", message),
                    new NameValuePair("extno", extno),
            }));
            int result = client.executeMethod(method);
            if (result == HttpStatus.SC_OK) {
                InputStream in = method.getResponseBodyAsStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return URLDecoder.decode(baos.toString(), "UTF-8");
            } else {
                throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
            }
        } finally {
            method.releaseConnection();
        }
    }*/
    /**
     * 响应状态值说明
     *
     * @param state 返回码
     * @return
     */
    private String getStateValue(int state) {
        String stateValue;
        switch (state) {
            case 0:
                stateValue = "success";
                break;
            case -1:
                stateValue = "请求返回空值";
                break;
            case 101:
                stateValue = "无此用户";
                break;
            case 102:
                stateValue = "密码错";
                break;
            case 103:
                stateValue = "提交过快（提交速度超过流速限制）";
                break;
            case 104:
                stateValue = "系统忙（因平台侧原因，暂时无法处理提交的短信）";
                break;
            case 105:
                stateValue = "敏感短信（短信内容包含敏感词）";
                break;
            case 106:
                stateValue = "消息长度错（>536或<=0）";
                break;
            case 107:
                stateValue = "包含错误的手机号码";
                break;
            case 108:
                stateValue = "手机号码个数错（群发>50000或<=0;单发>200或<=0）";
                break;
            case 109:
                stateValue = "无发送额度（该用户可用短信数已使用完）";
                break;
            case 110:
                stateValue = "不在发送时间内";
                break;
            case 111:
                stateValue = "超出该账户当月发送额度限制";
                break;
            case 112:
                stateValue = "无此产品，用户没有订购该产品";
                break;
            case 113:
                stateValue = "extno格式错（非数字或者长度不对）";
                break;
            case 115:
                stateValue = "自动审核驳回";
                break;
            case 116:
                stateValue = "签名不合法，未带签名（用户必须带签名的前提下）";
                break;
            case 117:
                stateValue = "IP地址认证错,请求调用的IP地址不是系统登记的IP地址";
                break;
            case 118:
                stateValue = "用户没有相应的发送权限";
                break;
            case 119:
                stateValue = "用户已过期";
                break;
            case 120:
                stateValue = "测试内容不是白名单";
                break;
            default:
                stateValue = "无法识别错误码";
                break;
        }
        return stateValue;
    }
}
