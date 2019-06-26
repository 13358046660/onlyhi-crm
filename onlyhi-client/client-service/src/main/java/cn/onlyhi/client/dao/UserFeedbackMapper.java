package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.UserFeedback;

public interface UserFeedbackMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserFeedback record);

    UserFeedback selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserFeedback record);

    UserFeedback selectByUuid(String uuid);

    int updateByUuidSelective(UserFeedback record);
}