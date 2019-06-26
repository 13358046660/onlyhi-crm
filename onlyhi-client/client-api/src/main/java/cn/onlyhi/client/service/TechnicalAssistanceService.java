package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TechnicalAssistance;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TechnicalAssistanceService {

	int save(TechnicalAssistance technicalAssistance);

	TechnicalAssistance findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param technicalAssistance
     * @return
     */
	int update(TechnicalAssistance technicalAssistance);


	List<TechnicalAssistance> findByAssistanceUuidAndAssistanceStatus(String assistanceUuid, int assistanceStatus);
}