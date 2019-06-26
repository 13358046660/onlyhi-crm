package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TcPayGradeMapper;
import cn.onlyhi.client.po.TcPayGrade;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TcPayGradeServiceImpl implements TcPayGradeService {

	@Autowired
	private TcPayGradeMapper tcPayGradeMapper;

	@Override
	public int save(TcPayGrade tcPayGrade) {
		return tcPayGradeMapper.insertSelective(tcPayGrade);
	}

	@Override
	public TcPayGrade findById(Long id) {
		return tcPayGradeMapper.selectByPrimaryKey(id);
	}

}