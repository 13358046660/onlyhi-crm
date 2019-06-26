package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassMateMapper;
import cn.onlyhi.client.dao.ClassRoomMapper;
import cn.onlyhi.client.dao.CpCourseMapper;
import cn.onlyhi.client.dto.*;
import cn.onlyhi.client.po.ClassMate;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.CpCourse;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.StringUtil;
import cn.onlyhi.common.util.UUIDUtil;
import cn.onlyhi.common.util.agora.DynamicKey4;
import cn.onlyhi.common.util.agora.SignalingToken;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cn.onlyhi.common.constants.Constants.PERSONOFCOURSE_KEY;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.*;
import static cn.onlyhi.common.util.FileUtils.getCommChannelId;
import static cn.onlyhi.common.util.FileUtils.getSignallingChannelId;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/7/6.
 */
@Service
public class CpCourseServiceImpl implements CpCourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpCourseServiceImpl.class);
    @Autowired
    private CpCourseMapper cpCourseMapper;
    @Autowired
    private ClassRoomMapper classRoomMapper;
    @Autowired
    private ClassMateMapper classMateMapper;
    @Autowired
    protected RedisService redisService;
    /**
     * 课程id
     */
    private String uuid;
    /**
     * 房间id
     */
    private Integer id;
    @Value("${phpUrl}")
    private String url;

    public CpCourseServiceImpl() {
    }

    public CpCourseServiceImpl(String uuid, Integer id) {
        this.uuid = uuid;
        this.id = id;
    }

    @Override
    public int save(CpCourse cpCourse) {
        return cpCourseMapper.insertSelective(cpCourse);
    }

    @Override
    public CpCourse findByUuid(String uuid) {
        return cpCourseMapper.selectByUuid(uuid);
    }

    @Override
    public int update(CpCourse cpCourse) {
        return cpCourseMapper.updateByUuidSelective(cpCourse);
    }

    @Override
    public List<CpCourse> selectStudentNoPushMessageCourseList(String currentDate, String currentTime) {
        return cpCourseMapper.selectStudentNoPushMessageCourseList(currentDate, currentTime);
    }

    @Override
    public int updatePushStatus(List<String> leadsUuidList) {
        return cpCourseMapper.updatePushStatus(leadsUuidList);
    }

    @Override
    public List<CourseInfoDto> findCourseInfoByLeadsUuid(String leadsUuid) {
        return cpCourseMapper.selectCourseInfoByLeadsUuid(leadsUuid);
    }

    @Override
    public CourseDto findCourseDetailsByUuid(String courseUuid) {
        return cpCourseMapper.findCourseDetailsByUuid(courseUuid);
    }

    @Override
    public long countTeacherCourseRecordList(String teacherUuid, String subject, String startDate, String endDate) {
        return cpCourseMapper.countTeacherCourseRecordList(teacherUuid, subject, startDate, endDate);
    }

    @Override
    public List<CourseRecordDto> findTeacherCourseRecordList(String teacherUuid, String subject, String startDate, String endDate, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findTeacherCourseRecordList(teacherUuid, subject, startDate, endDate, startSize, pageSize);
    }

    @Override
    public double findClassTimeByLeadsUuid(String leadsUuid) {
        return cpCourseMapper.findClassTimeByLeadsUuid(leadsUuid);
    }

    @Override
    public int countCourseLeads() {
        return cpCourseMapper.countCourseLeads();
    }

    @Override
    public int countLessClassTimeLeads(double classTime) {
        return cpCourseMapper.countLessClassTimeLeads(classTime);
    }

    @Override
    public int countNoJoinClassCount(String leadsUuid) {
        return cpCourseMapper.countNoJoinClassCount(leadsUuid);
    }

    @Override
    public int countCourseByLeadsUuid(String leadsUuid) {
        return cpCourseMapper.countCourseByLeadsUuid(leadsUuid);
    }

    @Override
    public double findClassTimeByLeadsUuidAndCourseLevel(String leadsUuid, int courseLevel) {
        return cpCourseMapper.findClassTimeByLeadsUuidAndCourseLevel(leadsUuid, courseLevel);
    }

    @Override
    public List<CourseDto> findStudentNoEndCourseListByLeadsUuidAndCourseDate(String leadsUuid, String courseDate) {
        return cpCourseMapper.findStudentNoEndCourseListByLeadsUuidAndCourseDate(leadsUuid, courseDate);
    }

    @Override
    public double findClassTimeByTeacherUuid(String teacherUuid) {
        return cpCourseMapper.findClassTimeByTeacherUuid(teacherUuid);
    }

    @Override
    public int countCourseTeacher() {
        return cpCourseMapper.countCourseTeacher();
    }

    @Override
    public int countLessClassTimeTeacher(double classTime) {
        return cpCourseMapper.countLessClassTimeTeacher(classTime);
    }

    @Override
    public int countTeacherNoJoinClassCount(String teacherUuid) {
        return cpCourseMapper.countTeacherNoJoinClassCount(teacherUuid);
    }

    @Override
    public int countCourseByTeacherUuid(String teacherUuid) {
        return cpCourseMapper.countCourseByTeacherUuid(teacherUuid);
    }

    @Override
    public double findClassTimeByTeacherUuidAndCourseLevel(String teacherUuid, int courseLevel) {
        return cpCourseMapper.findClassTimeByTeacherUuidAndCourseLevel(teacherUuid, courseLevel);
    }

    @Override
    public List<CourseDto> findStudentNoEndCourseListByTeacherUuidAndCourseDate(String teacherUuid, String courseDate) {
        return cpCourseMapper.findStudentNoEndCourseListByTeacherUuidAndCourseDate(teacherUuid, courseDate);
    }

    @Override
    public long countTeacherNoEndCourse(String teacherUuid) {
        return cpCourseMapper.countTeacherNoEndCourse(teacherUuid);
    }

    @Override
    public List<CourseDto> findTeacherNoEndCourse(String teacherUuid, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findTeacherNoEndCourse(teacherUuid, startSize, pageSize);
    }

    @Override
    public CpCourse findStartByUuid(String courseUuid) {
        return cpCourseMapper.findStartByUuid(courseUuid);
    }

    @Override
    public List<CourseDto> findNoEndCourseByLeadsUuid(String leadsUuid, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndCourseByLeadsUuid(leadsUuid, startSize, pageSize);
    }

    @Override
    public List<CourseDto> findTeacherNoEndV1Course(String teacherUuid, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findTeacherNoEndV1Course(teacherUuid, startSize, pageSize);
    }

    @Override
    public List<CourseDto> findTeacherCourseRecordV1List(String teacherUuid, String subject, String startDate, String endDate, Integer courseType, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findTeacherCourseRecordV1List(teacherUuid, subject, startDate, endDate, courseType, startSize, pageSize);
    }

    @Override
    public List<CourseDto> findStudentCourseRecordV1List(String leadsUuid, String subject, String startDate, String endDate, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findStudentCourseRecordV1List(leadsUuid, subject, startDate, endDate, startSize, pageSize);
    }

    @Override
    public long countStudentCourseRecordList(String leadsUuid, String subject, String startDate, String endDate) {
        return cpCourseMapper.countStudentCourseRecordList(leadsUuid, subject, startDate, endDate);
    }

    @Override
    public List<CourseDto> findStudentCourseRecordList(String leadsUuid, String subject, String startDate, String endDate, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findStudentCourseRecordList(leadsUuid, subject, startDate, endDate, startSize, pageSize);
    }

    @Override
    public boolean countNoEndCourseByUuid(String courseUuid) {
        int i = cpCourseMapper.countNoEndCourseByUuid(courseUuid);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<CourseDto> findNoEndCourse(String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndCourse(subject, startDate, endDate, courseType, userName, startSize, pageSize);
    }

    @Override
    @Transactional
    public int finishClass(String courseUuid, String userUuid, long currentTimeMillis, String userType) {
        int i = 0;
        CpCourse cpCourse = cpCourseMapper.selectByUuid(courseUuid);
        if (cpCourse == null) {
            return -1;
        }
        if (Objects.equals(cpCourse.getCourseType(), 2)) {
            return 0;
        }
        if (TEACHER.name().equals(userType)) {
            if (Objects.equals(cpCourse.getClassStatus(), 0)) {
                return -2;
            }
            int flag = cpCourseMapper.updateClassStatus(courseUuid, 1);
            if (flag > 0) {
                //更新下课时间
                ClassRoom classRoom = classRoomMapper.selectByCourseUuid(courseUuid);
                classRoom.setOutRoomTime(currentTimeMillis);
                classRoom.setUpdateUid(userUuid);
                i = classRoomMapper.updateByUuidSelective(classRoom);
            }
        } else {
            if (STUDENT.name().equals(userType)) {
                if (Objects.equals(cpCourse.getStudentClassStatus(), 0)) {
                    return -2;
                }
                cpCourse.setStudentClassStatus(1);
            } else if (CC.name().equals(userType)) {
                if (Objects.equals(cpCourse.getCcClassStatus(), 0)) {
                    return -2;
                }
                cpCourse.setCcClassStatus(1);
            } else if (CR.name().equals(userType)) {
                if (Objects.equals(cpCourse.getCrClassStatus(), 0)) {
                    return -2;
                }
                cpCourse.setCrClassStatus(1);
            }
            i = cpCourseMapper.updateByUuidSelective(cpCourse);
        }
        return i;
    }
    @Override
    @Transactional(rollbackFor=Exception.class)
    public int finishClassPhp(String courseUuid, String userUuid, long currentTimeMillis, String userType,String phpToken) {
        int i = 0;
        CpCourse cpCourse=null;
        try{
            String phpUrl =url.concat("client/course/detail");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Accept", "application/json");
            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            LOGGER.info("课程详情 result={}",result+",课程id={}",courseUuid);
            Response phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            cpCourse=JSON.parseObject(object.toString(),CpCourse.class);

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        if (cpCourse == null) {
            return -1;
        }
        if (Objects.equals(cpCourse.getCourseType(), 2)) {
            return 0;
        }
        if (TEACHER.name().equals(userType)) {
            if (Objects.equals(cpCourse.getClassStatus(), 0)) {
                return -2;
            }
            cpCourse.setClassStatus(1);
        } else {
            if (STUDENT.name().equals(userType)) {
                if (Objects.equals(cpCourse.getClassStatus(), 0)) {
                    return -2;
                }
                cpCourse.setStudentClassStatus(1);
            } else if (CC.name().equals(userType)) {
                if (Objects.equals(cpCourse.getClassStatus(), 0)) {
                    return -2;
                }
                cpCourse.setCcClassStatus(1);
            } else if (CR.name().equals(userType)) {
                if (Objects.equals(cpCourse.getClassStatus(), 0)) {
                    return -2;
                }
                cpCourse.setCrClassStatus(1);
            }
        }
        try {
            String phpUrl = url.concat("client/classStatus/update");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Accept", "application/json");
            post.setHeader("Authorization", "Bearer ".concat(phpToken));
            JSONObject param= new JSONObject();

            if(TEACHER.name().equals(userType))
            {
                param.put("classStatus",1);

            }else if (STUDENT.name().equals(userType)) {
                param.put("studentClassStatus",1);
            } else if (CC.name().equals(userType)) {
                param.put("ccClassStatus",1);
            } else if (CR.name().equals(userType)) {
                param.put("crClassStatus", 1);
            }

            param.put("courseUuid", courseUuid);
            param.put("userType", userType);

            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            LOGGER.info("下课更新课程状态入参 ={}",JSON.toJSONString(param));

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            LOGGER.info("下课更新课程状态 结果={}",result);
            Response phpResponse = JSON.parseObject(result, Response.class);

            if (Objects.equals(SUCCESS.getCode(),phpResponse.getCode())) {
                LOGGER.info("php 更新下课状态完成={}",courseUuid);
                i =1;
                if(TEACHER.name().equals(userType)){
                    ClassRoom classRoom = classRoomMapper.selectByCourseUuid(courseUuid);
                    classRoom.setOutRoomTime(currentTimeMillis);
                    classRoom.setUpdateUid(userUuid);
                    //只有老师更新下课时间（以老师下课时间为准）
                    i = classRoomMapper.updateByUuidSelective(classRoom);
                }
            }else {
                LOGGER.error("php 更新下课状态失败");
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return i;
    }
    @Override
    @Transactional
    public ClassRoom startClass(String courseUuid, String userUuid, String userType, String userName, int agoraUid, long currentTimeMillis) {
        LOGGER.info("开始上课.课程id：" + courseUuid);
        ClassRoom classRoom = classRoomMapper.selectByCourseUuid(courseUuid);
        String classRoomUuid;
        int flag = 0;
        int classMateFlag = 0;
        if (classRoom == null) {
            //生成通信频道Id，信令频道Id
            classRoom = new ClassRoom();
            classRoom.setCommChannelId(getCommChannelId(courseUuid));
            classRoom.setSignallingChannelId(getSignallingChannelId(courseUuid));
            int newRecordId = Math.abs(getRecordIdNew(courseUuid, classRoom.getId()));
            //LOGGER.info("newRecordId={}" + newRecordId);
            classRoom.setRecordId(newRecordId);
            classRoomUuid = UUIDUtil.randomUUID2();
            classRoom.setClassRoomUuid(classRoomUuid);
            classRoom.setCourseUuid(courseUuid);
            classRoom.setEnterRoomTime(currentTimeMillis);
            classRoom.setCreateUid(userUuid);
            flag = classRoomMapper.insertSelective(classRoom);
            if (flag > 0) {
                LOGGER.info("存入房间信息成功...");
            }
        } else {
            classRoomUuid = classRoom.getClassRoomUuid();
            classRoom.setUpdateUid(userUuid);
            flag = classRoomMapper.updateByUuidSelective(classRoom);
            if (flag > 0) {
                LOGGER.info("更新房间信息成功...");
            }
        }
        if (flag > 0) {
            //保存进入房间的人的信息（只记录一次）
            ClassMate classMate = classMateMapper.findByClassRoomUuidAndUserType(classRoomUuid, userType);
            if (classMate == null) {
                classMate = new ClassMate();
                classMate.setClassMateUuid(UUIDUtil.randomUUID2());
                classMate.setClassRoomUuid(classRoomUuid);
                classMate.setUserType(userType);
                classMate.setUserUuid(userUuid);
                classMate.setAgoraUid(agoraUid);
                classMate.setUserName(userName);
                classMate.setCreateUid(userUuid);
                classMateFlag = classMateMapper.insertSelective(classMate);
                if (classMateFlag > 0) {
                    LOGGER.info("存进入房间的人的信息成功...");
                }
            }
        }
        //不能用classMateFlag>0，如果用，第二次再进入教室时更新不了课程状态
        if (flag > 0) {
            CpCourse cpCourse = new CpCourse();
            cpCourse.setUuid(courseUuid);
            //防止家长或员工进入房间没有参数导致update异常
            cpCourse.setStatus(true);
            //更新课程状态（只有教师进入时才更新）
            if (TEACHER.name().equals(userType)) {
                cpCourse.setClassStatus(2);
            } else if (STUDENT.name().equals(userType)) {
                cpCourse.setStudentClassStatus(2);
            } else if (CC.name().equals(userType)) {
                cpCourse.setCcClassStatus(2);
            } else if (CR.name().equals(userType)) {
                cpCourse.setCrClassStatus(2);
            }
            int flag1 = cpCourseMapper.updateByUuidSelective(cpCourse);
            if (flag1 > 0) {
                LOGGER.info("/更新课程状态成功...");
            }
        }
        return classRoom;
    }

    @Override
    @Transactional
    public ClassRoom startClassPhp(String courseUuid, String userUuid, String userType, String userName, int agoraUid, long currentTimeMillis,String phpToken) {
        LOGGER.info("startClassPhp.课程id：" + courseUuid);
        ClassRoom classRoom = classRoomMapper.selectByCourseUuid(courseUuid);
        String classRoomUuid;
        int flag = 0;
        int classMateFlag = 0;
        if (classRoom == null) {
            //生成通信频道Id，信令频道Id
            classRoom = new ClassRoom();
            classRoom.setCommChannelId(getCommChannelId(courseUuid));
            classRoom.setSignallingChannelId(getSignallingChannelId(courseUuid));
            int newRecordId = Math.abs(getRecordIdNew(courseUuid, classRoom.getId()));
            classRoom.setRecordId(newRecordId);
            classRoomUuid = UUIDUtil.randomUUID2();
            classRoom.setClassRoomUuid(classRoomUuid);
            classRoom.setCourseUuid(courseUuid);
            classRoom.setEnterRoomTime(currentTimeMillis);
            classRoom.setCreateUid(userUuid);
            flag = classRoomMapper.insertSelective(classRoom);
            if (flag > 0) {
                LOGGER.info("更新房间信息成功...");
            }

        } else {
            classRoomUuid = classRoom.getClassRoomUuid();
            classRoom.setUpdateUid(userUuid);
            flag = classRoomMapper.updateByUuidSelective(classRoom);
            if (flag > 0) {
                LOGGER.info("更新房间信息成功...");
            }
        }
        if (flag > 0) {
            ClassMate classMate = classMateMapper.findByClassRoomUuidAndUserType(classRoomUuid, userType);
            if (classMate == null) {
                classMate = new ClassMate();
                classMate.setClassMateUuid(UUIDUtil.randomUUID2());
                classMate.setClassRoomUuid(classRoomUuid);
                classMate.setUserType(userType);
                classMate.setUserUuid(userUuid);
                classMate.setAgoraUid(agoraUid);
                classMate.setUserName(userName);
                classMate.setCreateUid(userUuid);
                classMateFlag = classMateMapper.insertSelective(classMate);
                if (classMateFlag > 0) {
                    LOGGER.info("存进入房间的人的信息成功...");
                }else {
                    LOGGER.info("存进入房间的人的信息失败={}",courseUuid);
                }
            }
        }
        //不能用classMateFlag>0，如果用，第二次再进入教室时更新不了课程状态
        if (flag > 0) {
          Response phpResponse;
          try {
              String phpUrl = url.concat("client/classStatus/update");

              CloseableHttpClient client = HttpClients.createDefault();
              HttpPost post=new HttpPost(phpUrl);
              post.setHeader("Content-Type","application/json;charset=utf-8");
              post.setHeader("Authorization", "Bearer ".concat(phpToken));

              JSONObject param= new JSONObject();
              //防止家长或员工进入房间没有参数导致update异常
              //cpCourse.setStatus(true);
              //更新课程状态（只有教师进入时才更新）
              param.put("courseUuid", courseUuid);
              param.put("userType", userType);
              if (TEACHER.name().equals(userType)) {
                  param.put("classStatus", "2");
              } else if (STUDENT.name().equals(userType)) {
                  param.put("studentClassStatus", "2");
              } else if (CC.name().equals(userType)) {
                  param.put("ccClassStatus", "2");
              } else if (CR.name().equals(userType)) {
                  param.put("crClassStatus", "2");
              }

              LOGGER.info("client/classStatus/update 传给php={}",JSON.toJSONString(param));
              StringEntity stringEntity = new StringEntity(param.toString());
              post.setEntity(stringEntity);

              CloseableHttpResponse response = client.execute(post);

              HttpEntity entity=response.getEntity();
              String result= EntityUtils.toString(entity,"UTF-8");
              LOGGER.info("client/classStatus/update result ={}",result);
              phpResponse = JSON.parseObject(result, Response.class);

              if(!Objects.equals(CodeEnum.SUCCESS.getCode(),phpResponse.getCode())){
                  LOGGER.info("php课程状态更新失败");
              }
          }catch (Exception e){
              LOGGER.error(e.getMessage());
          }
        }
        return classRoom;
    }
    public int getRecordIdNew(String uuid, Integer id) {
        int recordId = hashCode(uuid, id);
        return recordId;
    }

    //重写hashCode方法
    public int hashCode(String uuid, Integer id) {
        final int prime = 31;
        int result = 1;
        //result= result+age
        result = prime * 1 + result;
        //result=result+name.hashCode()
        //name=null返回0，否则返回name.hashCode().
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode()) + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    //重写equals方法
    @Override
    public boolean equals(Object obj) {
        //提高效率
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        //提高代码健壮性,不是同一个类型就直接返回false,省得向下转型了
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        //向下转型
        CpCourseServiceImpl p = (CpCourseServiceImpl) obj;

        if (this.uuid == null) {
            if (p.uuid != null) {
                return false;
            }

        } else if (!this.uuid.equals(p.uuid)) {
            return false;
        }
        return true;
    }

    @Override
    public List<CourseDto> findNoEndCourseByPatriarchMonitor(String patriarchUuid, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndCourseByPatriarchMonitor(patriarchUuid, startSize, pageSize);
    }

    @Override
    public List<CourseDto> findNoEndCourseByQCMonitor(String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndCourseByQCMonitor(subject, startDate, endDate, courseType, userName, startSize, pageSize);
    }

    @Override
    public List<CourseDto> findNoEndCourseByCC(String userUuid, String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndCourseByCC(userUuid, subject, startDate, endDate, courseType, userName, startSize, pageSize);
    }

    @Override
    public List<CourseDto> findNoEndCourseByCR(String userUuid, String subject, String startDate, String endDate, Integer courseType, String userName, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndCourseByCR(userUuid, subject, startDate, endDate, courseType, userName, startSize, pageSize);
    }

    @Override
    public int countNoEndDebugCourseByCC(String userUuid) {
        return cpCourseMapper.countNoEndDebugCourseByCC(userUuid);
    }

    @Override
    public List<CourseDto> findNoEndDebugCourseByCC(String userUuid, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndDebugCourseByCC(userUuid, startSize, pageSize);
    }

    @Override
    public int countNoEndDebugCourseByCR(String userUuid) {
        return cpCourseMapper.countNoEndDebugCourseByCR(userUuid);
    }

    @Override
    public List<CourseDto> findNoEndDebugCourseByCR(String userUuid, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndDebugCourseByCR(userUuid, startSize, pageSize);
    }

    @Override
    public List<String> findCourseDateByTeacherUuidAndYearMonth(String teacherUuid, String yearMonth) {
        return cpCourseMapper.findCourseDateByTeacherUuidAndYearMonth(teacherUuid, yearMonth);
    }

    @Override
    public List<CourseDto> findByByTeacherUuidAndCourseDate(String teacherUuid, String courseDate) {
        return cpCourseMapper.findByByTeacherUuidAndCourseDate(teacherUuid, courseDate);
    }

    @Override
    public List<CourseDto> findNoEndCourseListByTeacherUuidAndCourseDate(String teacherUuid, String courseDate, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findNoEndCourseListByTeacherUuidAndCourseDate(teacherUuid, courseDate, startSize, pageSize);
    }

    @Override
    public List<CpCourse> findStartedCourseByCourseDate(String courseDate) {
        return cpCourseMapper.findStartedCourseByCourseDate(courseDate);
    }

    @Override
    public List<CpCourse> findStartedCourseByCourseDateNew(String courseDate, String courseUuid) {
        return cpCourseMapper.findStartedCourseByCourseDateNew(courseDate, courseUuid);
    }

    @Override
    public int countSuccessCourseByTeacherUuid(String teacherUuid) {
        return cpCourseMapper.countSuccessCourseByTeacherUuid(teacherUuid);
    }

    @Override
    public double findClassTimeByTeacherUuidAndMonthDate(String teacherUuid, String monthDate) {
        return cpCourseMapper.findClassTimeByTeacherUuidAndMonthDate(teacherUuid, monthDate);
    }

    @Override
    public int countCourseTeacherByMonthDate(String monthDate) {
        return cpCourseMapper.countCourseTeacherByMonthDate(monthDate);
    }

    @Override
    public int countLessClassTimeTeacherByMonthDate(double classTime, String monthDate) {
        return cpCourseMapper.countLessClassTimeTeacherByMonthDate(classTime, monthDate);
    }

    @Override
    public List<CourseDto> findByByTeacherUuidAndCourseDate2(String teacherUuid, String courseDate) {
        return cpCourseMapper.findByByTeacherUuidAndCourseDate2(teacherUuid, courseDate);
    }

    @Override
    public List<CourseDto> findByCourseDate(String courseDate) {
        return cpCourseMapper.findByCourseDate(courseDate);
    }

    @Override
    public double findClassTimeByLeadsUuidAndMonth(String leadsUuid, String monthDate) {
        return cpCourseMapper.findClassTimeByLeadsUuidAndMonth(leadsUuid, monthDate);
    }

    @Override
    public int countCourseLeadsByMonthDate(String monthDate) {
        return cpCourseMapper.countCourseLeadsByMonthDate(monthDate);
    }

    @Override
    public int countLessClassTimeLeadsByMonthDate(double classTime, String monthDate) {
        return cpCourseMapper.countLessClassTimeLeadsByMonthDate(classTime, monthDate);
    }

    @Override
    public int countNoJoinClassCountByMonthDate(String leadsUuid, String monthDate) {
        return cpCourseMapper.countNoJoinClassCountByMonthDate(leadsUuid, monthDate);
    }

    @Override
    public int countCourseByLeadsUuidByMonthDate(String leadsUuid, String monthDate) {
        return cpCourseMapper.countCourseByLeadsUuidByMonthDate(leadsUuid, monthDate);
    }

    @Override
    public List<CourseDto> findByLeadsUuidAndCourseDate(String leadsUuid, String courseDate) {
        return cpCourseMapper.findByLeadsUuidAndCourseDate(leadsUuid, courseDate);
    }

    @Override
    public List<CourseDto> findNoEndCourseListByLeadsUuidAndCourseDate(String leadsUuid, String courseDate) {
        return cpCourseMapper.findNoEndCourseListByLeadsUuidAndCourseDate(leadsUuid, courseDate);
    }

    @Override
    public List<String> findCourseDateByLeadsUuidAndYearMonth(String leadsUuid, String yearMonth) {
        return cpCourseMapper.findCourseDateByLeadsUuidAndYearMonth(leadsUuid, yearMonth);
    }

    @Override
    public Map<String, Object> selectTodayCourse(TodayCourseVo vo) {
        LOGGER.info("查询技术支持登陆后课程列表");
        Map<String, Object> map = new HashMap<>();
        int startSize = (vo.getPageNo() - 1) * vo.getPageSize();
        vo.setStartSize(startSize);
        List<TodayCourseEntity> courseList = cpCourseMapper.selectTodayCourse(vo);
        int total = cpCourseMapper.countTodayCourse(vo);
        map.put("list", courseList);
        map.put("total", total);
        return map;
    }

    @Override
    public CpCourse selectNormalBack(String uuid) {
        return cpCourseMapper.selectNormalBack(uuid);
    }

    @Override
    public List<CourseDto> findTeacherCourseRecordV1ListNew(String teacherUuid, String subject, String startDate, String endDate, Integer courseType) {
        //int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findTeacherCourseRecordV1ListNew(teacherUuid, subject, startDate, endDate, courseType);
    }

    @Override
    public List<CourseDto> findStudentCourseRecordV1ListNew(String leadsUuid, String subject, String startDate, String endDate) {
        //int startSize = (pageNo - 1) * pageSize;
        return cpCourseMapper.findStudentCourseRecordV1ListNew(leadsUuid, subject, startDate, endDate);
    }

    @Override
    public List<CourseDto> findTeacherCourseRecordV1OneDay(String teacherUuid, String subject, String courseDate, Integer courseType) {
        return cpCourseMapper.findTeacherCourseRecordV1OneDay(teacherUuid, subject, courseDate, courseType);
    }

    @Override
    public String findMinDate(String teacherUuid, String leadsUuid) {
        return cpCourseMapper.findMinDate(teacherUuid, leadsUuid);
    }

    @Override
    public ClassRoomVO enterRoom(ClassRoomVO classRoomVO) {
        try {
            CpCourse course = cpCourseMapper.selectByUuid(classRoomVO.getCourseUuid());
            if (!course.getStatus()) {
                classRoomVO.setInvalid_course("INVALID_COURSE");
            }
            CpCourse cpCourse = cpCourseMapper.findStartByUuid(classRoomVO.getCourseUuid());
            if (cpCourse == null) {
                classRoomVO.setCourse_no_starat("COURSE_NO_STARAT");
            }
            String courseDate = cpCourse.getCourseDate();
            String endTime = cpCourse.getEndTime();
            LOGGER.info("courseDate" + courseDate + "endTime" + endTime);
            ClassRoom room = classRoomMapper.selectByCourseUuid(classRoomVO.getCourseUuid());
            int overtime = 0;
            if (room != null) {
                overtime = room.getOvertime();
            }
            if ((DateUtils.parseDate(courseDate + " " + endTime, "yyyy-MM-dd HH:mm").getTime() + overtime * 60 * 1000) <= new Date().getTime()) {
                if (!isExistPersonOfCourse(classRoomVO.getCourseUuid())) {
                    LOGGER.info("房间{}还有人", classRoomVO.getCourseUuid());
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            }
            Integer studentClassStatus = cpCourse.getStudentClassStatus();
            if (TEACHER.name().equals(classRoomVO.getUserType())) {
                if (Objects.equals(cpCourse.getClassStatus(), 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            } else if (CC.name().equals(classRoomVO.getUserType())) {
                if (Objects.equals(cpCourse.getCcClassStatus(), 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            } else if (CR.name().equals(classRoomVO.getUserType())) {
                if (Objects.equals(cpCourse.getCrClassStatus(), 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            } else {
                if (Objects.equals(studentClassStatus, 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            }

            long currentTimeMillis = System.currentTimeMillis();
            ClassRoom classRoom = startClass(classRoomVO.getCourseUuid(), classRoomVO.getUserUuid(), classRoomVO.getUserType(), classRoomVO.getUserName(), classRoomVO.getAgoraUid(), currentTimeMillis);
            LOGGER.info("startClass endTime={}", currentTimeMillis);

            if (classRoom != null) {
                String commChannelId = classRoom.getCommChannelId();
                classRoomVO.setCommChannelId(commChannelId);
                classRoomVO.setSignallingChannelId(classRoom.getSignallingChannelId());
                classRoomVO.setRecordId(classRoom.getRecordId());
                classRoomVO.setSysTime(currentTimeMillis);
                String appId = classRoomVO.getAppId();
                String appCertificate = classRoomVO.getAppCertificate();
                String channel = commChannelId;
                int ts = Math.toIntExact(currentTimeMillis / 1000);
                int r = new Random().nextInt();
                long uid = classRoomVO.getAgoraUid();
                int expiredTs = 0;
                String channelKey = DynamicKey4.generateMediaChannelKey(appId, appCertificate, channel, ts, r, uid, expiredTs);
                classRoomVO.setChannelKey(channelKey);
                int expiredTsInSeconds = Math.toIntExact(currentTimeMillis / 1000 + 3600 * 12);
                String signalingKey = SignalingToken.getToken(appId, appCertificate, String.valueOf(uid), expiredTsInSeconds);
                classRoomVO.setSignalingKey(signalingKey);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return classRoomVO;
    }
    @Override
    public ClassRoomVO enterRoomPhp(ClassRoomVO classRoomVO,String phpToken) {
        Response phpResponse=null;
        try {

            String courseUuid=classRoomVO.getCourseUuid();
            String phpUrl =url.concat("client/enterRoom/check");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(phpToken));
            post.setHeader("Accept", "application/json");

            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();

            String result= EntityUtils.toString(entity,"UTF-8");
            LOGGER.info("client/enterRoom/check result ={}",result);

            phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            CourseDto cpCourse =null;
            if(object!=null){
                cpCourse=JSON.parseObject(object.toString(), CourseDto.class);
                LOGGER.info("client/enterRoom/check cpCourse ={}",JSON.toJSONString(cpCourse));
            }
            //LOGGER.info("classRoomVO ={}",JSON.toJSONString(classRoomVO));
            if(cpCourse.getInvalidCourse()){
                classRoomVO.setInvalid_course("INVALID_COURSE");
            }
            if(cpCourse.getCourseNoStart()){
                classRoomVO.setCourse_no_starat("COURSE_NO_STARAT");
            }
            String courseDate = cpCourse.getCourseDate();
            String endTime = cpCourse.getEndTime();
            ClassRoom room = classRoomMapper.selectByCourseUuid(classRoomVO.getCourseUuid());
            int overtime = 0;
            if (room != null) {
                overtime = room.getOvertime();
            }

            if (StringUtil.isNotStringNull(courseDate) && (DateUtils.parseDate(courseDate + " " + endTime, "yyyy-MM-dd HH:mm").getTime() + overtime * 60 * 1000) <= System.currentTimeMillis()) {
                if (!isExistPersonOfCourse(classRoomVO.getCourseUuid())) {
                    LOGGER.info("房间{}还有人", classRoomVO.getCourseUuid());
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            }
            if (TEACHER.name().equals(classRoomVO.getUserType())) {
                if (Objects.equals(cpCourse.getClassStatus(), 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            } else if (CC.name().equals(classRoomVO.getUserType())) {
                if (Objects.equals(cpCourse.getCcClassStatus(), 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            } else if (CR.name().equals(classRoomVO.getUserType())) {
                if (Objects.equals(cpCourse.getCrClassStatus(), 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            } else if(STUDENT.name().equals(classRoomVO.getUserType())){
                if (Objects.equals(cpCourse.getStudentClassStatus(), 1)) {
                    classRoomVO.setCourse_has_end("COURSE_HAS_END");
                }
            }

            long currentTimeMillis = System.currentTimeMillis();
            //LOGGER.info("startClassPhp...");
            ClassRoom classRoom = startClassPhp(classRoomVO.getCourseUuid(), classRoomVO.getUserUuid(), classRoomVO.getUserType(), classRoomVO.getUserName(), classRoomVO.getAgoraUid(), currentTimeMillis,phpToken);
            if (classRoom != null) {
                String commChannelId = classRoom.getCommChannelId();
                classRoomVO.setCommChannelId(commChannelId);
                classRoomVO.setSignallingChannelId(classRoom.getSignallingChannelId());
                classRoomVO.setRecordId(classRoom.getRecordId());
                classRoomVO.setSysTime(currentTimeMillis);
                String appId = classRoomVO.getAppId();
                String appCertificate = classRoomVO.getAppCertificate();
                String channel = commChannelId;
                int ts = Math.toIntExact(currentTimeMillis / 1000);
                int r = new Random().nextInt();
                long uid = classRoomVO.getAgoraUid();
                int expiredTs = 0;
                String channelKey = DynamicKey4.generateMediaChannelKey(appId, appCertificate, channel, ts, r, uid, expiredTs);
                classRoomVO.setChannelKey(channelKey);
                int expiredTsInSeconds = Math.toIntExact(currentTimeMillis / 1000 + 3600 * 12);
                String signalingKey = SignalingToken.getToken(appId, appCertificate, String.valueOf(uid), expiredTsInSeconds);
                //LOGGER.info("signalingKey={}",signalingKey);
                classRoomVO.setSignalingKey(signalingKey);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return classRoomVO;
    }

    protected boolean isExistPersonOfCourse(String courseUuid) {
        String values = redisService.get(PERSONOFCOURSE_KEY + courseUuid);
        if (StringUtils.isBlank(values) || Integer.parseInt(values) <= 0) {
            return false;
        }
        return true;
    }
}