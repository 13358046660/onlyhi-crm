package cn.onlyhi.common.constants;

import cn.onlyhi.common.enums.ClientEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/19.
 */
public class Constants {
    //客户端角色
    public static final String CLIENT_TEACHER = ClientEnum.UserType.TEACHER.name();
    public static final String CLIENT_STUDENT = ClientEnum.UserType.STUDENT.name();
    public static final String CLIENT_PATRIARCH = ClientEnum.UserType.PATRIARCH.name();
    public static final String CLIENT_CC = ClientEnum.UserType.CC.name();
    public static final String CLIENT_CR = ClientEnum.UserType.CR.name();
    public static final String CLIENT_MONITOR = ClientEnum.UserType.MONITOR.name();
    public static final String CLIENT_QC = ClientEnum.UserType.QC.name();

    //文件目录分隔符
    public static final String FILESEPARATOR = File.separator;
    public static final String SUCCESS = "success";
    /**
     * 课件保存目录
     */
    public static final String COURSEWARE = "courseware";
    /**
     * 录制文件保存目录
     */
    public static final String RECORD_DIR = "recordDir";

    /**
     * 文件上传保存根目录
     */
    public static final String UPLOAD_ROOT = "uploadPath";
    public static final String UPLOAD_ROOT2 = "uploadDir";
    /**
     * 文件保存根目录路径
     */
    public static final String CLIENTFILE_ROOT = "/www/clientfile/uploadPath";
    /**
     * 录制文件保存根目录路径
     */
    public static final String RECORD_DIR_ROOT = CLIENTFILE_ROOT + FILESEPARATOR + RECORD_DIR;
    /**
     * 课件保存根目录路径
     */
    public static final String COURSEWARE_ROOT = CLIENTFILE_ROOT + FILESEPARATOR + COURSEWARE;
    /**
     * 文件上传根目录
     */
    public static final String FILEUPLOAD_DIR = "clientupload";
    /**
     * 文件上传根目录路径
     */
    public static final String FILEUPLOAD_ROOT = CLIENTFILE_ROOT + FILESEPARATOR + FILEUPLOAD_DIR;

    /**
     * onlyhi-file中websocket的key
     */
    public static final String SESSION_USER_ID = "token";
    /**
     * client-websocket中的key
     */
    public static final String WEBSOCKET_TOKEN = "token";
    public static final String WEBSOCKET_TYPE = "type";
    /**
     * 防止数据和以前的数据混淆
     */
    public static final String NONCE = "nonce";
    //白板id
    /**
     * 课件白板ID
     */
    public static final String BOARD_0 = "board0";
    /**
     * 纯白版ID
     */
    public static final String BOARD_1 = "board1";
    //操作类型
    /**
     * 画点
     */
    public static final String DRAWMODE_01 = "01";
    /**
     * 画直线
     */
    public static final String DRAWMODE_02 = "02";
    /**
     * 画矩形
     */
    public static final String DRAWMODE_03 = "03";
    /**
     * 画圆（椭圆）
     */
    public static final String DRAWMODE_04 = "04";
    /**
     * 写文字
     */
    public static final String DRAWMODE_05 = "05";
    /**
     * 使用橡皮
     */
    public static final String DRAWMODE_07 = "07";
    /**
     * 清除画板
     */
    public static final String DRAWMODE_08 = "08";


    //用户角色
    /**
     * cc
     */
    public static final String ROLE_CC = "cc";
    /**
     * cr
     */
    public static final String ROLE_CR = "cr";
    /**
     * 技术支持
     */
    public static final String ROLE_TA = "ta";
    /**
     * 教学监课
     */
    public static final String ROLE_TS = "ts";
    /**
     * 质检
     */
    public static final String ROLE_QC = "qc";
    /**
     * 质检专员
     */
    public static final String ROLE_QCZY = "qczy";
    /**
     * 异常短信接收人
     */
    public static final String SENDPHONE = "18001295396";
    /**
     * 录制轨迹文件根路径
     */
    public static final String BASEPATH = "/www/hktRecord/mylogs/";
    /**
     * 录制轨迹原始文件保存路径
     */
    public static final String BASESAVEPATH = "/www/recordDir/";
    /**
     * 技术支持redis zset的key值
     */
    public static final String TASET = "taset";
    /**
     * 技术支持redis zset的默认分数
     */
    public static final double DEFAULTSCORE = 0;

    /**
     * libreoffice5.3在docker中的命令路径
     */
    public static ConcurrentLinkedQueue<String> cmdQueue = new ConcurrentLinkedQueue<>();
    /**
     * 判断是否为event对象
     */
    public static final String EVENT="event";
    /**
     * 支付对象，支付成功时触发
     */
    public static final String CHARGESTATUS="charge.succeeded";
    static {
        cmdQueue.add("docker exec office1 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office2 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office3 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office4 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office5 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office6 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office7 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office8 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office9 /opt/libreoffice5.3/program/soffice ");
        cmdQueue.add("docker exec office10 /opt/libreoffice5.3/program/soffice ");
    }

    /**
     * 验证码登录
     */
    public static List<Integer> loginMethodCodeList_authcode = Arrays.asList(new Integer[]{10000142, 10000143});
    /**
     * 密码登录
     */
    public static List<Integer> loginMethodCodeList_mima = Arrays.asList(new Integer[]{10000144, 10000145});
    /**
     * 微信登录
     */
    public static List<Integer> loginMethodCodeList_wechat = Arrays.asList(new Integer[]{10000166, 10000167});
    /**
     * qq登录
     */
    public static List<Integer> loginMethodCodeList_qq = Arrays.asList(new Integer[]{10000170, 10000171});
    /**
     * 新浪微博
     */
    public static List<Integer> loginMethodCodeList_sinamicroblog = Arrays.asList(new Integer[]{10000174, 10000175});
    /**
     * 员工密码登录
     */
    public static List<Integer> loginMethodCodeList_emp = Arrays.asList(new Integer[]{100001157, 100001158});
    /**
     * 需要的登录信息
     */
    public static List<String> loginFieldList = Arrays.asList(new String[]{"phone", "deviceType", "userType", "name", "loginName"});
    public static List<Integer> loginMethodCodeList = new ArrayList<>();

    static {
        loginMethodCodeList.addAll(loginMethodCodeList_authcode);
        loginMethodCodeList.addAll(loginMethodCodeList_mima);
        loginMethodCodeList.addAll(loginMethodCodeList_wechat);
        loginMethodCodeList.addAll(loginMethodCodeList_qq);
        loginMethodCodeList.addAll(loginMethodCodeList_sinamicroblog);
        loginMethodCodeList.addAll(loginMethodCodeList_emp);
    }

    /**
     * 阿里云OSS存储配置参数
     */
    public static final String endpoint = "oss-cn-shanghai.aliyuncs.com";
    public static final String accessKeyId = "LTAIuZJID8X9AyIl";
    public static final String accessKeySecret = "2c7ykyjqy6VqpDMcXby39jaiLhpw7N";

    /**
     * 科目
     */
    public static final String SYS_ENUM_TYPE_SUBJECT = "5";
    /**
     * 教师年级
     */
    public static final String SYS_ENUM_TYPE_GRADE = "6";
    /**
     * 教师年级偏好
     */
    public static final String SYS_ENUM_TYPE_GRADE_PREFERENCE = "7";

    /**
     * 房间人数key的前缀，后接courseUuid
     */
    public static final String PERSONOFCOURSE_KEY = "person-of-course_";
}
