package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ArticleMapper;
import cn.onlyhi.client.po.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private ArticleMapper articleMapper;

	@Override
	public int save(Article article) {
		return articleMapper.insertSelective(article);
	}

	@Override
	public Article findByUuid(String uuid) {
		return articleMapper.selectByUuid(uuid);
	}

	@Override
	public int update(Article article) {
		return articleMapper.updateByUuidSelective(article);
	}

	@Override
	public List<Article> findAllArticleByDeviceType(int deviceType) {
		return articleMapper.findAllArticleByDeviceType(deviceType);
	}

}