package com.jd.bluedragon.distribution.appointmentStorage.service;

import com.jd.bluedragon.distribution.appointmentStorage.domain.AppointStorageDto;

import java.util.List;

/*
 * @author: zhengchengfa
 * @Time: 2021/1/12  20:18
 */
public interface AppointStorageService {
    /**
     * 校验是否为暂存包裹
     * @param packageCode  包裹号
     * @return
     */
    List<AppointStorageDto> checkPackageTempStorage(String packageCode);
    /**
     * 更新暂存包裹信息
      * @param appointStorageDto
     * @return
     */
    int updateTempStoragePackageInfo(AppointStorageDto appointStorageDto);
}
