package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import javax.annotation.Resource;

/**
 * 城配订单发货验证
 * Created by wangtingwei on 2017/4/26.
 * 验证处理：
 *        原则：针对城配箱子及原包，其它类型一概通过。
 *        流程：
 *             1：原包发货，只能发往分拣中心(即站点类型为64)
 *             2：箱子发货，同一派车单下的订单需要装入同一箱子中，才能发货
 */
public class CityDeliveryVerification implements DeliveryVerification{

    private static final Integer SITE_TYPE_FOR_SORTING_CENTER=Integer.valueOf(64);

    @Resource(name = "baseService")
    private BaseService baseService;

    30142 iceImpl 平台称重运单号为VC34270530405重量为0.12

    /**
     * 城配验证
     * @param       boxCode                 箱号
     * @param       receiveSiteCode         收货站点
     * @return
     */
    @Override
    public VerificationResult verification(String boxCode, Integer receiveSiteCode) {
        VerificationResult result=new VerificationResult();
        if(SerialRuleUtil.isMatchAllPackageNo(boxCode)
                &&isCityDistributionWaybill(SerialRuleUtil.getWaybillCode(boxCode))){
            BaseStaffSiteOrgDto site= baseService.getSiteBySiteID(receiveSiteCode);
            if(null!=site&&!SITE_TYPE_FOR_SORTING_CENTER.equals(site.getSiteType())){
                result.setCode(false);
            }
        }

        if(SerialRuleUtil.isMatchBoxCode(boxCode)&&isCityDistributionBox(boxCode)){
            /****send_d与派车单下运单进行对比，当一致时，才能通过*/
        }
        return result;
    }


    private boolean isCityDistributionWaybill(String waybillCode){
        return false;
    }


    private boolean isCityDistributionBox(String boxCode){
        return false;
    }
}
