package com.jd.bluedragon.distribution.spare.service;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.spare.dao.SpareSaleDao;
import com.jd.bluedragon.distribution.spare.domain.SpareSale;

@Service("spareSaleService")
public class SpareSaleServiceImpl implements SpareSaleService {
    
//    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private SpareSaleDao spareSaleDao;
    
    @Override
    @Profiled(tag = "SpareSaleService.addOrUpdate")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int addOrUpdate(SpareSale spareSale) {
        Assert.notNull(spareSale, "spareSale must not be null");
        if (this.spareSaleDao.update(SpareSaleDao.namespace, spareSale) <= 0) {
        	this.spareSaleDao.add(SpareSaleDao.namespace, spareSale);
        }
        return Constants.RESULT_SUCCESS;
    }
}
