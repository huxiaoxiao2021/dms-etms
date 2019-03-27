package com.jd.bluedragon.distribution.residentPrint.service;

/**
 * @ClassName: ResidentService
 * @Description: 驻场打印接口
 * @author: hujiping
 * @date: 2019/3/27 17:14
 */
public interface ResidentService {


    /**
     * 判断运单是否存在箱号中
     * @param boxCode
     * @param waybillCode
     * @return
     */
    Boolean isExist(String boxCode, String waybillCode);
}
