package com.jd.bluedragon.distribution.label.service;

import com.jd.bluedragon.distribution.label.dao.LabelDao;
import com.jd.bluedragon.distribution.label.domain.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("labelService")
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelDao labelDao;
 
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(Label label) {
    	return labelDao.add(LabelDao.namespace, label);
	}
}
