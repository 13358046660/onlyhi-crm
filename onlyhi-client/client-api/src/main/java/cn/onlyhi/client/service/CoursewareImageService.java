package cn.onlyhi.client.service;

import cn.onlyhi.client.po.CoursewareImage;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface CoursewareImageService {

	int save(CoursewareImage coursewareImage);

	CoursewareImage findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param coursewareImage
     * @return
     */
	int update(CoursewareImage coursewareImage);


	int batchSave(List<CoursewareImage> coursewareImageList);

	List<CoursewareImage> findNoMd5();
}