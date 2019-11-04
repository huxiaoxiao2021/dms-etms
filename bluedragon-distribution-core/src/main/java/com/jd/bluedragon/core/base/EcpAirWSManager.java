package com.jd.bluedragon.core.base;

import com.jd.tms.ecp.dto.AirDepartInfoDto;

/**
 * 运输航空相关
 * @author : xumigen
 * @date : 2019/11/4
 */
public interface EcpAirWSManager {

    /**
     * 分拣发货登记提交接口
     * @param param
     * @return
     */
    boolean submitSortAirDepartInfo(AirDepartInfoDto param) ;

    /**
     * 分拣发货登记补交提交接口
     * @param param
     * @return
     */
    boolean supplementSortAirDepartInfo(AirDepartInfoDto param) ;
}
