package cn.onlyhi.client.service;

import cn.onlyhi.client.po.AgoraCallLog;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface AgoraCallLogService {

	int save(AgoraCallLog agoraCallLog);

	AgoraCallLog findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param agoraCallLog
     * @return
     */
	int update(AgoraCallLog agoraCallLog);

	
}