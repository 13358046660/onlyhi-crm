package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.*;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.CpCourse;

import java.util.List;
import java.util.Map;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wqz on 2017/7/6.
 */
public interface CpCourseService {

    int save(CpCourse cpCourse);

    CpCourse findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param cpCourse
     * @return
     */
    int update(CpCourse cpCourse);

    List<CpCourse> selectStudentNoPushMessageCourseList(String currentDate, String currentTime);

    int updatePushStatus(List<String> leadsUuidList);

    /**
     * 根据leadsUuid获取排课的一些信息
     *
     * @param leadsUuid
     * @return
     */
    List<CourseInfoDto> findCourseInfoByLeadsUuid(String leadsUuid);

    /**
     * 获取上传详情（包括老师姓名，学生年级和科目 ）
     *
     * @param courseUuid
     * @return
     */
    CourseDto findCourseDetailsByUuid(String courseUuid);

    /**
     * 老师上课记录总数
     *
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @return
     */
    long countTeacherCourseRecordList(String teacherUuid, String subject, String startDate, String endDate);

    /**
     * 老师上课记录列表
     *
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseRecordDto> findTeacherCourseRecordList(String teacherUuid, String subject, String startDate, String endDate, int pageNo, int pageSize);


    /**
     * 查询leadsUuid消耗的课时
     *
     * @param leadsUuid
     * @return
     */
    double findClassTimeByLeadsUuid(String leadsUuid);

    /**
     * 所有有排课的学生总数
     *
     * @return
     */
    int countCourseLeads();

    /**
     * 小于消耗课时的学生数
     *
     * @param classTime
     * @return
     */
    int countLessClassTimeLeads(double classTime);

    /**
     * 查询学生未参加课程的节数
     *
     * @param leadsUuid
     * @return
     */
    int countNoJoinClassCount(String leadsUuid);

    /**
     * 学生课程的总节数
     *
     * @param leadsUuid
     * @return
     */
    int countCourseByLeadsUuid(String leadsUuid);

    /**
     * 根据leadsUuid和课程级别查询消耗总课时
     *
     * @param leadsUuid
     * @param courseLevel
     * @return
     */
    double findClassTimeByLeadsUuidAndCourseLevel(String leadsUuid, int courseLevel);

    /**
     * 根据leadsUuid和课程日期查询未结束课程列表
     *
     * @param leadsUuid
     * @param courseDate
     * @return
     */
    List<CourseDto> findStudentNoEndCourseListByLeadsUuidAndCourseDate(String leadsUuid, String courseDate);

    /**
     * 查询老师消耗的课时(正式课)
     *
     * @param teacherUuid
     * @return
     */
    double findClassTimeByTeacherUuid(String teacherUuid);

    /**
     * 所有有排课的教师总数
     *
     * @return
     */
    int countCourseTeacher();

    /**
     * 小于消耗课时的教师数
     *
     * @param classTime
     * @return
     */
    int countLessClassTimeTeacher(double classTime);

    /**
     * 教师没有参加课程的节数
     *
     * @param teacherUuid
     * @return
     */
    int countTeacherNoJoinClassCount(String teacherUuid);

    /**
     * 教师课程总节数
     *
     * @param teacherUuid
     * @return
     */
    int countCourseByTeacherUuid(String teacherUuid);

    /**
     * 根据teacherUuid和课程级别查询消耗总课时
     *
     * @param teacherUuid
     * @param courseLevel
     * @return
     */
    double findClassTimeByTeacherUuidAndCourseLevel(String teacherUuid, int courseLevel);

    /**
     * 根据teacherUuid和课程日期查询未结束课程列表
     *
     * @param teacherUuid
     * @param courseDate
     * @return
     */
    List<CourseDto> findStudentNoEndCourseListByTeacherUuidAndCourseDate(String teacherUuid, String courseDate);

    /**
     * 教师未结束课程数
     *
     * @param teacherUuid
     * @return
     */
    long countTeacherNoEndCourse(String teacherUuid);

    /**
     * 分页查询教师未结束课程
     *
     * @param teacherUuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseDto> findTeacherNoEndCourse(String teacherUuid, int pageNo, int pageSize);

    /**
     * 查询课程开始时间是否在当前时间十分钟内，是:返回课程信息，否:返回null
     *
     * @param courseUuid
     * @return
     */
    CpCourse findStartByUuid(String courseUuid);

    /**
     * 分页查询学生未结束课程列表
     *
     * @param leadsUuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseDto> findNoEndCourseByLeadsUuid(String leadsUuid, int pageNo, int pageSize);

    /**
     * 分页查询教师未结束课程列表（一对一）
     *
     * @param teacherUuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseDto> findTeacherNoEndV1Course(String teacherUuid, int pageNo, int pageSize);

    /**
     * 教师上课记录列表（一对一）
     *
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param courseType
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseDto> findTeacherCourseRecordV1List(String teacherUuid, String subject, String startDate, String endDate, Integer courseType, int pageNo, int pageSize);

    /**
     * 学生上课记录列表（一对一）
     * 测评课和正式课
     *
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseDto> findStudentCourseRecordV1List(String leadsUuid, String subject, String startDate, String endDate, int pageNo, int pageSize);


    /**
     * 学生上课记录数（一对多）
     *
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @return
     */
    long countStudentCourseRecordList(String leadsUuid, String subject, String startDate, String endDate);

