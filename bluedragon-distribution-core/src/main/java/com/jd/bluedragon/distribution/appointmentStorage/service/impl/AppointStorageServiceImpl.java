package com.jd.bluedragon.distribution.appointmentStorage.service.impl;

import com.jd.bluedragon.distribution.appointmentStorage.domain.AppointStorageDto;
import com.jd.bluedragon.distribution.appointmentStorage.service.AppointStorageService;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @author: zhengchengfa
 * @Time: 2021/1/12  20:18
 */
@Service
public class AppointStorageServiceImpl implements AppointStorageService {

    @Override
    public List<AppointStorageDto> checkPackageTempStorage(String packageCode) {
        //查询吕远包裹预约暂存表中是否存在该包裹，存在标识暂存状态
        return null;
    }

    @Override
    public int updateTempStoragePackageInfo(AppointStorageDto appointStorageDto) {
        //装车发货时更新暂存包裹的状态为已发货
        return 0;
    }
}
