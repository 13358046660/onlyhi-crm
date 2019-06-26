package cn.onlyhi.common.enums;

public enum CodeEnum {

    SUCCESS(0, "成功"),
    FAILURE(1, "错误"),
    INVALID_TOKEN(112, "登录失效，请重新登录"),
    ALREADY_BING(113, "该学生账号已经绑定"),
    NO_BING(114, "没有绑定学生账号"),
    PARAMETER_ERROR(115, "参数不全"),
    INVALID_MOBILE(116, "该手机号码未注册"),
    INVALID_USERNAME_PWD(117, "密码不正确"),
    INVALID_OLD_USERNAME_PWD(118, "原密码不正确"),
    INVALID_FILEFORMAT(119, "文件格式不合规范"),
    INVALID_ACCOUNT(120, "无此账号"),
    NO_COURSE(121, "无此课程"),
    NO_WECHAT(122, "微信信息不存在"),
    NO_QQ(123, "qq信息不存在"),
    NO_SINAMICROBLOG(124, "新浪微博信息不存在"),
    BING_FAIL(125, "绑定失败"),
    IMUSER_NO_EXIST(126, "IM用户不存在"),
    IMPASSWORD_INVALID(127, "IM用户密码不正确"),
    MOBILE_HAS_BING_QQ(128, "该手机号码已经绑定过其他QQ,不能再绑定该QQ"),
    MOBILE_HAS_BING_WECHAT(129, "该手机号码已经绑定过其他微信,不能再绑定该微信"),
    MOBILE_HAS_BING_SINAMICROBLOG(130, "该手机号码已经绑定过其他微博,不能再绑定该微博"),
    COURSE_HAS_APPRAISE(131, "课程已评价"),
    NO_MONITOR(132, "没有监课权限"),
    NO_CC(133, "不是cc"),
    NO_CR(134, "不是cr"),
    INVALID_IDENTITY(135, "无效身份"),
    NO_USER(136, "该用户不存在！"),
    COURSE_NO_STUDENT(137, "该课程学生信息不存在！"),
    NO_OVERTIME(138, "课程不能超过当天23:59:59！"),
    NO_TA(139, "不是技术支持"),
    P_ALREADY_BING(140, "该家长账号已经绑定过学生"),
    INVALID_VERIFY_CODE(144, "无效验证码"),
    NO_RECORD_DATA(145, "该课程无视频回放"),
    NO_ACCESS_AUTH(146, "没有访问权限"),
    TEMP_NO_RECORD_DATA(147, "视频转换中，次日方可查看。"),
    CANNOT_END_COURSE(148, "非法操作！课程还没上就下课！"),
    STUDENT_HAS_SIGN(149, "今天已签到"),
    ROOM_NUMBER_FULL(300, "房间人数已满"),
    MOBILE_HAS_REGISTERED(301, "该手机号码已经注册"),
    SMS_SEND_FAIL(302, "验证码发送失败"),
    AUTHCODE_TIMEOUT(303, "验证码超时,请重新获取"),
    AUTHCODE_ERROR(304, "验证码输入错误"),
    COURSE_NO_STARAT(305, "课程还未开始,请稍后进入！"),
    COURSE_HAS_END(306, "课程已结束！"),
    VOICEFILE_NOEXIT(307, "音频录制文件不存在！"),
    VOICEDIR_NOEXIT(308, "音频录制目录不存在！"),
    NULL_FILE(309, "上传文件不能为空！"),
    INVALID_COURSE(310, "该课程已被取消！"),
    INVALID_FILE(400, "文件不合法！"),
    UNAUTHORIZED(401, "未授权"),
    SERRVER_ERROR(500, "服务器错误"),
    FILECONVERT_ERROR(501, "课件转换错误"),
    FILE_UPLOAD_FAIL(502, "课件上传失败，请重新上传！"),
    FILE_ALREADY_CONVERT(502, "课件已转换！"),
    NO_ROOM(503, "未进入房间！"),
    ILLEGAL_OUT_ROOM(504, "未到上课时间，可退出教室，不能下课！"),
    INVALID_FILE_MATCH(505, "文件格式与后缀不匹配，可能改过文件后缀"),
    REPEAT_FILE(506, "上传了重复文件");
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
