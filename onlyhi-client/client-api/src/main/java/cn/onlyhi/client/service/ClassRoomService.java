package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.ClassRoomVO;
import cn.onlyhi.client.po.ClassRoom;

import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi
 */
public interface ClassRoomService {

    ClassRoom findByCourseUuid(String courseUuid);

    int save(ClassRoom classRoom);

    ClassRoom findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param classRoom
     * @return
     */
    int update(ClassRoom classRoom);

    /**
     * 根据courseUuid更新
     *
     * @param classRoom
     * @return
     */
    int updateByCourseUuid(ClassRoom classRoom);

    /**
     * 未正常结束的课程
     *
     * @return
     */
    List<ClassRoom> findCourseException();

    int updateOvertimeByCourseUuid(String courseUuid, int overtime);
    ClassRoom selectNormalRoom(String uuid);
    ClassRoom selectNormalRoomGold(String uuid);
    /**
     * 根据courseUuid更新
     *
     * @param classRoom
     * @return
     */
    int updateByDate(ClassRoomVO classRoom);
    List<ClassRoomVO> selectByDate(ClassRoomVO classRoom);
}
