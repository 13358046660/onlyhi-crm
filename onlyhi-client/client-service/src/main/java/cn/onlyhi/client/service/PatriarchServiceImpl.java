package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.PatriarchMapper;
import cn.onlyhi.client.po.Patriarch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/6/5.
 */
@Service
public class PatriarchServiceImpl implements PatriarchService {

    @Autowired
    private PatriarchMapper patriarchMapper;

    @Override
    public int save(Patriarch patriarch) {

        return patriarchMapper.insertSelective(patriarch);
    }

    @Override
    public Patriarch findByphone(String phone) {
        return patriarchMapper.findByPhone(phone);
    }

    @Override
    public int updatePasswordByUuid(String patriarchUuid, String newPassword) {
        return patriarchMapper.updatePasswordByUuid(patriarchUuid, newPassword);
    }

    @Override
    public Patriarch findByUuid(String uuid) {
        return patriarchMapper.selectByUuid(uuid);
    }

    @Override
    public int update(Patriarch patriarch) {
        return patriarchMapper.updateByUuidSelective(patriarch);
    }
}
