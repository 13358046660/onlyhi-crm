package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.SysEnum;

import java.util.List;

public interface SysEnumMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(SysEnum record);

    SysEnum selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysEnum record);

    SysEnum selectByUuid(String uuid);

    int updateByUuidSelective(SysEnum record);

    List<SysEnum> findByEnumType(String enumType);
}