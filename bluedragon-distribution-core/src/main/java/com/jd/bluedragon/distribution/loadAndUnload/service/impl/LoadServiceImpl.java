package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-19 20:39
 */
@Service("loadService")
public class LoadServiceImpl implements LoadService {

    @Autowired
    private LoadCarDao loadCarDao;

    @Override
    public int deleteById(LoadDeleteReq req) {
        return loadCarDao.deleteById(req);
    }

    @Override
    public int insert(LoadCar detail) {
        return loadCarDao.insert(detail);
    }

    @Override
    public List<LoadTaskListDto> selectByIds(List<Long> list) {
        return loadCarDao.selectByIds(list);
    }

    @Override
    public LoadCar findLoadCarById(Long id) {
        return loadCarDao.findLoadCarByTaskId(id);
    }

    @Override
    public List<Long> selectByCreateUserErp(String loginUserErp) {
        return loadCarDao.selectByCreateUserErp(loginUserErp);
    }

    @Override
    public List<LoadCar> selectByEndSiteCode(LoadCar loadCar) {
        return loadCarDao.selectByEndSiteCode(loadCar);
    }

}
