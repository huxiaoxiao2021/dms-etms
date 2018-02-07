package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.preseparate.vo.BaseResponseIncidental;
import com.jd.preseparate.vo.MediumStationOrderInfo;
import com.jd.preseparate.vo.OriginalOrderInfo;

/**
 * Created by wangtingwei on 2015/10/28.
 */
public interface PreseparateWaybillManager {

    /**
     * 获取预分拣站编号
     * @param waybillCode 运单号
     * @return 站点编号
     * @throws Exception
     */
    Integer getPreseparateSiteId(String waybillCode) throws Exception;
    
	/**
	 * 根据原站点和上传的称重信息获取新预分拣站点
	 * @param originalOrderInfo 原站点及运单+重量信息
	 * @return
	 */
	JdResult<BaseResponseIncidental<MediumStationOrderInfo>> getMediumStation(OriginalOrderInfo originalOrderInfo);
}
