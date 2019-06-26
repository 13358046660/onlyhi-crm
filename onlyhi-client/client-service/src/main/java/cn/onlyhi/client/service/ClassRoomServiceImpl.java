package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassRoomMapper;
import cn.onlyhi.client.dto.ClassRoomVO;
import cn.onlyhi.client.po.ClassRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author wqz
 * <p>
 * Created by wqz on 2017/6/22.
 */
@Service
public class ClassRoomServiceImpl implements ClassRoomService {

    @Autowired
    private ClassRoomMapper classRoomMapper;

    @Override
    public ClassRoom findByCourseUuid(String courseUuid) {
        return classRoomMapper.selectByCourseUuid(courseUuid);
    }

    @Override
    public int save(ClassRoom classRoom) {
        return classRoomMapper.insertSelective(classRoom);
    }

    @Override
    public ClassRoom findByUuid(String uuid) {
        return classRoomMapper.selectByUuid(uuid);
    }

    @Override
    public int update(ClassRoom classRoom) {
        return classRoomMapper.updateByUuidSelective(classRoom);
    }

    @Override
    public int updateByCourseUuid(ClassRoom classRoom) {
        return classRoomMapper.updateByCourseUuid(classRoom);
    }

    @Override
    public List<ClassRoom> findCourseException() {
        return classRoomMapper.findCourseException();
    }

    @Override
    public int updateOvertimeByCourseUuid(String courseUuid, int overtime) {
        return classRoomMapper.updateOvertimeByCourseUuid(courseUuid,overtime);
    }
    @Override
    public ClassRoom selectNormalRoom(String courseUuid) {
        return classRoomMapper.selectNormalRoom(courseUuid);
    }
    @Override
    public ClassRoom selectNormalRoomGold(String courseUuid) {
        return classRoomMapper.selectNormalRoomGold(courseUuid);
    }

    @Override
    public int updateByDate(ClassRoomVO classRoom) {
        return classRoomMapper.updateByDate(classRoom);
    }

    @Override
    public List<ClassRoomVO> selectByDate(ClassRoomVO classRoom) {
        return classRoomMapper.selectByDate(classRoom);
    }
}
