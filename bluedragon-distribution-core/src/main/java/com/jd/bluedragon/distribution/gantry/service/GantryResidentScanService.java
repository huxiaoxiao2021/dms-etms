package com.jd.bluedragon.distribution.gantry.service;

import com.jd.bluedragon.distribution.gantry.domain.GantryResidentDto;

/**
 * 龙门架驻厂扫描接口
 *
 * @author: hujiping
 * @date: 2020/10/22 18:09
 */
public interface GantryResidentScanService {

    /**
     * 龙门架驻厂扫描处理逻辑
     * @param gantryResidentDto
     */
    void dealLogic(GantryResidentDto gantryResidentDto) throws Exception;
}
