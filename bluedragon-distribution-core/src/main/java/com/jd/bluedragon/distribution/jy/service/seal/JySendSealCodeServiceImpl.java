package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.distribution.jy.dao.send.JySendSealCodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class JySendSealCodeServiceImpl implements JySendSealCodeService{
    @Autowired
    JySendSealCodeDao jySendSealCodeDao;

    @Override
    public List<String> selectSealCodeByBizId(String bizId) {
        return jySendSealCodeDao.selectSealCodeByBizId(bizId);
    }
}
