package cn.onlyhi.client.service;


import cn.onlyhi.client.dto.LeadsExtendEntity;
import cn.onlyhi.client.po.Leads;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
public interface LeadsService {

    int save(Leads leads);

    Leads findByUuid(String uuid);

    /**
     * 根据uuid更新
     * @param leads
     * @return
     */
    int update(Leads leads);

    /**
     * 根据手机号查询leads信息
     *
     * @param phone
     * @return
     */
    Leads findLeadsByPhone(String phone);

    /**
     * 更新sex
     *
     * @param uuid
     * @param sex
     * @return
     */
    int updateSexByUuid(String uuid, Integer sex);

    /**
     * 更新年级
     *
     * @param uuid
     * @param grade
     * @return
     */
    int updateGradeByUuid(String uuid, String grade);

    /**
     * 更新高考地区
     *
     * @param uuid
     * @param examArea
     * @return
     */
    int updateExamAreaByUuid(String uuid, String examArea);

    int updateSubjectByUuid(String leadsUuid, String subject);
    /**
     * 取学生头像图像与基础信息
     *
     * @param phone
     * @return
     */
    LeadsExtendEntity selectLeadsByPhone(String phone);
}
