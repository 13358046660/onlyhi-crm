package cn.onlyhi.client.service;

import cn.onlyhi.client.po.Article;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface ArticleService {

	int save(Article article);

	Article findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param article
     * @return
     */
	int update(Article article);

	List<Article> findAllArticleByDeviceType(int deviceType);



}