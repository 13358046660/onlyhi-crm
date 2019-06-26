package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TcTablet;

public interface TcTabletMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TcTablet record);

    TcTablet selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TcTablet record);

    TcTablet selectByUuid(String uuid);

    int updateByUuidSelective(TcTablet record);

    TcTablet findByPhone(String phone);
}