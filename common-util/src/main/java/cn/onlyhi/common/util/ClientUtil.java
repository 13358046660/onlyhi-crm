package cn.onlyhi.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import static cn.onlyhi.common.constants.Constants.cmdQueue;
import static cn.onlyhi.common.constants.Constants.loginFieldList;
import static cn.onlyhi.common.util.FileUtils.getDateTimeFileName;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/5/27.
 */
public class ClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUtil.class);

    public static <T> void validate(T t) throws ValidationException {
        ValidatorFactory vFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = vFactory.getValidator();
        Set<ConstraintViolation<T>> set = validator.validate(t);
        if (set.size() > 0) {
            StringBuilder validateError = new StringBuilder();
            for (ConstraintViolation<T> val : set) {
                validateError.append(val.getMessage());
            }
            throw new ValidationException(validateError.toString());
        }
    }

    /**
     * 获取声网用户Id
     *
     * @param userUuid
     * @param userType
     * @return
     */
    public static int getAgoraUid(String userUuid, String userType) {
        return Math.abs((userUuid + userType).hashCode());
    }

    /**
     * 获取get请求参数封装为map
     *
     * @param param
     * @return
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<String, Object>();
        if ("".equals(param) || null == param) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    /**
     * 已知矩形对角线两顶点坐标，求矩形的长宽
     *
     * @param point1 坐标1：格式x,y
     * @param point2 坐标2：格式x,y
     * @return
     */
    public static String[] getRectWH(String point1, String point2) {
        String[] p1 = point1.split(",");
        String x1 = p1[0];
        String y1 = p1[1];
        String[] p2 = point2.split(",");
        String x2 = p2[0];
        String y2 = p2[1];
        int a = Math.abs(Integer.parseInt(x1) - Integer.parseInt(x2));
        int b = Math.abs(Integer.parseInt(y1) - Integer.parseInt(y2));
        String as = String.valueOf(a);
        String bs = String.valueOf(b);
      /*  if (a > b) {//暂时注释，画的矩形是反的，不判断宽高
            return new String[]{as, bs};
        }*/
        return new String[]{as, bs};
    }

    /**
     * 已知椭圆内切矩形对角线两顶点坐标，求椭圆长短半径及其圆心坐标
     *
     * @param point1 "1,2"
     * @param point2 "3,4"
     * @return String[0][0]:长半径    String[0][1]:短半径    String[1][0]:圆心x坐标  String[1][1]:圆心y坐标
     */
    public static String[][] getCircleInfo(String point1, String point2) {
        String[][] circleInfo = new String[2][2];
        String[] p1 = point1.split(",");
        String x1 = p1[0];
        String y1 = p1[1];
        String[] p2 = point2.split(",");
        String x2 = p2[0];
        String y2 = p2[1];
        int ix1 = Integer.parseInt(x1);
        int ix2 = Integer.parseInt(x2);
        int iy1 = Integer.parseInt(y1);
        int iy2 = Integer.parseInt(y2);
        BigDecimal divisor = new BigDecimal("2");

        int a = Math.abs(ix1 - ix2);
        int b = Math.abs(iy1 - iy2);
        BigDecimal a1 = new BigDecimal(a);
        BigDecimal b1 = new BigDecimal(b);
        String as = String.valueOf(a1.divide(divisor).doubleValue());
        String bs = String.valueOf(b1.divide(divisor).doubleValue());
     /*   if (a > b) {暂时注释，画的圆回放位置是反的
            circleInfo[0][0] = as;
            circleInfo[0][1] = bs;
        } else {
            circleInfo[0][0] = bs;
            circleInfo[0][1] = as;
        }*/
        circleInfo[0][0] = as;
        circleInfo[0][1] = bs;

        String xs = String.valueOf(new BigDecimal(ix1 + ix2).divide(divisor).doubleValue());
        String ys = String.valueOf(new BigDecimal(iy1 + iy2).divide(divisor).doubleValue());
        circleInfo[1][0] = xs;
        circleInfo[1][1] = ys;
        return circleInfo;
    }


    @Deprecated
    public static Integer getFirstKeyByValue(Map<Integer, String> map, String value) {
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        for (Map.Entry<Integer, String> entry : entrySet) {
            String entryValue = entry.getValue();
            if (entryValue.equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static final Pattern TIME_FORMAT = Pattern.compile("(\\d{2}:){2}\\d{2}.\\d{2}");

    /**
     * 将时间字符串转换为毫秒数
     *
     * @param str 格式：00:00:00.00
     * @return
     * @throws Exception
     */
    public static Integer str2ms(String str) throws Exception {
        if (str == null) {
            return null;
        }
        str = str.trim();
        boolean matches = TIME_FORMAT.matcher(str).matches();
        if (!matches) {
            throw new Exception("时间字符串格式不正确，正确格式为：00:00:00.00");
        }
        String[] temp = str.split(":");
        int hours = Integer.parseInt(temp[0]);  //小时数
        int minute = Integer.parseInt(temp[1]); //  分钟数
        String[] temp1 = temp[2].split("\\.");
        int sec = Integer.parseInt(temp1[0]);   //秒数
        int msec = Integer.parseInt(temp1[1]);   //毫秒数
        int allTime = hours * 60 * 60 * 1000 + minute * 60 * 1000 + sec * 1000 + msec;
        return allTime;
    }

    /**
     * 根据请求对象获取传入的token值，若没有则返回null
     *
     * @param rquestObj
     * @return
     * @throws Exception
     */
    /*public static String getToken(Object rquestObj) throws Exception {
        Class<?> aClass = rquestObj.getClass();
        Field field;
        String token = null;
        if (BaseRequest.class.getSimpleName().equals(aClass.getSimpleName())) {
            field = aClass.getDeclaredField("token");
        } else {
            if (rquestObj instanceof PageRequest) {
                if (PageRequest.class.getSimpleName().equals(aClass.getSimpleName())) {
                    field = aClass.getSuperclass().getDeclaredField("token");
                } else {
                    field = aClass.getSuperclass().getSuperclass().getDeclaredField("token");
                }
            } else {
                field = aClass.getSuperclass().getDeclaredField("token");
            }
        }
        String fieldName = field.getName(); //获取属性
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1); // 将属性的首字符大写，方便构造get，set方法
        String type = field.getGenericType().toString(); // 获取属性的类型
        if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
            Method m = aClass.getMethod("get" + fieldName);
            token = (String) m.invoke(rquestObj); // 调用getter方法获取属性值
        }
        return token;
    }*/
    public static String getToken(Object rquestObj) throws Exception {
        Class<?> aClass = rquestObj.getClass();
        Method m = aClass.getMethod("getToken");
        String token = (String) m.invoke(rquestObj); // 调用getter方法获取属性值
        return token;
    }

    public static void setToken(Object rquestObj, String token) throws Exception {
        Class<?> aClass = rquestObj.getClass();
        Method m = aClass.getMethod("setToken", String.class);
        m.invoke(rquestObj, token);
    }

    public static Map<String, String> getLoginInfo(Object rquestObj) {
        Map<String, String> loginInfoMap = new HashMap<>();
        Class<?> aClass = rquestObj.getClass();
        for (String name : loginFieldList) {
            String fieldName = getFirstUpperCase(name);
            try {
                Method m = aClass.getMethod("get" + fieldName);
                String fieldValue = (String) m.invoke(rquestObj);
                loginInfoMap.put(name, fieldValue);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return loginInfoMap;
    }

    /**
     * 获取值的首字母大写
     *
     * @param value
     * @return
     */
    public static String getFirstUpperCase(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1); // 将value的首字符大写
    }

    public static void setFieldValue(Object rquestObj, String fieldName, Object fieldValue) throws Exception {
        Class<?> aClass = rquestObj.getClass();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1); // 将属性的首字符大写，方便构造get，set方法
        Method m;
       /* if (fieldValue instanceof com.alibaba.fastjson.JSONArray) {
            m = aClass.getMethod("set" + fieldName, List.class);
        } else {

        }*/
        m = aClass.getMethod("set" + fieldName, fieldValue.getClass());
        m.invoke(rquestObj, fieldValue);
    }

    /**
     * 获取随机4位数
     *
     * @return
     */
    public static String getFourRandom() {
        Random rad = new Random();
        String result = rad.nextInt(10000) + "";
        if (result.length() != 4) {
            return getFourRandom();
        }
        return result;
    }

    /***
     * Get request query string, form method : post
     *
     * @param request
     * @return byte[]
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        /*当无请求参数时，request.getContentLength()返回-1 */
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /***
     * Get request query string, form method : post
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        if (buffer == null) {
            return null;
        }
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    public static String getRequestBody(InputStream stream) {
        String line = "";
        StringBuilder body = new StringBuilder();
        int counter = 0;

        // 读取POST提交的数据内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            while ((line = reader.readLine()) != null) {
                if (counter > 0) {
                    body.append("rn");
                }
                body.append(line);
                counter++;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return body.toString();
    }

    /**
     * 将json请求参数设置到请求对象中
     *
     * @param httpServletRequest
     * @param obj
     * @throws Exception
     */
    public static void jsonParamsToRequest(HttpServletRequest httpServletRequest, Object obj) throws Exception {
        String contentType = httpServletRequest.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String charEncoding = httpServletRequest.getCharacterEncoding();
            if (charEncoding == null) {
                charEncoding = "UTF-8";
            }
            String requestBody = IOUtils.toString(httpServletRequest.getInputStream(), charEncoding);
            if (StringUtils.isNotBlank(requestBody)) {
                JSONObject jsonObject = JSONObject.parseObject(requestBody);
                Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    setFieldValue(obj, key, value);
                }
            }
        }
    }

    /**
     * 获取libreoffice5.3在docker中的命令路径
     *
     * @return
     */
    public static String getSofficeCmd() {
        String sofficeCmd = "";
        for (int i = 0; i < 10; i++) {
            sofficeCmd = cmdQueue.poll();
            if (StringUtils.isNotBlank(sofficeCmd)) {
                return sofficeCmd;
            }
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
                LOGGER.info("e3: ", e);
            }
        }
        return sofficeCmd;
    }

    /**
     * 发送websocket消息
     *
     * @param token
     * @param type
     * @param message
     * @param websocketUrl
     * @return
     */
    public static String sendWebsocketMessageToUser(String token, String type, String message, String websocketUrl) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("token", token);
        paramMap.put("type", type);
        paramMap.put("message", message);
        String result = HttpUtil.sendPost(websocketUrl, paramMap);
        LOGGER.info("发送websocket消息【{}】结果: {}", message, result);
        return result;
    }

    /**
     * 获取教师上传图片到oss的key值
     *
     * @param fileName
     * @return
     */
    public static String getTeacherUploadOssKey(String fileName) {
        String dateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String key = "teacher" + "/" + dateStr + "/" + getDateTimeFileName(fileName);
        return key;
    }

    /**
     * 根据教师保存在oss上的url生成规则获取其ossKey
     *
     * @param ossUrl
     * @return
     */
    public static String getTeacherOssKey(String ossUrl) {
        return ossUrl.substring(ossUrl.lastIndexOf("teacher"), ossUrl.indexOf("?"));
    }

    /**
     * 根据教师保存在oss上的url生成规则获取其保存的文件名称
     *
     * @param ossUrl
     * @return
     */
    public static String getTeacherOssSaveName(String ossUrl) {
        return ossUrl.substring(ossUrl.lastIndexOf("/") + 1, ossUrl.indexOf("?"));
    }

    /*public static void main(String[] args) {
        String teacherOssSaveName = getTeacherOssSaveName("https://image.onlyhi.cn/teacher/2018-01-29/1517196772466-image.jpg?Expires=1517283176&OSSAccessKeyId=LTAIuZJID8X9AyIl&Signature=pTuyOLCCX4G3u8TIDSd3EDIYvRU%3D");
        System.out.println(teacherOssSaveName);
    }*/

    /**
     * 去除list中重复元素
     *
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List removeDuplicate(List list) {
        List newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);//获取传入集合对象的每一个元素
            if (!newList.contains(o)) {   //查看新集合中是否有指定的元素，如果没有则加入
                newList.add(o);
            }
        }
        return newList;  //返回集合
    }


    public static String md5(URL url) {
        String content = null;
        try {
            content = SecurityUtil.hashMD5Hex(url.openStream());
        } catch (IOException e) {
            LOGGER.info(url.toString() + " 加密失败:{}", e);
            LOGGER.error(e.getMessage());
        }
        return content;
    }

    public static String md5(File file) {
        String content = null;
        try {
            content = SecurityUtil.hashMD5Hex(new FileInputStream(file));
        } catch (IOException e) {
            LOGGER.info(file.getPath() + " 加密失败:{}", e);
            LOGGER.error(e.getMessage());
        }
        return content;
    }
}
