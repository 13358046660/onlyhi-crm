package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.Article;

import java.util.List;

public interface ArticleMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Article record);

    Article selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Article record);

    Article selectByUuid(String uuid);

    int updateByUuidSelective(Article record);

    List<Article> findAllArticleByDeviceType(int deviceType);

}