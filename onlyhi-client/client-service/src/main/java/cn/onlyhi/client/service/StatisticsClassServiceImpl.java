package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassStatisticsMapper;
import cn.onlyhi.client.dto.ClassOutPutExcel;
import cn.onlyhi.client.dto.StatisticsClassVo;
import cn.onlyhi.client.po.ClassStatistics;
import cn.onlyhi.client.po.StatisticsClass;
import cn.onlyhi.common.util.ExportUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsClassServiceImpl implements StatisticsClassService {
    @Autowired
    private ClassStatisticsMapper classStatisticsMapper;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StatisticsClassServiceImpl.class);

    @Override
    public boolean statisticsClass(String today) {
        try {
            List<StatisticsClass> classList = classStatisticsMapper.statisticsClass(today);
            if (classList.size() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Map<String, List<StatisticsClass>> map = new HashMap<>();
                //输出对象集合
                List<ClassOutPutExcel> outPutList = new ArrayList<>();
                for (StatisticsClass obj : classList) {
                    if (map.containsKey(obj.getRoomUuid())) {
                        map.get(obj.getRoomUuid()).add(obj);
                    } else {
                        List<StatisticsClass> sameRoomIdList = new ArrayList<>();
                        sameRoomIdList.add(obj);
                        map.put(obj.getRoomUuid(), sameRoomIdList);
                    }
                }
                List<ClassStatistics> classStatisticsList = new ArrayList<>();
                //遍历map，分别取老师与学生最早进入房间时间及最晚退出房间时间
                for (Map.Entry<String, List<StatisticsClass>> entry : map.entrySet()) {
                    List<StatisticsClass> sameRoomList = entry.getValue();
                    Collections.sort(sameRoomList, new Comparator<StatisticsClass>() {
                        @Override
                        public int compare(StatisticsClass o1, StatisticsClass o2) {
                            String time1 = o1.getStudentRecordTime();
                            String time2 = o2.getStudentRecordTime();
                            Date date1;
                            Date date2;
                            try {
                                date1 = sdf.parse(time1);
                                date2 = sdf.parse(time2);
                                return date1.compareTo(date2);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage());
                                return 0;
                            }
                        }
                    });
                    //去重同一房间多个老师与学生的进出记录，取最早进入时间，最晚退出时间
                    if (sameRoomList.size() > 0) {
                        StatisticsClass interRecord = sameRoomList.get(0);
                        StatisticsClass outerRecord = sameRoomList.get(sameRoomList.size() - 1);
                        //写入新表用于页面查看或导出
                        ClassStatistics classStatistics = new ClassStatistics();
                        classStatistics.setStudentName(interRecord.getStudentName());
                        classStatistics.setGrade(interRecord.getGrade());
                        classStatistics.setTeacherName(interRecord.getTeacherName());
                        classStatistics.setCourseDate(interRecord.getCourseDate());
                        classStatistics.setTeacherFirstTime(interRecord.getTeacherEnterTime());
                        classStatistics.setTeacherLastTime(outerRecord.getTeacherOutTime());
                        classStatistics.setStudentFirstTime(interRecord.getStudentEnterTime());
                        classStatistics.setStudentLastTime(outerRecord.getStudentOutTime());
                        classStatistics.setCommonLength(interRecord.getCommonTime());
                        classStatistics.setTeacherLength(interRecord.getTeacherTime());
                        classStatistics.setStudentLength(interRecord.getStudentTime());
                        classStatistics.setRoomUuid(interRecord.getRoomUuid());
                        classStatisticsList.add(classStatistics);
                    }
                }
                //不要在for循环insert，用批量写表
                if (classStatisticsList.size() > 0) {
                    saveClass(classStatisticsList);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<ClassStatistics> findClass(StatisticsClassVo vo) {
        List<ClassStatistics> list = classStatisticsMapper.findClass(vo);
        //点击导出
        if (Objects.equals(vo.getIsExport(), 1)) {
            List<Map<String, String>> classMapList = new ArrayList<>();
            Map<String, String> classMap = new HashMap<>();
            for (ClassStatistics obj : list) {
                classMap = new HashMap<>();
                classMap.put("studentName", obj.getStudentName());
                classMap.put("grade", obj.getGrade());
                classMap.put("teacherName", obj.getTeacherName());
                classMap.put("courseDate", obj.getCourseDate());
                classMap.put("startTime", obj.getStartTime());
                classMap.put("endTime", obj.getEndTime());
                classMap.put("teacherFirstTime", obj.getTeacherFirstTime().toString());
                classMap.put("teacherLastTime", obj.getTeacherLastTime().toString());
                classMap.put("studentFirstTime", obj.getStudentFirstTime().toString());
                classMap.put("studentLastTime", obj.getStudentLastTime().toString());
                classMap.put("commonLength", obj.getCommonLength().toString());
                classMap.put("teacherLength", obj.getTeacherLength().toString());
                classMap.put("studentLength", obj.getStudentLength().toString());
                classMap.put("roomUuid", obj.getRoomUuid());
                classMapList.add(classMap);
            }
            Map<String, String> classHeadMap = new HashMap<>();
            classHeadMap.put("studentName", "学生名");
            classHeadMap.put("grade", "年级");
            classHeadMap.put("teacherName", "老师名");
            classHeadMap.put("courseDate", "课程日期");
            classHeadMap.put("startTime", "开始时间");
            classHeadMap.put("endTime", "结束时间");
            classHeadMap.put("teacherFirstTime", "老师首次进房间时间");
            classHeadMap.put("teacherLastTime", "老师最后离开房间时间");
            classHeadMap.put("studentFirstTime", "学生首次进房间时间");
            classHeadMap.put("studentLastTime", "学生最后离开房间时间");
            classHeadMap.put("commonLength", "共同时长");
            classHeadMap.put("teacherLength", "老师时长");
            classHeadMap.put("studentLength", "学生时长");
            classHeadMap.put("roomUuid", "房间id");


            Map<Map<String, String>, List<Map<String, String>>> map = new HashMap<>();
            map.put(classHeadMap, classMapList);

            List<String> sheetNames = Arrays.asList("课耗统计");
            ExportUtil.wirteExcelFile2(map, sheetNames, null);

        }
        return list;
    }

    @Override
    public int updateClassById(Integer id) {
        int count = 0;
        try {
            count = classStatisticsMapper.updateClassById(id);
            if (count > 0) {
                LOGGER.info("更新课耗成功.");
            } else {
                LOGGER.info("更新课耗失败.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return count;
    }

    @Override
    public Map<Map<String, String>, List<Map<String, String>>> exportClass(String courseStartDate) {
        Map<Map<String, String>, List<Map<String, String>>> outMap = new HashMap<>();
        try {
            List<ClassStatistics> classList = classStatisticsMapper.exportClass(courseStartDate);
            if (classList.size() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //输出对象集合
                List<ClassOutPutExcel> outPutList = new ArrayList<>();
                Map<String, List<ClassStatistics>> map = new HashMap<>();
                for (ClassStatistics obj : classList) {
                    if (map.containsKey(obj.getRoomId())) {
                        map.get(obj.getRoomId()).add(obj);
                    } else {
                        List<ClassStatistics> sameRoomIdList = new ArrayList<>();
                        sameRoomIdList.add(obj);
                        map.put(obj.getRoomId(), sameRoomIdList);
                    }
                }
                //遍历map，分别取老师与学生最早进入房间时间及最晚退出房间时间
                for (Map.Entry<String, List<ClassStatistics>> entry : map.entrySet()) {
                    List<ClassStatistics> sameRoomList = entry.getValue();
                    Collections.sort(sameRoomList, new Comparator<ClassStatistics>() {
                        @Override
                        public int compare(ClassStatistics o1, ClassStatistics o2) {
                            String time1 = o1.getStudentTime();
                            String time2 = o2.getStudentTime();
                            Date date1;
                            Date date2;
                            try {
                                date1 = sdf.parse(time1);
                                date2 = sdf.parse(time2);
                                return date1.compareTo(date2);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage());
                                return 0;
                            }
                        }
                    });
                    //去重同一房间老师与学生的多个进出记录，取最早进入时间，最晚退出时间
                    if (sameRoomList.size() > 0) {

                        //for (int k = 0; k < sameRoomList.size(); k++) {

                        ClassStatistics interRecord = sameRoomList.get(0);
                        // if (k - 1 > 0) {
                        ClassStatistics outerRecord = sameRoomList.get(sameRoomList.size() - 1);

                        //写入新表用于页面查看或导出
                        ClassOutPutExcel opr = new ClassOutPutExcel();

                        opr.setStuName(interRecord.getStudentName());
                        opr.setGrade(interRecord.getGrade());
                        opr.setTcName(interRecord.getTeacherName());
                        opr.setCourseDate(interRecord.getCourseDate());
                        opr.setStartTime(interRecord.getStartTime());
                        opr.setEndTime(interRecord.getEndTime());
                        opr.setTcEnterTime(interRecord.getTeacherTime());
                        opr.setTcOuterTime(outerRecord.getTeacherTime());
                        opr.setStuEnterTime(interRecord.getStudentTime());
                        opr.setStuOuterTime(outerRecord.getStudentTime());
                        opr.setRealLength(interRecord.getCommonLength());
                        opr.setTeacherLength(interRecord.getTeacherLength());
                        opr.setLeadsLength(interRecord.getStudentLength());
                        outPutList.add(opr);
                        // }
                        // }
                    }
                }
                List<Map<String, String>> classMapList = new ArrayList<>();
                Map<String, String> classMap = new HashMap<>();
                for (ClassOutPutExcel obj : outPutList) {
                    classMap = new HashMap<>();
                    classMap.put("studentName", obj.getStuName());
                    classMap.put("grade", obj.getGrade());
                    classMap.put("teacherName", obj.getTcName());
                    classMap.put("courseDate", obj.getCourseDate());
                    classMap.put("startTime", obj.getStartTime());
                    classMap.put("endTime", obj.getEndTime());
                    if (obj.getTcEnterTime() != null) {
                        classMap.put("teacherFirstTime", obj.getTcEnterTime().toString());
                    }
                    if (obj.getTcOuterTime() != null) {
                        classMap.put("teacherLastTime", obj.getTcOuterTime().toString());
                    }
                    if (obj.getStuEnterTime() != null) {
                        classMap.put("studentFirstTime", obj.getStuEnterTime().toString());
                    }
                    if (obj.getStuOuterTime() != null) {
                        classMap.put("studentLastTime", obj.getStuOuterTime().toString());
                    }
                    if (obj.getRealLength() != null) {
                        classMap.put("commonLength", obj.getRealLength().toString());
                    }
                    if (obj.getTeacherLength() != null) {
                        classMap.put("teacherLength", obj.getTeacherLength().toString());
                    }
                    if (obj.getLeadsLength() != null) {
                        classMap.put("studentLength", obj.getLeadsLength().toString());
                    }
                    classMapList.add(classMap);
                }
                Map<String, String> classHeadMap = new LinkedHashMap<>();
                classHeadMap.put("studentName", "学生名");
                classHeadMap.put("grade", "年级");
                classHeadMap.put("teacherName", "老师名");
                classHeadMap.put("courseDate", "课程日期");
                classHeadMap.put("startTime", "开始时间");
                classHeadMap.put("endTime", "结束时间");
                classHeadMap.put("teacherFirstTime", "老师首次进房间时间");
                classHeadMap.put("teacherLastTime", "老师最后离开房间时间");
                classHeadMap.put("studentFirstTime", "学生首次进房间时间");
                classHeadMap.put("studentLastTime", "学生最后离开房间时间");
                classHeadMap.put("commonLength", "共同时长");
                classHeadMap.put("teacherLength", "老师时长");
                classHeadMap.put("studentLength", "学生时长");
                outMap.put(classHeadMap, classMapList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return outMap;
    }

    public void saveClass(List<ClassStatistics> list) {
        int count = classStatisticsMapper.batchSaveClass(list);
        if (count > 0) {
            LOGGER.info("已写入课耗表.");
        } else {
            LOGGER.info("写入课耗表失败.");
        }
    }
}