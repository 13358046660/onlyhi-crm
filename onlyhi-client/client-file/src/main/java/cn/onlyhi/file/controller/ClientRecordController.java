package cn.onlyhi.file.controller;

import cn.onlyhi.client.po.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.util.*;
import cn.onlyhi.file.request.RestoreRecordRequest;
import cn.onlyhi.file.util.CourseUtilRefactoring;
import cn.onlyhi.file.vo.CourseDateVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

import static cn.onlyhi.common.constants.Constants.*;

/**
 * @Author wqz
 * <p>
 * 手动恢复进出房间记录，恢复class_record上课记录表
 * Created by wqz on 2018/07/27.
 */
@RestController
@RequestMapping("/client/record")
public class ClientRecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRecordController.class);

    @Autowired
    private ClassMateService classMateService;
    @Autowired
    private ClassRecordService classRecordService;
    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private MessageService messageService;

    /**
     * 恢复没有进出房间记录的，有记录的不更新
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/restoreRecord")
    public ResponseEntity<Response> restoreRecord(RestoreRecordRequest request) throws Exception {
        try {
            String yesterday = DateFormatUtils.format(System.currentTimeMillis() - 24 * 60 * 60 * 1000, "yyyy-MM-dd");
            String dateDir = "";
            List<CpCourse> cpCourseList = null;
            File recordLogsDir = null;
            LOGGER.info("restoreType:" + request.getRestoreType());
            if (Objects.equals(request.getRestoreType(), 1) || Objects.equals(request.getRestoreType(), 2) || Objects.equals(request.getRestoreType(), 3)) {
                if (StringUtils.isNotEmpty(request.getCourseDate()) && Objects.equals(request.getRestoreType(), 2)) {
                    //指定课程日期
                    dateDir = request.getCourseDate().replace("-", "");
                    cpCourseList = cpCourseService.findStartedCourseByCourseDateNew(request.getCourseDate(), null);
                } else if (Objects.equals(request.getRestoreType(), 1)) {
                    //默认统计昨天的
                    dateDir = yesterday.replace("-", "");
                    cpCourseList = cpCourseService.findStartedCourseByCourseDate(yesterday);
                } else if (StringUtils.isNotEmpty(request.getCourseDate()) && Objects.equals(request.getRestoreType(), 3)) {
                    //指定课程日期及课程id
                    dateDir = request.getCourseDate().replace("-", "");
                    cpCourseList = cpCourseService.findStartedCourseByCourseDateNew(request.getCourseDate(), request.getCourseUuid());
                }
                recordLogsDir = new File(BASEPATH + dateDir);
                if (!recordLogsDir.exists()) {
                    LOGGER.info("音频录制目录不存在！");
                }
            } else if (Objects.equals(request.getRestoreType(), 4)) {
                cpCourseList = cpCourseService.findStartedCourseByCourseDateNew(null, null);
                LOGGER.info("cpCourseList:" + cpCourseList.size());
            }

            //需统计的学生与老师的课时和上课进出记录
            List<CourseDateVo> courseUuidList_ks = new ArrayList<>();
            for (CpCourse cpCourse : cpCourseList) {
                CourseDateVo courseDateVo = new CourseDateVo();
                String courseUuid = cpCourse.getUuid();

                ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
                if (classRoom != null) {
                    //更新全部时，dateDir页面不传，需要查询record记录为空的所有dateDir
                    dateDir = cpCourse.getCourseDate().replace("-", "");
                    String classRoomUuid = classRoom.getClassRoomUuid();
                    List<ClassRecord> classRecordList = classRecordService.findByClassRoomUuid(classRoomUuid);
                    if (classRecordList == null || classRecordList.size() == 0) {
                        recordLogsDir = new File(BASEPATH + dateDir);
                        courseDateVo.setCourseId(courseUuid);
                        courseDateVo.setRecordLogsDir(recordLogsDir);
                        courseDateVo.setRecordId(classRoom.getRecordId());
                        courseUuidList_ks.add(courseDateVo);
                    }
                }
            }
            LOGGER.info("courseUuidList_ks.size={}", courseUuidList_ks.size());
            if (courseUuidList_ks.size() > 0) {
                for (CourseDateVo vo : courseUuidList_ks) {
                    if (Objects.equals(request.getRestoreType(), 4)) {
                        CourseUtilRefactoring.parseAndStatisticsClassRecordDBDataByThread(vo.getCourseId(), vo.getRecordLogsDir(), classMateService, classRecordService, classRoomService, cpCourseService, messageService, vo.getRecordId());
                    } else {
                        CourseUtilRefactoring.parseAndStatisticsClassRecordDBDataByThread(vo.getCourseId(), recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService, messageService, vo.getRecordId());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success());
    }
}
