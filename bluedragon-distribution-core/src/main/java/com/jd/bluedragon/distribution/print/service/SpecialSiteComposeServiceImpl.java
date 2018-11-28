package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangtingwei on 2015/12/24.
 */
@Service("specialSiteComposeService")
public class SpecialSiteComposeServiceImpl implements ComposeService {

    private static final Log logger = LogFactory.getLog(SpecialSiteComposeServiceImpl.class);

    private static Integer PREPARE_SITE_CODE_NOTHING = 0;
    private static String PREPARE_SITE_NAME_NOTHING = "未定位门店";

    private static Integer PREPARE_SITE_CODE_OVER_LINE = -100;
    private static String PREPARE_SITE_NAME_OVER_LINE = "超区分界线";

    private static Integer PREPARE_SITE_CODE_OVER_AREA = -2;
    private static String PREPARE_SITE_NAME_OVER_AREA = "超区";

    private static Integer PREPARE_SITE_CODE_EMS_DIRECT = 999999999;
    private static String PREPARE_SITE_NAME_EMS_DIRECT = "EMS全国直发";

    @Autowired
    private BaseService baseService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        if(null!=targetSiteCode&&targetSiteCode>0){
            waybill.setPrepareSiteCode(targetSiteCode);
        }

        //新通路订单预分拣站点替换为代配站点（运单中的backupSiteId字段）
        if(BusinessHelper.isNewPathWay(waybill.getSendPay())){
            BaseEntity<BigWaybillDto> baseEntity =  waybillQueryManager.getWaybillDataForPrint(waybill.getWaybillCode());

            if(baseEntity != null && baseEntity.getData() != null
                    && baseEntity.getData().getWaybill() != null
                    && baseEntity.getData().getWaybill().getWaybillExt() != null) {

                Integer backupSiteId = baseEntity.getData().getWaybill().getWaybillExt().getBackupSiteId();
                waybill.setPrepareSiteCode(backupSiteId);
                BaseStaffSiteOrgDto site = baseService.getSiteBySiteID(backupSiteId);
                if (null != site) {
                    waybill.setPrepareSiteName(site.getSiteName());
                }
            }
        }

        //超区
        if (PREPARE_SITE_CODE_OVER_AREA.equals(waybill.getPrepareSiteCode())) {
            waybill.setPrepareSiteCode(PREPARE_SITE_CODE_OVER_AREA);
            waybill.setPrepareSiteName(PREPARE_SITE_NAME_OVER_AREA);
            logger.warn(" 没有获取预分拣站点(-2超区),"+waybill.getWaybillCode());
            //未定位门店
        } else if(waybill.getPrepareSiteCode()==null
                || (waybill.getPrepareSiteCode()<=PREPARE_SITE_CODE_NOTHING
                    && waybill.getPrepareSiteCode() > PREPARE_SITE_CODE_OVER_LINE)
                ){
            waybill.setPrepareSiteCode(PREPARE_SITE_CODE_NOTHING);
            waybill.setPrepareSiteName(PREPARE_SITE_NAME_NOTHING);
            logger.warn(" 没有获取预分拣站点(未定位门店),"+waybill.getWaybillCode());
        } else if(waybill.getPrepareSiteCode() !=null
                && waybill.getPrepareSiteCode().intValue() < PREPARE_SITE_CODE_OVER_LINE){
            //新细分超区
            waybill.setPrepareSiteCode(waybill.getPrepareSiteCode());
            waybill.setPrepareSiteName(PREPARE_SITE_NAME_OVER_AREA);
            logger.warn(" 没有获取预分拣站点(细分超区)," + waybill.getPrepareSiteCode() + ","+waybill.getWaybillCode());
        }

        //EMS全国直发
        if(waybill.getPrepareSiteCode()!=null
                && waybill.getPrepareSiteCode().equals(PREPARE_SITE_CODE_EMS_DIRECT)){
            waybill.setPrepareSiteName(PREPARE_SITE_NAME_EMS_DIRECT);
        }


        if(null==waybill.getPrepareSiteName()&&null!=waybill.getPrepareSiteCode()){
            BaseStaffSiteOrgDto site= baseService.getSiteBySiteID(waybill.getPrepareSiteCode());
            if(null!=site){
                waybill.setPrepareSiteName(site.getSiteName());
            }
        }
    }
}