    /**
     * 学生上课记录列表（一对多）
     *
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseDto> findStudentCourseRecordList(String leadsUuid, String subject, String startDate, String endDate, int pageNo, int pageSize);

    /**
     * 当前课程是否未结束
     *
     * @param courseUuid
     * @return true:未结束   false:已结束
     */
    boolean countNoEndCourseByUuid(String courseUuid);

    /**
     * 未结束课程列表
     *
     * @param subject
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourse(String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize);

    /**
     * 结束课程，退出房间
     *
     * @param courseUuid
     * @param userUuid
     * @param currentTimeMillis
     * @param userType
     * @return
     */
    int finishClass(String courseUuid, String userUuid, long currentTimeMillis, String userType);
    /**
     * 结束课程，退出房间
     *
     * @param courseUuid
     * @param userUuid
     * @param currentTimeMillis
     * @param userType
     * @return
     */
    int finishClassPhp(String courseUuid, String userUuid, long currentTimeMillis, String userType,String phpToken);
    /**
     * 开始课程，进入房间
     *
     * @param courseUuid
     * @param userUuid
     * @param userType
     * @param userName
     * @param agoraUid
     * @param currentTimeMillis
     * @return
     */
    ClassRoom startClass(String courseUuid, String userUuid, String userType, String userName, int agoraUid, long currentTimeMillis);
    /**
     * 开始课程，进入房间
     *
     * @param courseUuid
     * @param userUuid
     * @param userType
     * @param userName
     * @param agoraUid
     * @param currentTimeMillis
     * @return
     */
    ClassRoom startClassPhp(String courseUuid, String userUuid, String userType, String userName, int agoraUid, long currentTimeMillis,String phpToken);
    /**
     * 家长监听自己孩子的测评和正式课列表
     *
     * @param patriarchUuid
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CourseDto> findNoEndCourseByPatriarchMonitor(String patriarchUuid, int pageNo, int pageSize);

    /**
     * 质检监课--监听所有学生的调试、测评和正式课列表
     *
     * @param subject
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourseByQCMonitor(String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize);

    /**
     * cc:自己学生的调试课和测评课
     *
     * @param userUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourseByCC(String userUuid, String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize);

    /**
     * cr:自己学生的调试课、测评课和正式课
     *
     * @param userUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourseByCR(String userUuid, String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize);

    int countNoEndDebugCourseByCC(String userUuid);

    List<CourseDto> findNoEndDebugCourseByCC(String userUuid, int pageNo, int pageSize);

    int countNoEndDebugCourseByCR(String userUuid);

    List<CourseDto> findNoEndDebugCourseByCR(String userUuid, int pageNo, int pageSize);

    /**
     * @param teacherUuid
     * @param yearMonth   yyyy-MM
     * @return
     */
    List<String> findCourseDateByTeacherUuidAndYearMonth(String teacherUuid, String yearMonth);

    List<CourseDto> findByByTeacherUuidAndCourseDate(String teacherUuid, String courseDate);

    List<CourseDto> findNoEndCourseListByTeacherUuidAndCourseDate(String teacherUuid, String courseDate, int pageNo, int pageSize);

    List<CpCourse> findStartedCourseByCourseDate(String courseDate);
    List<CpCourse> findStartedCourseByCourseDateNew(String courseDate,String courseUuid) throws Exception;

    /**
     * 成功上过正式课的数量
     *
     * @param teacherUuid
     * @return
     */
    int countSuccessCourseByTeacherUuid(String teacherUuid);

    double findClassTimeByTeacherUuidAndMonthDate(String teacherUuid, String monthDate);

    int countCourseTeacherByMonthDate(String monthDate);

    int countLessClassTimeTeacherByMonthDate(double classTime, String monthDate);

    List<CourseDto> findByByTeacherUuidAndCourseDate2(String teacherUuid, String courseDate);

    List<CourseDto> findByCourseDate(String courseDate);

    double findClassTimeByLeadsUuidAndMonth(String leadsUuid, String monthDate);

    int countCourseLeadsByMonthDate(String monthDate);

    int countLessClassTimeLeadsByMonthDate(double classTime, String monthDate);

    int countNoJoinClassCountByMonthDate(String leadsUuid, String monthDate);

    int countCourseByLeadsUuidByMonthDate(String leadsUuid, String monthDate);

    List<CourseDto> findByLeadsUuidAndCourseDate(String leadsUuid, String courseDate);

    List<CourseDto> findNoEndCourseListByLeadsUuidAndCourseDate(String leadsUuid, String courseDate);

    List<String> findCourseDateByLeadsUuidAndYearMonth(String leadsUuid, String yearMonth);
    Map<String,Object> selectTodayCourse(TodayCourseVo vo);
    CpCourse selectNormalBack(String uuid);
    /**
     * 教师上课记录列表（一对一）
     *
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param courseType
     * @return
     */
    List<CourseDto> findTeacherCourseRecordV1ListNew(String teacherUuid, String subject, String startDate, String endDate, Integer courseType);
    /**
     * 学生上课记录列表（一对一）
     * 测评课和正式课
     *
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @return
     */
    List<CourseDto> findStudentCourseRecordV1ListNew(String leadsUuid, String subject, String startDate, String endDate);
    /**
     * 学生上课记录列表（一对一）
     * 一天天的查
     * @param subject
     * @return
     */
    List<CourseDto> findTeacherCourseRecordV1OneDay(String teacherUuid,String subject,String courseDate,Integer courseType);
    String findMinDate(String teacherUuid,String leadsUuid);

    ClassRoomVO enterRoom(ClassRoomVO classRoomVOIn);

    ClassRoomVO enterRoomPhp(ClassRoomVO classRoomVOIn,String phpToken);
}