package cn.onlyhi.client.dao;


import cn.onlyhi.client.dto.ClassRoomVO;
import cn.onlyhi.client.po.ClassRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassRoomMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClassRoom record);

    ClassRoom selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClassRoom record);

    ClassRoom selectByUuid(String uuid);

    int updateByUuidSelective(ClassRoom record);

    ClassRoom selectByCourseUuid(String courseUuid);

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

    int updateOvertimeByCourseUuid(@Param("courseUuid") String courseUuid, @Param("overtime") int overtime);
    /**
     * 测试与线上回放列表不同
     * 测试用
     * 只返回房间统计状态为已统计，轨迹路径，时长正常的
     * @return
     */
    ClassRoom selectNormalRoom(String courseUuid);
    /**
     * 测试与线上回放列表不同
     * 线上用
     * 只返回房间统计状态为已统计，轨迹路径，时长正常的
     * @return
     */
    ClassRoom selectNormalRoomGold(String courseUuid);
    /**
     * 根据开始结束日期更新
     *
     * @param classRoom
     * @return
     */
    int updateByDate(ClassRoomVO classRoom);
    List<ClassRoomVO> selectByDate(ClassRoomVO classRoom);
}