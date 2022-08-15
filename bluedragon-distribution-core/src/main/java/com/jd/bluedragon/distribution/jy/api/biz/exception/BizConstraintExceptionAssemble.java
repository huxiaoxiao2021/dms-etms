package com.jd.bluedragon.distribution.jy.api.biz.exception;

import com.jd.bluedragon.distribution.jy.api.BizConstraintAssemble;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dto.BizTaskConstraint;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BizConstraintExceptionAssemble implements BizConstraintAssemble {

    @Autowired
    private JyBizTaskExceptionDao bizTaskExceptionDao;

    @Override
    public BizTaskConstraint bizConstraintAssemble(Long bizId) {
        JyBizTaskExceptionEntity e = bizTaskExceptionDao.findByBizId(bizId.toString());
        BizTaskConstraint bizConstraint = new BizTaskConstraint();
        bizConstraint.setSiteCode(e.getSiteCode());
        bizConstraint.setFloor(e.getFloor());
        bizConstraint.setGridCode(e.getGridCode());
        return bizConstraint;
    }
}
