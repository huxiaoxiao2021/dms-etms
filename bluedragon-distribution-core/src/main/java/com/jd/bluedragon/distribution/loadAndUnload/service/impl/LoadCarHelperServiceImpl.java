package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxServiceImpl;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCarHelper;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarHelperDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadCarHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-19 20:22
 */
@Service("loadCarHelperService")
public class LoadCarHelperServiceImpl implements LoadCarHelperService {

    private static final Logger log = LoggerFactory.getLogger(LoadCarHelperServiceImpl.class);

    @Autowired
    private LoadCarHelperDao loadCarHelperDao;

    @Override
    public int batchInsert(List<LoadCarHelper> dataList) {
        log.info("添加协助人接口入库参数={}", JSON.toJSONString(dataList));
        return loadCarHelperDao.batchInsert(dataList);
    }

    @Override
    public List<Long> selectByCreateUserErp(String loginUserErp) {
        return loadCarHelperDao.selectByCreateUserErp(loginUserErp);
    }

    @Override
    public List<Long> selectByHelperErp(String loginUserErp) {
        return loadCarHelperDao.selectByHelperErp(loginUserErp);
    }

    public List<Long> selectIdsByErp(String loginUserErp) {
        return loadCarHelperDao.selectIdsByErp(loginUserErp);
    }

    @Override
    public int deleteById(Long taskId) {
        return loadCarHelperDao.deleteById(taskId);
    }


}
