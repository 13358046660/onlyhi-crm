package cn.onlyhi.client.service;


import cn.onlyhi.client.dto.CoursewareDto;
import cn.onlyhi.client.po.Courseware;
import cn.onlyhi.client.po.CoursewareImage;

import java.util.List;
import java.util.Map;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/24.
 */
public interface CoursewareService {

    int save(Courseware courseware);

    Courseware findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param courseware
     * @return
     */
    int update(Courseware courseware);

    /**
     * 根据教师Id获取课件
     *
     * @param teacherUuid
     * @return
     */
    List<Courseware> findByTeacherUuid(String teacherUuid);

    /**
     * 获取教师的课件列表并进行排序
     *
     * @param teacherUuid
     * @param orderBy     排序字段
     * @param orderSort   asc desc
     * @return
     */
    List<Courseware> findByTeacherUuid(String teacherUuid, String orderBy, String orderSort);

    /**
     * 系统课件总数
     *
     * @return
     */
    long countSysCourseware(Map paramMap);

    /**
     * 获取系统课件列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<CoursewareDto> findSysCourseware(Map paramMap, int pageNo, int pageSize);

    /**
     * 根据课件Id获取课件图片
     *
     * @param coursewareId
     * @return
     */
    List<CoursewareImage> findImageByCoursewareId(String coursewareId);


    /**
     * 删除课件
     *
     * @param coursewareId
     * @return
     */
    int deleteCourseware(String coursewareId);

    /**
     * 教师课件总数
     *
     * @return
     */
    long countTeacherCouresewares(Map paramMap);

    /**
     * 相同文件名列表
     *
     * @param fileName    文件名
     * @param suffix      后缀名
     * @param teacherUuid 教师uuid
     * @return 文件名列表
     */
    List<String> findByCoursewareName(String fileName, String suffix, String teacherUuid);

    List<Courseware> findByCoursewareDirUuid(String coursewareDirUuid, String orderBy, String orderSort);

    List<Courseware> findRootByTeacherUuid(String teacherUuid, String orderBy, String orderSort);

    int updateCoursewareDir(String coursewareUuid, String coursewareDirUuid);

    int deleteDirAndCourseware(String coursewareDirUuid);

    List<Courseware> findLikeByCoursewareName(String teacherUuid, String coursewareName);

    Courseware findByCoursewareName(String teacherUuid, String coursewareName);

    List<CoursewareDto> findTeacherCouresewaresByCreateTime(String startDate, String endDate);

    int selectByMD5(String teacherUuid, String streamMD5);
}
