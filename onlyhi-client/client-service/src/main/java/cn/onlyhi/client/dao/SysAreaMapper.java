package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.SysArea;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAreaMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(SysArea record);

    SysArea selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysArea record);

    List<SysArea> findByAreaLevel(Integer areaLevel);

    List<SysArea> findByAreaLevelAndParentCode(@Param("areaLevel") Integer areaLevel, @Param("parentCode") String parentCode);

    SysArea findByAreaCode(String areaCode);
}