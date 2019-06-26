package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.UserNeteaseim;

public interface UserNeteaseimMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserNeteaseim record);

    UserNeteaseim selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserNeteaseim record);

    UserNeteaseim selectByUuid(String uuid);

    int updateByUuidSelective(UserNeteaseim record);

    UserNeteaseim findByUserUuid(String userUuid);

    int updateByUserUuid(UserNeteaseim record);
}