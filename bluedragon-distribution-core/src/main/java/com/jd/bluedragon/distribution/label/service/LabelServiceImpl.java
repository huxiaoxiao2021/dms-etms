package com.jd.bluedragon.distribution.label.service;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.jd.bluedragon.distribution.label.dao.LabelDao;
import com.jd.bluedragon.distribution.label.domain.Label;

@Service("labelService")
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelDao labelDao;
 
    @Profiled(tag = "LabelService.add")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(Label label) {
    	return labelDao.add(LabelDao.namespace, label);
	}
}
