package com.jd.bluedragon.distribution.residentPrint.service;

import java.util.List;

/**
 * @ClassName: ResidentService
 * @Description: 驻场打印接口
 * @author: hujiping
 * @date: 2019/3/27 17:14
 */
public interface ResidentService {


    /**
     * 获得箱号中所有运单号
     * @param boxCode
     * @return
     */
    List<String> getAllWaybillCodeByBoxCode(String boxCode);
}
