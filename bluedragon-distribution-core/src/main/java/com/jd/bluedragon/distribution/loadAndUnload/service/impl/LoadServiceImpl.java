package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-19 20:39
 */
@Service("loadServiceImpl")
public class LoadServiceImpl implements LoadService {

    private static final Logger log = LoggerFactory.getLogger(LoadServiceImpl.class);

    @Autowired
    private LoadCarDao loadCarDao;

    @Override
    public int deleteById(LoadDeleteReq req) {
        return loadCarDao.deleteById(req);
    }

    @Override
    public int insert(LoadCar detail) {
        log.info("创建装车任务入库参数={}", JSON.toJSONString(detail));
        return loadCarDao.insert(detail);
    }

    @Override
    public List<LoadTaskListDto> selectByIds(List<Long> list) {
        log.info("查询装车任务列表taskIds={}", JSON.toJSONString(list));
        return loadCarDao.selectByIds(list);
    }
}
