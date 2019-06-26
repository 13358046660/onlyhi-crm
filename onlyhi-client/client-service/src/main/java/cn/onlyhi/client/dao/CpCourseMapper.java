package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.*;
import cn.onlyhi.client.po.CpCourse;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CpCourseMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CpCourse record);

    CpCourse selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CpCourse record);

    CpCourse selectByUuid(String uuid);

    int updateByUuidSelective(CpCourse record);

    /**
     * 更新课程状态
     *
     * @param courseUuid
     * @param classStatus
     * @return
     */
    int updateClassStatus(@Param("courseUuid") String courseUuid, @Param("classStatus") int classStatus);

    List<CpCourse> selectStudentNoPushMessageCourseList(@Param("currentDate") String currentDate, @Param("currentTime") String currentTime);

    int updatePushStatus(@Param("leadsUuidList") List<String> leadsUuidList);

    List<CourseInfoDto> selectCourseInfoByLeadsUuid(String leadsUuid);

    /**
     * 获取上课详情（包括老师姓名，学生年级和科目 ）
     *
     * @param courseUuid
     * @return
     */
    CourseDto findCourseDetailsByUuid(String courseUuid);

    /**
     * 老师上课记录列表（一对多）
     *
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseRecordDto> findTeacherCourseRecordList(@Param("teacherUuid") String teacherUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 老师上课记录总数（一对多）
     *
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @return
     */
    long countTeacherCourseRecordList(@Param("teacherUuid") String teacherUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate);

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
    int countNoJoinClassCount(@Param("leadsUuid") String leadsUuid);

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
    double findClassTimeByLeadsUuidAndCourseLevel(@Param("leadsUuid") String leadsUuid, @Param("courseLevel") int courseLevel);

    /**
     * 根据leadsUuid和课程日期查询未结束课程列表
     *
     * @param leadsUuid
     * @param courseDate
     * @return
     */
    List<CourseDto> findStudentNoEndCourseListByLeadsUuidAndCourseDate(@Param("leadsUuid") String leadsUuid, @Param("courseDate") String courseDate);

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
    double findClassTimeByTeacherUuidAndCourseLevel(@Param("teacherUuid") String teacherUuid, @Param("courseLevel") int courseLevel);

    /**
     * 根据teacherUuid和课程日期查询未结束课程列表
     *
     * @param teacherUuid
     * @param courseDate
     * @return
     */
    List<CourseDto> findStudentNoEndCourseListByTeacherUuidAndCourseDate(@Param("teacherUuid") String teacherUuid, @Param("courseDate") String courseDate);

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
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findTeacherNoEndCourse(@Param("teacherUuid") String teacherUuid, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

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
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findNoEndCourseByLeadsUuid(@Param("leadsUuid") String leadsUuid, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 分页查询教师未结束课程列表（一对一）
     *
     * @param teacherUuid
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findTeacherNoEndV1Course(@Param("teacherUuid") String teacherUuid, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 教师上课记录列表（一对一）
     *
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findTeacherCourseRecordV1List(@Param("teacherUuid") String teacherUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("courseType") Integer courseType, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 学生上课记录列表（一对一）
     *
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findStudentCourseRecordV1List(@Param("leadsUuid") String leadsUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("startSize") int startSize, @Param("pageSize") int pageSize);


    /**
     * 学生上课记录数（一对多）
     *
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @return
     */
    long countStudentCourseRecordList(@Param("leadsUuid") String leadsUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 学生上课记录列表（一对多）
     *
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findStudentCourseRecordList(@Param("leadsUuid") String leadsUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 当前课程未结束数
     *
     * @param courseUuid
     * @return
     */
    int countNoEndCourseByUuid(String courseUuid);

    /**
     * 未结束课程列表
     *
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourse(@Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("courseType") Integer courseType, @Param("userName") String userName, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 家长监听自己孩子的测评和正式课列表
     *
     * @param patriarchUuid
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findNoEndCourseByPatriarchMonitor(@Param("patriarchUuid") String patriarchUuid, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 质检监课--监听所有学生的调试、测评和正式课列表
     *
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourseByQCMonitor(@Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("courseType") Integer courseType, @Param("userName") String userName, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * cc:自己学生的调试课和测评课
     *
     * @param userUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourseByCC(@Param("userUuid") String userUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("courseType") Integer courseType, @Param("userName") String userName, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * cr:自己学生的调试课、测评课和正式课
     *
     * @param userUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @param startSize
     * @param pageSize  @return
     */
    List<CourseDto> findNoEndCourseByCR(@Param("userUuid") String userUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("courseType") Integer courseType, @Param("userName") String userName, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    int countNoEndDebugCourseByCC(String userUuid);

    List<CourseDto> findNoEndDebugCourseByCC(@Param("userUuid") String userUuid, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    int countNoEndDebugCourseByCR(String userUuid);

    List<CourseDto> findNoEndDebugCourseByCR(@Param("userUuid") String userUuid, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    List<String> findCourseDateByTeacherUuidAndYearMonth(@Param("teacherUuid") String teacherUuid, @Param("yearMonth") String yearMonth);

    List<CourseDto> findByByTeacherUuidAndCourseDate(@Param("teacherUuid") String teacherUuid, @Param("courseDate") String courseDate);

    List<CourseDto> findNoEndCourseListByTeacherUuidAndCourseDate(@Param("teacherUuid") String teacherUuid, @Param("courseDate") String courseDate, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    List<CpCourse> findStartedCourseByCourseDate(String courseDate);
    List<CpCourse> findStartedCourseByCourseDateNew(@Param("courseDate") String courseDate,@Param("courseUuid") String courseUuid);
    int countSuccessCourseByTeacherUuid(String teacherUuid);

    double findClassTimeByTeacherUuidAndMonthDate(@Param("teacherUuid") String teacherUuid, @Param("monthDate") String monthDate);

    int countCourseTeacherByMonthDate(String monthDate);

    int countLessClassTimeTeacherByMonthDate(@Param("classTime") double classTime, @Param("monthDate") String monthDate);

    List<CourseDto> findByByTeacherUuidAndCourseDate2(@Param("teacherUuid") String teacherUuid, @Param("courseDate") String courseDate);

    List<CourseDto> findByCourseDate(String courseDate);

    double findClassTimeByLeadsUuidAndMonth(@Param("leadsUuid") String leadsUuid, @Param("monthDate") String monthDate);

    int countCourseLeadsByMonthDate(String monthDate);

    int countLessClassTimeLeadsByMonthDate(@Param("classTime") double classTime, @Param("monthDate") String monthDate);

    int countNoJoinClassCountByMonthDate(@Param("leadsUuid") String leadsUuid, @Param("monthDate") String monthDate);

    int countCourseByLeadsUuidByMonthDate(@Param("leadsUuid") String leadsUuid, @Param("monthDate") String monthDate);

    List<CourseDto> findByLeadsUuidAndCourseDate(@Param("leadsUuid") String leadsUuid, @Param("courseDate") String courseDate);

    List<CourseDto> findNoEndCourseListByLeadsUuidAndCourseDate(@Param("leadsUuid") String leadsUuid, @Param("courseDate") String courseDate);

    List<String> findCourseDateByLeadsUuidAndYearMonth(@Param("leadsUuid") String leadsUuid, @Param("yearMonth") String yearMonth);
    List<TodayCourseEntity> selectTodayCourse(TodayCourseVo vo);
    int countTodayCourse(TodayCourseVo vo);
    //只返回有回放的
    CpCourse selectNormalBack(String uuid);
    /**
     * 教师上课记录列表（一对一）
     * 不修改原方法原则，新增
     * @param teacherUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @return
     */
    List<CourseDto> findTeacherCourseRecordV1ListNew(@Param("teacherUuid") String teacherUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("courseType") Integer courseType);
    /**
     * 学生上课记录列表（一对一）
     * 不修改原方法原则，新增此方法
     * @param leadsUuid
     * @param subject
     * @param startDate
     * @param endDate
     * @return
     */
    List<CourseDto> findStudentCourseRecordV1ListNew(@Param("leadsUuid") String leadsUuid, @Param("subject") String subject, @Param("startDate") String startDate, @Param("endDate") String endDate);
    /**
     * 学生上课记录列表（一对一）
     * 一天天的查
     * @return
     */
    List<CourseDto> findTeacherCourseRecordV1OneDay(@Param("teacherUuid") String teacherUuid,@Param("subject") String subject, @Param("courseDate") String courseDate, @Param("courseType") Integer courseType);
    String findMinDate(@Param("teacherUuid") String teacherUuid,@Param("leadsUuid") String leadsUuid);
}