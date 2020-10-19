package com.jd.bluedragon.distribution.loadAndUnload.service;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadTaskListDto;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-19 20:39
 */
public interface LoadService {

    int deleteById(LoadDeleteReq req);

    int insert(LoadCar detail);

    List<LoadTaskListDto> selectByIds(List<Long> list);
}
