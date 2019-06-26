package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TcTeacherFileMapper;
import cn.onlyhi.client.dao.TcTeacherMapper;
import cn.onlyhi.client.dto.SaveTeacherInfoDto;
import cn.onlyhi.client.dto.TeacherDto;
import cn.onlyhi.client.po.TcTeacher;
import cn.onlyhi.client.po.TcTeacherFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.onlyhi.common.util.ClientUtil.getTeacherOssKey;
import static cn.onlyhi.common.util.ClientUtil.getTeacherOssSaveName;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TcTeacherServiceImpl implements TcTeacherService {

    @Autowired
    private TcTeacherMapper tcTeacherMapper;
    @Autowired
    private TcTeacherFileMapper tcTeacherFileMapper;

    @Override
    public int save(TcTeacher tcTeacher) {
        return tcTeacherMapper.insertSelective(tcTeacher);
    }

    @Override
    public TcTeacher findByUuid(String uuid) {
        return tcTeacherMapper.selectByUuid(uuid);
    }

    @Override
    public int update(TcTeacher tcTeacher) {
        return tcTeacherMapper.updateByUuidSelective(tcTeacher);
    }

    @Override
    public TcTeacher findTeacherByPhone(String phone) {
        return tcTeacherMapper.selectByPhone(phone);
    }

    @Override
    public int updatePasswordByUuid(String uuid, String password) {
        return tcTeacherMapper.updatePasswordByUuid(uuid, password);
    }

    @Override
    public TeacherDto findInfoByUuid(String teacherUuid) {
        return tcTeacherMapper.findInfoByUuid(teacherUuid);
    }

    @Transactional
    @Override
    public int infoSave(SaveTeacherInfoDto dto) {
        String teacherUuid = dto.getTeacherUuid();
        //更新教师信息
        TcTeacher teacher = tcTeacherMapper.selectByUuid(teacherUuid);
        if (teacher == null) {
            teacher = new TcTeacher();
        }
        teacher.setUuid(teacherUuid);
        teacher.setTcName(dto.getTeacherName());
        teacher.setSex(dto.getSex());
        teacher.setPhone(dto.getPhone());
        teacher.setWechat(dto.getWechat());
        teacher.setQq(dto.getQq());
        teacher.setEmail(dto.getEmail());
        teacher.setEmergencyPhone(dto.getEmergencyPhone());
        teacher.setEmergencyRelation(dto.getEmergencyRelation());
        teacher.setProvince(dto.getProvinceCode());
        teacher.setCity(dto.getCityCode());
        teacher.setDistrict(dto.getDistrictCode());
        teacher.setArtsOrScience(dto.getArtsOrScience());
        teacher.setSchoolLocation(dto.getTeacherSchoolUuid());
        teacher.setEducation(dto.getEducation());
        teacher.setHighestEducation(dto.getHighestEducation());
        teacher.setMajor(dto.getMajor());
        teacher.setGrade(dto.getGrade());
        teacher.setGradePreference(dto.getGradePreferenceValue());
        teacher.setTeachingSubject(dto.getTeachingSubjectUuid());
        teacher.setSecondSubject(dto.getSecondSubjectUuid());
        teacher.setThirdSubject(dto.getThirdSubjectUuid());
        teacher.setIdNumber(dto.getIdNumber());
        teacher.setCardNumber(dto.getCardNumber());
        teacher.setBankAddress(dto.getBankAddress());
        teacher.setUpdateUserId(teacherUuid);
        tcTeacherMapper.updateByUuidSelective(teacher);

        //保存教师头像、证件等图片信息
        //存在先删除
        Long teacherId = teacher.getId();
        List<TcTeacherFile> tcTeacherFileList = tcTeacherFileMapper.findByTeacherId(teacherId);
        if (tcTeacherFileList != null && tcTeacherFileList.size() > 0) {
            tcTeacherFileMapper.deleteByTeacherId(teacherId);
        }
        List<TcTeacherFile> teacherFileList = dto.getTeacherFileList();
        for (TcTeacherFile teacherFile : teacherFileList) {
            String ossUrl = teacherFile.getFileAddress();
            teacherFile.setTeacherId(teacherId);
            teacherFile.setCreateUserId(teacherUuid);
            teacherFile.setOssKey(getTeacherOssKey(ossUrl));
            teacherFile.setFileName(getTeacherOssSaveName(ossUrl));
        }
        int i = tcTeacherFileMapper.batchSave(teacherFileList);
        return i;
    }

}