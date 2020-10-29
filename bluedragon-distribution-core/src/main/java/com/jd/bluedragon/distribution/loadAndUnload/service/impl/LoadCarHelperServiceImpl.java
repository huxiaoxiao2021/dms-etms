package com.jd.bluedragon.distribution.loadAndUnload.service.impl;


import com.jd.bluedragon.distribution.loadAndUnload.LoadCarHelper;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarHelperDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadCarHelperService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private LoadCarDao loadCarDao;

    @Override
    public int batchInsert(List<LoadCarHelper> dataList) {
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

    @Override
    public int deleteById(Long taskId) {
        return loadCarHelperDao.deleteById(taskId);
    }

    @Override
    public List<String> selectCreateUserErpByTaskId(Long taskId) {
        return loadCarHelperDao.selectCreateUserErpByTaskId(taskId);
    }

    @Override
    public List<String> selectHelperErpByTaskId(Long taskId) {
        return loadCarHelperDao.selectHelperErpByTaskId(taskId);
    }

    @Override
    public Boolean checkUserPermission(Long taskId, String erp) {
        if (null == taskId || StringUtils.isBlank(erp)) {
            return false;
        }
        List<String> creatorList = loadCarHelperDao.selectCreateUserErpByTaskId(taskId);
        List<String> helperList = loadCarHelperDao.selectHelperErpByTaskId(taskId);
        List<String> taskCreatorList = loadCarDao.selectCreateUserErpByTaskId(taskId);
        if (CollectionUtils.isNotEmpty(creatorList) && creatorList.contains(erp)) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(helperList) && helperList.contains(erp)) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(taskCreatorList) && taskCreatorList.contains(erp)) {
            return true;
        }
        return false;
    }

}
